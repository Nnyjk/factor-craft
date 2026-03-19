package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.factor.TideEffectsConfig.EffectEntry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 潮汐效果管理器
 * 
 * 负责管理潮汐状态对玩家、机器和世界的影响
 * 
 * 功能：
 * 1. 玩家效果：进入/离开区域时应用/移除状态效果
 * 2. 机器效果：自动应用效率修正
 * 3. 世界效果：影响生物生成、作物生长等
 * 
 * 配置化：效果值可通过 tide_effects.json 配置
 */
public class TideEffectManager {
    
    private static final TideEffectManager instance = new TideEffectManager();
    
    /** 玩家 UUID -> 当前潮汐状态 */
    private final Map<UUID, TideStatus> playerTideStatus = new ConcurrentHashMap<>();
    
    /** 玩家 UUID -> 上次检查的位置 */
    private final Map<UUID, Vec3d> playerLastPosition = new ConcurrentHashMap<>();
    
    /** 效果应用半径（方块） */
    private static final double EFFECT_RADIUS = 64.0;
    
    /** 效果更新间隔（tick） */
    private static final int UPDATE_INTERVAL = 20;
    
    private TideEffectManager() {
        // 单例，初始化时加载配置
        TideEffectsConfig.getInstance();
    }
    
    public static TideEffectManager getInstance() {
        return instance;
    }
    
    /**
     * 每 tick 更新玩家效果
     * 应在服务器 tick 事件中调用
     */
    public void tick(ServerWorld world) {
        // 检查是否启用
        if (!TideEffectsConfig.isEnabled()) {
            return;
        }
        
        long gameTime = world.getTime();
        
        // 每 UPDATE_INTERVAL tick 更新一次
        if (gameTime % UPDATE_INTERVAL != 0) {
            return;
        }
        
        // 获取当前区域的潮汐状态
        FactorService factorService = FactorService.getInstance();
        if (factorService == null) {
            return;
        }
        
        // 遍历所有玩家
        for (PlayerEntity player : world.getPlayers()) {
            if (player.isSpectator()) {
                continue;
            }
            
            UUID playerId = player.getUuid();
            Vec3d currentPosition = player.getPos();
            
            // 检查玩家是否移动了足够距离
            Vec3d lastPos = playerLastPosition.get(playerId);
            if (lastPos != null && lastPos.squaredDistanceTo(currentPosition) < 16.0) {
                // 移动距离小于 4 方块，不更新
                continue;
            }
            
            // 更新位置
            playerLastPosition.put(playerId, currentPosition);
            
            // 获取当前区块的 Factor 浓度
            ChunkPos chunkPos = new ChunkPos(player.getBlockPos());
            double concentration = factorService.getFactor(world);
            
            // 获取潮汐状态
            TideStatus newStatus = TideStatus.fromConcentration(concentration);
            TideStatus oldStatus = playerTideStatus.get(playerId);
            
            // 如果状态变化，应用新效果
            if (oldStatus != newStatus) {
                applyTideEffects(player, oldStatus, newStatus);
                playerTideStatus.put(playerId, newStatus);
            }
        }
    }
    
    /**
     * 应用潮汐状态效果
     */
    private void applyTideEffects(PlayerEntity player, @Nullable TideStatus oldStatus, TideStatus newStatus) {
        // 移除旧效果
        if (oldStatus != null) {
            removeTideEffects(player, oldStatus);
        }
        
        // 从配置获取效果列表
        List<EffectEntry> effects = TideEffectsConfig.getPlayerEffects(newStatus);
        
        var registry = player.getRegistryManager().getOrThrow(RegistryKeys.STATUS_EFFECT);
        
        for (EffectEntry entry : effects) {
            RegistryEntry<StatusEffect> effectEntry = getEffectEntryById(entry.effectId(), registry);
            if (effectEntry != null) {
                player.addStatusEffect(new StatusEffectInstance(
                    effectEntry,
                    entry.duration(),
                    entry.amplifier(),
                    false,  // ambient
                    false,  // visible
                    true    // show icon
                ));
            }
        }
        
        // 发送状态更新通知（可选）
        if (oldStatus != null && !player.getWorld().isClient()) {
            FactorCraftMod.LOGGER.debug("Tide status changed for {}: {} -> {}", 
                player.getName().getString(), oldStatus.getName(), newStatus.getName());
        }
    }
    
    /**
     * 根据效果 ID 获取状态效果条目
     */
    @Nullable
    private RegistryEntry<StatusEffect> getEffectEntryById(String effectId, net.minecraft.registry.Registry<StatusEffect> registry) {
        // 尝试解析为命名空间:路径格式
        Identifier id = Identifier.tryParse(effectId);
        if (id == null) {
            // 尝试添加 minecraft: 前缀
            id = Identifier.tryParse("minecraft:" + effectId);
        }
        if (id == null) {
            return null;
        }
        
        return registry.getEntry(id).orElse(null);
    }
    
    /**
     * 移除潮汐状态效果
     */
    private void removeTideEffects(PlayerEntity player, TideStatus status) {
        List<EffectEntry> effects = TideEffectsConfig.getPlayerEffects(status);
        
        var registry = player.getRegistryManager().getOrThrow(RegistryKeys.STATUS_EFFECT);
        
        for (EffectEntry entry : effects) {
            RegistryEntry<StatusEffect> effectEntry = getEffectEntryById(entry.effectId(), registry);
            if (effectEntry != null) {
                player.removeStatusEffect(effectEntry);
            }
        }
    }
    
    /**
     * 玩家离开世界时清理
     */
    public void onPlayerLeave(UUID playerId) {
        playerTideStatus.remove(playerId);
        playerLastPosition.remove(playerId);
    }
    
    /**
     * 获取玩家的当前潮汐状态
     */
    @Nullable
    public TideStatus getPlayerStatus(PlayerEntity player) {
        return playerTideStatus.get(player.getUuid());
    }
    
    /**
     * 计算机器实际效率
     * @param baseEfficiency 基础效率
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 修正后的效率
     */
    public double calculateMachineEfficiency(double baseEfficiency, ServerWorld world, ChunkPos chunkPos) {
        FactorService factorService = FactorService.getInstance();
        if (factorService == null || !TideEffectsConfig.isEnabled()) {
            return baseEfficiency;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        double modifier = TideEffectsConfig.getMachineEfficiency(status);
        return baseEfficiency * (1.0 + modifier);
    }
    
    /**
     * 计算实际 Factor 提取量
     * @param baseAmount 基础提取量
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 修正后的提取量
     */
    public double calculateExtractionAmount(double baseAmount, ServerWorld world, ChunkPos chunkPos) {
        FactorService factorService = FactorService.getInstance();
        if (factorService == null || !TideEffectsConfig.isEnabled()) {
            return baseAmount;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        double modifier = TideEffectsConfig.getExtractionEfficiency(status);
        return baseAmount * (1.0 + modifier);
    }
    
    /**
     * 获取生物生成率修正系数
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 修正系数
     */
    public float getSpawnRateModifier(ServerWorld world, ChunkPos chunkPos) {
        FactorService factorService = FactorService.getInstance();
        if (factorService == null || !TideEffectsConfig.isEnabled()) {
            return 1.0f;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        double modifier = TideEffectsConfig.getCreatureSpawnModifier(status);
        return (float) (1.0 + modifier);
    }
    
    /**
     * 检查是否有过载风险
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 是否有过载风险
     */
    public boolean hasOverloadRisk(ServerWorld world, ChunkPos chunkPos) {
        FactorService factorService = FactorService.getInstance();
        if (factorService == null || !TideEffectsConfig.isEnabled()) {
            return false;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        return TideEffectsConfig.hasOverloadRisk(status);
    }
    
    /**
     * 重载配置
     */
    public void reloadConfig() {
        TideEffectsConfig.reload();
        FactorCraftMod.LOGGER.info("[TideEffectManager] 配置已重载，启用状态: {}", TideEffectsConfig.isEnabled());
    }
}