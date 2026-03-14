package com.factorcraft.module.factor;

import com.factorcraft.module.event.FactorChangeEvent;
import com.factorcraft.module.event.FactorDisasterEvent;
import com.factorcraft.module.event.FactorThresholdEvent;
import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.event.FactorTideEvent;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.factor.api.FactorApi;
import com.factorcraft.module.factor.state.DayTierSnapshot;
import com.factorcraft.module.factor.state.EventCooldownState;
import com.factorcraft.module.factor.state.FactorWorldState;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factor 系统运行时服务
 * 
 * 核心功能：
 * - 实时 Factor 更新（基于潮汐系统）
 * - 日切结算（Tier 变更）
 * - 阈值事件 / 灾害冷却
 * - 区块级 Factor 扩散
 * 
 * 维度基准值体系：
 * - 主世界：0.5（基准）
 * - 下界：1.5（高活性）
 * - 末地：3.0（超高活性）
 */
public final class FactorService implements FactorApi {
    
    private static FactorService instance;
    
    public static FactorService getInstance() {
        if (instance == null) {
            instance = (FactorService) com.factorcraft.module.factor.api.FactorApiProvider.get();
        }
        return instance;
    }
    
    // ==================== 常量 ====================
    
    private static final long WORLD_DAY_TICKS = 24_000;
    private static final double CHANGE_EVENT_EPSILON = 0.0001;
    private static final double SLOPE_EPSILON = 0.00001;
    
    // 阻尼系数（相对于基准值的比例）
    private static final double DAMPING_RATIO = 0.04;
    
    // 趋势权重
    private static final double TREND_WEIGHT = 0.35;
    
    // 迟滞阈值（偏离度）
    private static final double HYSTERESIS = 0.1;

    private static final long DISASTER_COOLDOWN_TICKS = WORLD_DAY_TICKS;
    private static final String DISASTER_EVENT_ID = "factor_disaster";
    private static final int DISASTER_BASE_SEVERITY = 2;
    private static final int DISASTER_MAX_SEVERITY = 5;

    // 噪声参数（相对于基准值的比例）
    private static final double NOISE_DIMENSION_MULTIPLIER = 31.0;
    private static final double NOISE_TICK_MULTIPLIER = 17.0;
    private static final double NOISE_SINE_SCALE = 0.01;
    private static final double NOISE_RATIO = 0.02; // 噪声幅度 = 基准值 × 2%
    
    // 潮汐效果检查间隔 (1200 ticks = 60秒)
    private static final long TIDE_EFFECT_INTERVAL = 1200;

    // ==================== 状态存储 ====================

    private final Map<String, RuntimeState> states = new ConcurrentHashMap<>();
    private final Map<String, DayTierSnapshot> snapshots = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    // ==================== 核心逻辑 ====================

    public void tick(ServerWorld world) {
        String dimensionKey = world.getRegistryKey().getValue().toString();
        long tick = world.getTime();
        long day = tick / WORLD_DAY_TICKS;
        
        DimensionType dimensionType = DimensionType.fromKey(dimensionKey);
        RuntimeState state = states.computeIfAbsent(dimensionKey, 
            k -> new RuntimeState(dimensionType, day));

        state.expireOffsets(tick);

        double previousFactor = state.currentFactor;
        
        // 使用 DimensionType 计算潮汐变化
        double tideDelta = calculateTideDelta(dimensionType, tick);
        double playerDelta = state.activeOffsetTotal();
        double randomDelta = pseudoNoise(dimensionKey, tick, dimensionType.baseValue());

        // 阻尼：使 Factor 趋向基准值
        double damping = DAMPING_RATIO * (previousFactor - state.baseFactor);
        double nextFactor = previousFactor + tideDelta + playerDelta + randomDelta - damping;
        
        // 使用维度的 Factor 范围进行 clamp
        state.currentFactor = clamp(nextFactor, dimensionType.getMinFactor(), dimensionType.getMaxFactor());

        state.dayFactorSum += state.currentFactor;
        state.daySampleCount++;
        state.lastUpdatedTick = tick;

        // Factor 变化事件
        if (Math.abs(state.currentFactor - previousFactor) > CHANGE_EVENT_EPSILON) {
            SimpleFactorEventBus.getInstance().publish(
                new FactorChangeEvent(world, previousFactor, state.currentFactor));
        }
        
        // 潮汐效果检查（每 60 秒）
        if (tick % TIDE_EFFECT_INTERVAL == 0) {
            checkTideEffects(world, state, dimensionType);
        }

        // 日切结算
        if (tick % WORLD_DAY_TICKS == 0 && day > state.lastSettledDay) {
            settleDay(world, state, day, dimensionType);
        }
    }
    
    /**
     * 计算潮汐变化量（使用 DimensionType）
     */
    private double calculateTideDelta(DimensionType type, long tick) {
        double currentTide = type.calculateFactor(tick);
        double nextTide = type.calculateFactor(tick + 1);
        return nextTide - currentTide;
    }
    
    /**
     * 检查并触发潮汐效果
     */
    private void checkTideEffects(ServerWorld world, RuntimeState state, DimensionType type) {
        double currentFactor = state.currentFactor;
        double deviation = calculateDeviation(currentFactor, type.baseValue());
        TideStatus status = getTideStatus(deviation);
        
        // 发布潮汐事件（供其他模块监听）
        SimpleFactorEventBus.getInstance().publish(
            new FactorTideEvent(world, currentFactor, deviation, status, type));
        
        // 更新状态记录
        state.lastTideStatus = status;
    }

    // ==================== 潮汐相关 API ====================
    
    /**
     * 计算偏离基准值的百分比
     */
    public double calculateDeviation(double currentFactor, double baseValue) {
        if (baseValue == 0) return 0;
        return (currentFactor - baseValue) / baseValue;
    }
    
    /**
     * 根据偏离度获取潮汐状态
     */
    public TideStatus getTideStatus(double deviation) {
        double absDeviation = Math.abs(deviation);
        
        if (absDeviation <= 0.1) {
            return TideStatus.STABLE;
        } else if (absDeviation <= 0.3) {
            return TideStatus.DEVIATED;
        } else if (absDeviation <= 0.5) {
            return TideStatus.FLUCTUATING;
        } else {
            return TideStatus.VOLATILE;
        }
    }
    
    /**
     * 获取世界的潮汐状态
     */
    public TideStatus getTideStatus(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        if (state == null) return TideStatus.STABLE;
        
        DimensionType type = DimensionType.fromKey(key);
        double deviation = calculateDeviation(state.currentFactor, type.baseValue());
        return getTideStatus(deviation);
    }
    
    /**
     * 获取当前偏离度
     */
    public double getDeviation(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        if (state == null) return 0;
        
        DimensionType type = DimensionType.fromKey(key);
        return calculateDeviation(state.currentFactor, type.baseValue());
    }
    
    /**
     * 预测下一个潮汐峰值 tick
     */
    public long getNextPeakTick(ServerWorld world) {
        DimensionType type = DimensionType.fromKey(world.getRegistryKey().getValue().toString());
        return TideSystem.findNextPeakTick(type, world.getTime());
    }
    
    /**
     * 预测下一个潮汐谷值 tick
     */
    public long getNextTroughTick(ServerWorld world) {
        DimensionType type = DimensionType.fromKey(world.getRegistryKey().getValue().toString());
        return TideSystem.findNextTroughTick(type, world.getTime());
    }
    
    /**
     * 判断是否为爆发时间（Factor 处于高位）
     */
    public boolean isOutbreakTime(ServerWorld world) {
        double deviation = getDeviation(world);
        return deviation > 0.5;
    }
    
    /**
     * 获取潮汐周期进度 (0-100%)
     */
    public double getTideCycleProgress(ServerWorld world) {
        DimensionType type = DimensionType.fromKey(world.getRegistryKey().getValue().toString());
        long currentTick = world.getTime();
        return (currentTick % type.periodTicks()) / (double) type.periodTicks() * 100;
    }

    // ==================== 基础 API 实现 ====================

    public String debugHudLine(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        if (state == null) {
            DimensionType type = DimensionType.fromKey(key);
            return String.format("factor=%.2f tier=2 tide=STABLE", type.baseValue());
        }
        
        DimensionType type = DimensionType.fromKey(key);
        
        return String.format(
            "factor=%.2f tier=%d tide=%s deviation=%.1f%% cycle=%.1f%%",
            state.currentFactor,
            state.currentTier,
            state.lastTideStatus.getName(),
            calculateDeviation(state.currentFactor, type.baseValue()) * 100,
            getTideCycleProgress(world)
        );
    }

    public DayTierSnapshot getLatestSnapshot(ServerWorld world) {
        return snapshots.get(world.getRegistryKey().getValue().toString());
    }

    public Map<String, FactorWorldState> worldStatesView() {
        Map<String, FactorWorldState> view = new LinkedHashMap<>();
        List<Map.Entry<String, RuntimeState>> entries = new ArrayList<>(states.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getKey));
        
        for (Map.Entry<String, RuntimeState> entry : entries) {
            RuntimeState state = entry.getValue();
            double dayAverage = state.daySampleCount == 0 ? state.currentFactor : 
                state.dayFactorSum / state.daySampleCount;
            
            view.put(entry.getKey(), new FactorWorldState(
                entry.getKey(),
                state.currentFactor,
                state.baseFactor,
                dayAverage,
                dayAverage - state.previousDayAverage,
                state.currentTier,
                state.lastUpdatedTick
            ));
        }
        return Collections.unmodifiableMap(view);
    }

    public EventCooldownState cooldownState(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        return new EventCooldownState(cooldowns.getOrDefault(key, Map.of()));
    }

    @Override
    public double getFactor(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        DimensionType type = DimensionType.fromKey(key);
        return state == null ? type.baseValue() : state.currentFactor;
    }

    @Override
    public int getTier(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        DimensionType type = DimensionType.fromKey(key);
        return state == null ? FactorTier.STABLE.level() : state.currentTier;
    }

    @Override
    public OptionalLong predictCrossing(ServerWorld world, double target) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        if (state == null) {
            return OptionalLong.empty();
        }

        double current = state.currentFactor;
        double slope = (state.daySampleCount == 0 ? 0 : 
            state.dayFactorSum / state.daySampleCount) - state.previousDayAverage;
        
        if (Math.abs(slope) < SLOPE_EPSILON) {
            return OptionalLong.empty();
        }
        
        double ticks = (target - current) / slope;
        if (ticks <= 0) {
            return OptionalLong.empty();
        }
        return OptionalLong.of((long) ticks);
    }

    @Override
    public void addFactorOffset(ServerWorld world, double offset, long durationTicks) {
        if (durationTicks <= 0 || offset == 0) {
            return;
        }
        String key = world.getRegistryKey().getValue().toString();
        DimensionType type = DimensionType.fromKey(key);
        RuntimeState state = states.computeIfAbsent(key, 
            k -> new RuntimeState(type, world.getTime() / WORLD_DAY_TICKS));
        state.offsets.add(new TimedOffset(offset, world.getTime() + durationTicks));
    }
    
    // ==================== BlockEntity 便捷方法 ====================
    
    public void addFactor(net.minecraft.util.math.BlockPos pos, int amount) {
        // 占位实现
    }
    
    public void consumeFactor(net.minecraft.util.math.BlockPos pos, int amount) {
        // 占位实现
    }
    
    public void addFactor(ServerWorld world, net.minecraft.util.math.BlockPos pos, int amount) {
        addFactorOffset(world, amount, 1200);
    }
    
    public void consumeFactor(ServerWorld world, net.minecraft.util.math.BlockPos pos, int amount) {
        addFactorOffset(world, -amount, 1200);
    }
    
    public double getDimensionBaseValue(ServerWorld world) {
        return DimensionType.fromKey(world.getRegistryKey().getValue().toString()).baseValue();
    }
    
    public double calculateTransferMultiplier(ServerWorld fromWorld, ServerWorld toWorld) {
        DimensionType from = DimensionType.fromKey(fromWorld.getRegistryKey().getValue().toString());
        DimensionType to = DimensionType.fromKey(toWorld.getRegistryKey().getValue().toString());
        return from.calculateTransferMultiplierTo(to);
    }
    
    public static double baseForDimension(String dimensionKey) {
        return DimensionType.fromKey(dimensionKey).baseValue();
    }

    // ==================== 日切结算 ====================

    private void settleDay(ServerWorld world, RuntimeState state, long dayIndex, DimensionType dimensionType) {
        double dayAverage = state.daySampleCount == 0 ? state.currentFactor : 
            state.dayFactorSum / state.daySampleCount;
        double trend = dayAverage - state.previousDayAverage;
        double predicted = dayAverage + TREND_WEIGHT * trend;

        int previousTier = state.currentTier;
        int nextTier = DayTierDecider.resolveTier(predicted, dimensionType.baseValue(), previousTier, HYSTERESIS);

        DayTierSnapshot snapshot = new DayTierSnapshot(
            dayIndex, dayAverage, trend, HYSTERESIS, previousTier, nextTier);
        snapshots.put(world.getRegistryKey().getValue().toString(), snapshot);

        if (nextTier != previousTier) {
            SimpleFactorEventBus.getInstance().publish(
                new FactorTierChangeEvent(world, previousTier, nextTier, dayIndex));
            SimpleFactorEventBus.getInstance().publish(
                new FactorThresholdEvent(world, previousTier, nextTier, dayIndex));
        }

        triggerDisasterIfNeeded(world, state, nextTier);

        state.currentTier = nextTier;
        state.previousDayAverage = dayAverage;
        state.dayFactorSum = 0;
        state.daySampleCount = 0;
        state.lastSettledDay = dayIndex;
    }

    private void triggerDisasterIfNeeded(ServerWorld world, RuntimeState state, int tier) {
        String dim = world.getRegistryKey().getValue().toString();
        Map<String, Long> worldCooldown = cooldowns.computeIfAbsent(dim, 
            ignored -> new ConcurrentHashMap<>());
        long now = world.getTime();
        long cooldownEnd = worldCooldown.getOrDefault(DISASTER_EVENT_ID, 0L);

        if (tier >= FactorTier.OVERLOAD.level()) {
            state.overloadStreakDays++;
        } else {
            state.overloadStreakDays = 0;
        }

        if (state.overloadStreakDays > 0 && now >= cooldownEnd) {
            int severity = Math.min(DISASTER_MAX_SEVERITY, 
                DISASTER_BASE_SEVERITY + state.overloadStreakDays);
            SimpleFactorEventBus.getInstance().publish(
                new FactorDisasterEvent(world, "unstable_resonance", severity));
            worldCooldown.put(DISASTER_EVENT_ID, now + DISASTER_COOLDOWN_TICKS);
        }
    }

    // ==================== 工具方法 ====================

    private static double pseudoNoise(String dimensionKey, long tick, double baseValue) {
        double seed = dimensionKey.hashCode() * NOISE_DIMENSION_MULTIPLIER + 
            tick * NOISE_TICK_MULTIPLIER;
        return Math.sin(seed * NOISE_SINE_SCALE) * baseValue * NOISE_RATIO;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // ==================== 内部状态类 ====================

    private static final class RuntimeState {
        private final double baseFactor;
        private final List<TimedOffset> offsets = new ArrayList<>();
        private double currentFactor;
        private double dayFactorSum;
        private long daySampleCount;
        private double previousDayAverage;
        private int currentTier;
        private long lastUpdatedTick;
        private long lastSettledDay;
        private int overloadStreakDays;
        private TideStatus lastTideStatus = TideStatus.STABLE;

        private RuntimeState(DimensionType dimensionType, long dayIndex) {
            this.baseFactor = dimensionType.baseValue();
            this.currentFactor = dimensionType.baseValue();
            this.previousDayAverage = dimensionType.baseValue();
            this.currentTier = FactorTier.STABLE.level(); // 初始为稳定状态
            this.lastSettledDay = Math.max(0, dayIndex - 1);
        }

        private void expireOffsets(long currentTick) {
            offsets.removeIf(offset -> offset.endTick < currentTick);
        }

        private double activeOffsetTotal() {
            return offsets.stream().mapToDouble(TimedOffset::offset).sum();
        }
    }

    private record TimedOffset(double offset, long endTick) {}
}