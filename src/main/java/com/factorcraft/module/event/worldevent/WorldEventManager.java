package com.factorcraft.module.event.worldevent;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.TideStatus;
import com.factorcraft.module.shared.ModuleLoggers;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 世界事件管理器
 * 
 * 负责：
 * - 事件触发检测
 * - 活跃事件管理
 * - 效果应用
 * - 通知发送
 */
public final class WorldEventManager {
    private static final org.slf4j.Logger LOG = ModuleLoggers.forModule("world_event");
    private static final WorldEventManager INSTANCE = new WorldEventManager();
    
    // 活跃事件存储
    private final Map<UUID, ActiveWorldEvent> activeEvents = new ConcurrentHashMap<>();
    
    // 上次检测时间
    private final Map<WorldEventType, Long> lastCheckTimes = new EnumMap<>(WorldEventType.class);
    
    // 事件统计
    private final Map<WorldEventType, Integer> eventCounts = new EnumMap<>(WorldEventType.class);
    
    private WorldEventManager() {}
    
    public static WorldEventManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 主 tick 处理
     */
    public void tick(ServerWorld world) {
        long currentTime = world.getTime();
        
        // 1. 检测并触发新事件
        checkAndTriggerEvents(world, currentTime);
        
        // 2. 更新活跃事件
        tickActiveEvents(world);
        
        // 3. 应用事件效果
        applyEventEffects(world);
    }
    
    /**
     * 检测并触发事件
     */
    private void checkAndTriggerEvents(ServerWorld world, long currentTime) {
        Random random = world.random;
        
        for (WorldEventType type : WorldEventType.values()) {
            // 跳过周期性事件（由单独逻辑处理）
            if (type.isPeriodic()) {
                continue;
            }
            
            // 检查检测间隔
            long lastCheck = lastCheckTimes.getOrDefault(type, 0L);
            if (currentTime - lastCheck < type.getCheckIntervalTicks()) {
                continue;
            }
            
            lastCheckTimes.put(type, currentTime);
            
            // 计算触发概率（基于世界状态调整）
            double probability = calculateTriggerProbability(world, type);
            
            if (random.nextDouble() < probability) {
                triggerEvent(world, type, random);
            }
        }
    }
    
    /**
     * 计算事件触发概率
     */
    private double calculateTriggerProbability(ServerWorld world, WorldEventType type) {
        double baseProbability = type.getBaseProbability();
        double currentFactor = FactorService.getInstance().getFactor(world);
        TideStatus status = TideStatus.fromConcentration(currentFactor);
        
        return switch (type) {
            case CONCENTRATION_FLUCTUATION -> {
                // 高浓度区域波动概率更高
                yield baseProbability * (1.0 + currentFactor);
            }
            case FACTOR_STORM -> {
                // 过载状态触发风暴概率显著增加
                if (status == TideStatus.OVERLOAD) {
                    yield baseProbability * 5.0;
                } else if (status == TideStatus.HIGH_ENERGY) {
                    yield baseProbability * 2.0;
                }
                yield baseProbability * 0.1; // 低浓度时几乎不触发
            }
            case FACTOR_ERUPTION -> {
                // 深度挖掘时概率增加（由玩家行为触发）
                yield baseProbability;
            }
            case VOID_EROSION -> {
                // 低浓度区域侵蚀概率增加
                if (status == TideStatus.DEPLETED) {
                    yield baseProbability * 10.0;
                } else if (status == TideStatus.LOW_ENERGY) {
                    yield baseProbability * 3.0;
                }
                yield baseProbability * 0.1;
            }
            default -> baseProbability;
        };
    }
    
    /**
     * 触发事件
     */
    private void triggerEvent(ServerWorld world, WorldEventType type, Random random) {
        // 选择事件中心位置
        BlockPos center = selectEventCenter(world, type, random);
        if (center == null) {
            return;
        }
        
        // 计算事件参数
        int radius = calculateEventRadius(type, random);
        int duration = calculateEventDuration(type, random);
        int severity = calculateEventSeverity(world, type, random);
        
        // 创建活跃事件
        ActiveWorldEvent event = new ActiveWorldEvent(type, world, center, radius, duration, severity);
        activeEvents.put(event.getEventId(), event);
        
        // 更新统计
        eventCounts.merge(type, 1, Integer::sum);
        
        // 发送通知
        broadcastEventStart(world, event);
        
        // 触发事件总线
        SimpleFactorEventBus.getInstance().publish(
            new WorldEventTriggeredEvent(world, event)
        );
        
        LOG.info("[WorldEvent] 触发事件: type={}, pos={}, duration={}ticks, severity={}",
            type.getId(), center, duration, severity);
    }
    
    /**
     * 选择事件中心位置
     */
    private BlockPos selectEventCenter(ServerWorld world, WorldEventType type, Random random) {
        // 找到最近的玩家作为参考
        Optional<ServerPlayerEntity> nearestPlayer = world.getPlayers().stream()
            .min(Comparator.comparingDouble(p -> p.getBlockPos().getSquaredDistance(BlockPos.ORIGIN)));
        
        if (nearestPlayer.isEmpty()) {
            return null;
        }
        
        BlockPos playerPos = nearestPlayer.get().getBlockPos();
        
        // 根据事件类型选择位置
        int offsetX = random.nextBetween(-100, 100);
        int offsetZ = random.nextBetween(-100, 100);
        int y = world.getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, 
            playerPos.getX() + offsetX, playerPos.getZ() + offsetZ);
        
        return switch (type) {
            case FACTOR_ERUPTION -> {
                // 喷发发生在玩家附近地面
                yield world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, 
                    new BlockPos(playerPos.getX() + offsetX, 0, playerPos.getZ() + offsetZ));
            }
            case VOID_EROSION -> {
                // 侵蚀发生在低浓度区域
                yield new BlockPos(playerPos.getX() + offsetX * 2, y / 2, playerPos.getZ() + offsetZ * 2);
            }
            default -> new BlockPos(playerPos.getX() + offsetX, y / 2, playerPos.getZ() + offsetZ);
        };
    }
    
    /**
     * 计算事件影响半径
     */
    private int calculateEventRadius(WorldEventType type, Random random) {
        return switch (type) {
            case CONCENTRATION_FLUCTUATION -> random.nextBetween(32, 64);
            case FACTOR_STORM -> random.nextBetween(64, 128);
            case FACTOR_TIDE -> 256; // 全球范围
            case FACTOR_ERUPTION -> random.nextBetween(8, 16);
            case VOID_EROSION -> random.nextBetween(24, 48);
        };
    }
    
    /**
     * 计算事件持续时间
     */
    private int calculateEventDuration(WorldEventType type, Random random) {
        int min = type.getMinDurationTicks();
        int max = type.getMaxDurationTicks();
        if (max < 0) {
            return -1; // 无限
        }
        return random.nextBetween(min, max);
    }
    
    /**
     * 计算事件严重程度
     */
    private int calculateEventSeverity(ServerWorld world, WorldEventType type, Random random) {
        double factor = FactorService.getInstance().getFactor(world);
        int base = random.nextBetween(1, 3);
        
        return switch (type) {
            case FACTOR_STORM -> {
                // 高浓度时风暴更严重
                yield (int)(base * (1.0 + factor));
            }
            case VOID_EROSION -> {
                // 低浓度时侵蚀更严重
                yield (int)(base * (2.0 - factor));
            }
            default -> base;
        };
    }
    
    /**
     * Tick 活跃事件
     */
    private void tickActiveEvents(ServerWorld world) {
        Iterator<Map.Entry<UUID, ActiveWorldEvent>> iterator = activeEvents.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, ActiveWorldEvent> entry = iterator.next();
            ActiveWorldEvent event = entry.getValue();
            
            // 只处理当前世界的事件
            if (!event.getWorld().equals(world)) {
                continue;
            }
            
            boolean stillActive = event.tick();
            
            if (!stillActive) {
                // 事件结束
                onEventEnd(world, event);
                iterator.remove();
            }
        }
    }
    
    /**
     * 应用事件效果
     */
    private void applyEventEffects(ServerWorld world) {
        for (ActiveWorldEvent event : activeEvents.values()) {
            if (!event.getWorld().equals(world) || !event.isActive()) {
                continue;
            }
            
            // 根据事件类型应用效果
            switch (event.getType()) {
                case CONCENTRATION_FLUCTUATION -> applyFluctuationEffect(world, event);
                case FACTOR_STORM -> applyStormEffect(world, event);
                case FACTOR_TIDE -> applyTideEffect(world, event);
                case FACTOR_ERUPTION -> applyEruptionEffect(world, event);
                case VOID_EROSION -> applyErosionEffect(world, event);
            }
        }
    }
    
    /**
     * 浓度波动效果
     */
    private void applyFluctuationEffect(ServerWorld world, ActiveWorldEvent event) {
        // 每 100 tick 应用一次效果
        if (event.getElapsedTicks() % 100 != 0) {
            return;
        }
        
        BlockPos center = event.getCenterPos();
        int radius = event.getRadius();
        
        // 在影响区域内生成粒子
        for (int i = 0; i < 5; i++) {
            double x = center.getX() + world.random.nextBetween(-radius, radius);
            double y = center.getY() + world.random.nextBetween(-10, 10);
            double z = center.getZ() + world.random.nextBetween(-radius, radius);
            
            world.spawnParticles(ParticleTypes.ENCHANT,
                x, y, z, 1, 0.5, 0.5, 0.5, 0.02);
        }
    }
    
    /**
     * Factor 风暴效果
     */
    private void applyStormEffect(ServerWorld world, ActiveWorldEvent event) {
        long elapsed = event.getElapsedTicks();
        
        // 每 20 tick 应用一次效果
        if (elapsed % 20 != 0) {
            return;
        }
        
        BlockPos center = event.getCenterPos();
        int radius = event.getRadius();
        double intensity = event.getIntensity();
        
        // 闪电效果（高严重程度时）
        if (event.getSeverity() >= 3 && world.random.nextDouble() < 0.1 * intensity) {
            BlockPos strikePos = center.add(
                world.random.nextBetween(-radius / 2, radius / 2),
                0,
                world.random.nextBetween(-radius / 2, radius / 2)
            );
            // 安全起见不实际召唤闪电，只播放粒子效果
            world.spawnParticles(ParticleTypes.FLASH,
                strikePos.getX(), strikePos.getY() + 10, strikePos.getZ(),
                1, 0, 0, 0, 0);
        }
        
        // 粒子效果
        for (int i = 0; i < (int)(10 * intensity); i++) {
            double x = center.getX() + world.random.nextBetween(-radius, radius);
            double y = center.getY() + world.random.nextBetween(-20, 20);
            double z = center.getZ() + world.random.nextBetween(-radius, radius);
            
            world.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME,
                x, y, z, 1, 0, 1, 0, 0.02);
        }
        
        // 玩家效果
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (event.affectsPosition(player.getBlockPos())) {
                // 过载增益
                if (intensity > 1.0) {
                    player.sendMessage(Text.literal("⚡ Factor Storm intensifying...")
                        .formatted(Formatting.DARK_PURPLE), true);
                }
            }
        }
    }
    
    /**
     * Factor 潮汐效果
     */
    private void applyTideEffect(ServerWorld world, ActiveWorldEvent event) {
        // 潮汐效果由 TideSystem 处理
        // 这里只添加视觉提示
        if (event.getElapsedTicks() % 6000 == 0) {
            // 每 5 分钟提醒一次
            for (ServerPlayerEntity player : world.getPlayers()) {
                player.sendMessage(Text.literal("🌊 Factor Tide is shifting...")
                    .formatted(Formatting.AQUA), true);
            }
        }
    }
    
    /**
     * Factor 喷发效果
     */
    private void applyEruptionEffect(ServerWorld world, ActiveWorldEvent event) {
        long elapsed = event.getElapsedTicks();
        
        // 只在前 30 秒应用效果
        if (elapsed > 600) {
            return;
        }
        
        BlockPos center = event.getCenterPos();
        double intensity = 1.0 - (elapsed / 600.0); // 强度递减
        
        // 喷泉粒子效果
        for (int i = 0; i < (int)(20 * intensity); i++) {
            double x = center.getX() + world.random.nextBetween(-3, 3);
            double y = center.getY() + world.random.nextBetween(0, (int)(10 * intensity));
            double z = center.getZ() + world.random.nextBetween(-3, 3);
            
            world.spawnParticles(ParticleTypes.END_ROD,
                x, y, z, 1, 0.2, 0.5, 0.2, 0.05);
        }
        
        // 爆炸音效
        if (elapsed % 40 == 0) {
            world.playSound(null, center, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH,
                SoundCategory.AMBIENT, 2.0f, 1.2f);
        }
    }
    
    /**
     * 虚空侵蚀效果
     */
    private void applyErosionEffect(ServerWorld world, ActiveWorldEvent event) {
        long elapsed = event.getElapsedTicks();
        
        // 每 200 tick 应用一次效果
        if (elapsed % 200 != 0) {
            return;
        }
        
        BlockPos center = event.getCenterPos();
        int radius = event.getRadius();
        double intensity = event.getIntensity();
        
        // 虚空粒子
        for (int i = 0; i < (int)(8 * intensity); i++) {
            double x = center.getX() + world.random.nextBetween(-radius, radius);
            double y = center.getY() + world.random.nextBetween(-10, 10);
            double z = center.getZ() + world.random.nextBetween(-radius, radius);
            
            world.spawnParticles(ParticleTypes.SQUID_INK,
                x, y, z, 1, 0.5, 0.5, 0.5, 0.01);
        }
    }
    
    /**
     * 事件结束处理
     */
    private void onEventEnd(ServerWorld world, ActiveWorldEvent event) {
        broadcastEventEnd(world, event);
        
        SimpleFactorEventBus.getInstance().publish(
            new WorldEventEndedEvent(world, event)
        );
        
        LOG.info("[WorldEvent] 事件结束: type={}, id={}", 
            event.getType().getId(), event.getEventId());
    }
    
    /**
     * 广播事件开始通知
     */
    private void broadcastEventStart(ServerWorld world, ActiveWorldEvent event) {
        Text message = switch (event.getType()) {
            case CONCENTRATION_FLUCTUATION -> Text.literal("⚠ Factor concentration fluctuating!")
                .formatted(Formatting.YELLOW);
            case FACTOR_STORM -> Text.literal("⛈ Factor Storm approaching!")
                .formatted(Formatting.DARK_PURPLE);
            case FACTOR_TIDE -> Text.literal("🌊 Factor Tide rising...")
                .formatted(Formatting.AQUA);
            case FACTOR_ERUPTION -> Text.literal("🌋 Factor Eruption detected!")
                .formatted(Formatting.GOLD);
            case VOID_EROSION -> Text.literal("☠ Void Erosion spreading!")
                .formatted(Formatting.DARK_GRAY);
        };
        
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
            world.playSound(null, player.getBlockPos(), 
                SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
    }
    
    /**
     * 广播事件结束通知
     */
    private void broadcastEventEnd(ServerWorld world, ActiveWorldEvent event) {
        Text message = switch (event.getType()) {
            case CONCENTRATION_FLUCTUATION -> Text.literal("Factor concentration stabilizing.")
                .formatted(Formatting.GREEN);
            case FACTOR_STORM -> Text.literal("Factor Storm has passed.")
                .formatted(Formatting.LIGHT_PURPLE);
            case FACTOR_TIDE -> Text.literal("Factor Tide receding...")
                .formatted(Formatting.AQUA);
            case FACTOR_ERUPTION -> Text.literal("Factor Eruption has subsided.")
                .formatted(Formatting.GOLD);
            case VOID_EROSION -> Text.literal("Void Erosion halted!")
                .formatted(Formatting.WHITE);
        };
        
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
        }
    }
    
    /**
     * 手动触发事件（用于测试或特殊场景）
     */
    public void triggerEventManually(ServerWorld world, WorldEventType type, BlockPos pos) {
        triggerEvent(world, type, world.random);
    }
    
    /**
     * 终止指定事件
     */
    public void endEvent(UUID eventId) {
        ActiveWorldEvent event = activeEvents.get(eventId);
        if (event != null) {
            event.forceEnd();
        }
    }
    
    /**
     * 获取所有活跃事件
     */
    public Collection<ActiveWorldEvent> getActiveEvents() {
        return Collections.unmodifiableCollection(activeEvents.values());
    }
    
    /**
     * 获取指定类型的事件数量
     */
    public int getEventCount(WorldEventType type) {
        return eventCounts.getOrDefault(type, 0);
    }
    
    /**
     * 获取世界中的活跃事件
     */
    public List<ActiveWorldEvent> getEventsInWorld(ServerWorld world) {
        return activeEvents.values().stream()
            .filter(e -> e.getWorld().equals(world))
            .filter(ActiveWorldEvent::isActive)
            .toList();
    }
    
    /**
     * 检查位置是否受事件影响
     */
    public boolean isPositionAffected(ServerWorld world, BlockPos pos) {
        return activeEvents.values().stream()
            .filter(e -> e.getWorld().equals(world))
            .anyMatch(e -> e.affectsPosition(pos));
    }
}