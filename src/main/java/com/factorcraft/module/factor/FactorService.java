package com.factorcraft.module.factor;

import com.factorcraft.module.event.FactorChangeEvent;
import com.factorcraft.module.event.FactorDisasterEvent;
import com.factorcraft.module.event.FactorThresholdEvent;
import com.factorcraft.module.event.FactorTierChangeEvent;
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
 * M1 因子系统运行时服务：实时更新 + 日切结算 + 阈值事件/灾害冷却。
 */
public final class FactorService implements FactorApi {
    private static final long WORLD_DAY_TICKS = 24_000;
    private static final double FACTOR_MIN = 0;
    private static final double FACTOR_MAX = 100;
    private static final double CHANGE_EVENT_EPSILON = 0.001;
    private static final double SLOPE_EPSILON = 0.0001;

    private static final double TIDE_AMPLITUDE_A = 12;
    private static final double TIDE_AMPLITUDE_B = 6;
    private static final long TIDE_PERIOD_A = 96_000;
    private static final long TIDE_PERIOD_B = 192_000;
    // 第二个潮汐波加入固定相位差，避免双周期始终同相叠加造成规律过强。
    private static final double TIDE_PHASE_SHIFT = 0.8;

    private static final double DAMPING = 0.04;
    private static final double TREND_WEIGHT = 0.35;
    private static final double HYSTERESIS = 2.0;

    private static final long DISASTER_COOLDOWN_TICKS = WORLD_DAY_TICKS;
    private static final String DISASTER_EVENT_ID = "factor_disaster";
    private static final String DISASTER_TYPE_UNSTABLE_RESONANCE = "unstable_resonance";
    private static final int DISASTER_BASE_SEVERITY = 2;
    private static final int DISASTER_MAX_SEVERITY = 5;

    private static final String HUD_LINE_EMPTY = "factor=0.00 dayAvg=0.00 trend=0.00 tier=0 next=0";

    private static final double BASE_FACTOR_OVERWORLD = 50;
    private static final double BASE_FACTOR_NETHER = 80;
    private static final double BASE_FACTOR_END = 20;

    private static final double NOISE_DIMENSION_MULTIPLIER = 31.0;
    private static final double NOISE_TICK_MULTIPLIER = 17.0;
    private static final double NOISE_SINE_SCALE = 0.01;
    private static final double NOISE_AMPLITUDE = 0.45;

    private final Map<String, RuntimeState> states = new ConcurrentHashMap<>();
    private final Map<String, DayTierSnapshot> snapshots = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public void tick(ServerWorld world) {
        String dimensionKey = world.getRegistryKey().getValue().toString();
        long tick = world.getTime();
        long day = tick / WORLD_DAY_TICKS;
        RuntimeState state = states.computeIfAbsent(dimensionKey, k -> new RuntimeState(baseForDimension(k), day));

        state.expireOffsets(tick);

        double previousFactor = state.currentFactor;
        double tideDelta = tideAt(tick + 1) - tideAt(tick);
        double playerDelta = state.activeOffsetTotal();
        double randomDelta = pseudoNoise(dimensionKey, tick);

        double nextFactor = previousFactor + tideDelta + playerDelta + randomDelta - DAMPING * (previousFactor - state.baseFactor);
        state.currentFactor = clamp(nextFactor, FACTOR_MIN, FACTOR_MAX);

        state.dayFactorSum += state.currentFactor;
        state.daySampleCount++;
        state.lastUpdatedTick = tick;

        if (Math.abs(state.currentFactor - previousFactor) > CHANGE_EVENT_EPSILON) {
            SimpleFactorEventBus.getInstance().publish(new FactorChangeEvent(world, previousFactor, state.currentFactor));
        }

        if (tick % WORLD_DAY_TICKS == 0 && day > state.lastSettledDay) {
            settleDay(world, state, day);
        }
    }

    public String debugHudLine(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        if (state == null) {
            return HUD_LINE_EMPTY;
        }
        double dayAvg = state.daySampleCount == 0 ? state.currentFactor : state.dayFactorSum / state.daySampleCount;
        double predicted = dayAvg + TREND_WEIGHT * (dayAvg - state.previousDayAverage);
        int predictedTier = DayTierDecider.resolveTier(predicted, state.currentTier, HYSTERESIS);
        return String.format(
                "factor=%.2f dayAvg=%.2f trend=%.2f tier=%d next=%d",
                state.currentFactor,
                dayAvg,
                dayAvg - state.previousDayAverage,
                state.currentTier,
                predictedTier
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
            double dayAverage = state.daySampleCount == 0 ? state.currentFactor : state.dayFactorSum / state.daySampleCount;
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
        return state == null ? baseForDimension(key) : state.currentFactor;
    }

    @Override
    public int getTier(ServerWorld world) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        return state == null ? FactorTier.fromFactor(baseForDimension(key)).level() : state.currentTier;
    }

    @Override
    public OptionalLong predictCrossing(ServerWorld world, double target) {
        String key = world.getRegistryKey().getValue().toString();
        RuntimeState state = states.get(key);
        if (state == null) {
            return OptionalLong.empty();
        }

        double current = state.currentFactor;
        double slope = (state.daySampleCount == 0 ? 0 : state.dayFactorSum / state.daySampleCount) - state.previousDayAverage;
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
        RuntimeState state = states.computeIfAbsent(key, k -> new RuntimeState(baseForDimension(k), world.getTime() / WORLD_DAY_TICKS));
        state.offsets.add(new TimedOffset(offset, world.getTime() + durationTicks));
    }

    private void settleDay(ServerWorld world, RuntimeState state, long dayIndex) {
        double dayAverage = state.daySampleCount == 0 ? state.currentFactor : state.dayFactorSum / state.daySampleCount;
        double trend = dayAverage - state.previousDayAverage;
        double predicted = dayAverage + TREND_WEIGHT * trend;

        int previousTier = state.currentTier;
        int nextTier = DayTierDecider.resolveTier(predicted, previousTier, HYSTERESIS);

        DayTierSnapshot snapshot = new DayTierSnapshot(dayIndex, dayAverage, trend, HYSTERESIS, previousTier, nextTier);
        snapshots.put(world.getRegistryKey().getValue().toString(), snapshot);

        if (nextTier != previousTier) {
            SimpleFactorEventBus.getInstance().publish(new FactorTierChangeEvent(world, previousTier, nextTier, dayIndex));
            SimpleFactorEventBus.getInstance().publish(new FactorThresholdEvent(world, previousTier, nextTier, dayIndex));
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
        Map<String, Long> worldCooldown = cooldowns.computeIfAbsent(dim, ignored -> new ConcurrentHashMap<>());
        long now = world.getTime();
        long cooldownEnd = worldCooldown.getOrDefault(DISASTER_EVENT_ID, 0L);

        if (tier >= FactorTier.OVERLOAD.level()) {
            state.overloadStreakDays++;
        } else {
            state.overloadStreakDays = 0;
        }

        if (state.overloadStreakDays > 0 && now >= cooldownEnd) {
            int severity = Math.min(DISASTER_MAX_SEVERITY, DISASTER_BASE_SEVERITY + state.overloadStreakDays);
            SimpleFactorEventBus.getInstance().publish(new FactorDisasterEvent(world, DISASTER_TYPE_UNSTABLE_RESONANCE, severity));
            worldCooldown.put(DISASTER_EVENT_ID, now + DISASTER_COOLDOWN_TICKS);
        }
    }

    public static double baseForDimension(String dimensionKey) {
        if (dimensionKey.contains("the_nether")) {
            return BASE_FACTOR_NETHER;
        }
        if (dimensionKey.contains("the_end")) {
            return BASE_FACTOR_END;
        }
        return BASE_FACTOR_OVERWORLD;
    }

    private static double tideAt(long tick) {
        double thetaA = (Math.PI * 2 * tick) / TIDE_PERIOD_A;
        double thetaB = (Math.PI * 2 * tick) / TIDE_PERIOD_B;
        return TIDE_AMPLITUDE_A * Math.sin(thetaA) + TIDE_AMPLITUDE_B * Math.sin(thetaB + TIDE_PHASE_SHIFT);
    }

    private static double pseudoNoise(String dimensionKey, long tick) {
        double seed = dimensionKey.hashCode() * NOISE_DIMENSION_MULTIPLIER + tick * NOISE_TICK_MULTIPLIER;
        return Math.sin(seed * NOISE_SINE_SCALE) * NOISE_AMPLITUDE;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

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

        private RuntimeState(double baseFactor, long dayIndex) {
            this.baseFactor = baseFactor;
            this.currentFactor = baseFactor;
            this.previousDayAverage = baseFactor;
            this.currentTier = FactorTier.fromFactor(baseFactor).level();
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
