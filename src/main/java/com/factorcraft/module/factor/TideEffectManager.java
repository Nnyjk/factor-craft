package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

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
        // 单例
    }
    
    public static TideEffectManager getInstance() {
        return instance;
    }
    
    /**
     * 每 tick 更新玩家效果
     * 应在服务器 tick 事件中调用
     */
    public void tick(ServerWorld world) {
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
        
        // 应用新效果
        switch (newStatus) {
            case DEPLETED:
                // 疲劳 I（缓慢）
                player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, 
                    UPDATE_INTERVAL * 3, 
                    0, 
                    false, 
                    false, 
                    true
                ));
                break;
                
            case LOW_ENERGY:
                // 无明显效果
                break;
                
            case STABLE:
                // 无效果
                break;
                
            case HIGH_ENERGY:
                // 生命恢复 I
                player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 
                    UPDATE_INTERVAL * 3, 
                    0, 
                    false, 
                    false, 
                    true
                ));
                break;
                
            case OVERLOAD:
                // 力量 I
                player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.STRENGTH, 
                    UPDATE_INTERVAL * 3, 
                    0, 
                    false, 
                    false, 
                    true
                ));
                // 缓慢掉血（伤害效果）
                player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.WITHER, 
                    UPDATE_INTERVAL * 3, 
                    0, 
                    false, 
                    false, 
                    true
                ));
                break;
        }
        
        // 发送状态更新通知（可选）
        if (oldStatus != null && !player.getWorld().isClient()) {
            FactorCraftMod.LOGGER.debug("Tide status changed for {}: {} -> {}", 
                player.getName().getString(), oldStatus.getName(), newStatus.getName());
        }
    }
    
    /**
     * 移除潮汐状态效果
     */
    private void removeTideEffects(PlayerEntity player, TideStatus status) {
        switch (status) {
            case DEPLETED:
                player.removeStatusEffect(StatusEffects.SLOWNESS);
                break;
            case HIGH_ENERGY:
                player.removeStatusEffect(StatusEffects.REGENERATION);
                break;
            case OVERLOAD:
                player.removeStatusEffect(StatusEffects.STRENGTH);
                player.removeStatusEffect(StatusEffects.WITHER);
                break;
            default:
                break;
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
        if (factorService == null) {
            return baseEfficiency;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        return status.applyMachineEfficiency(baseEfficiency);
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
        if (factorService == null) {
            return baseAmount;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        return status.applyExtractionAmount(baseAmount);
    }
    
    /**
     * 获取生物生成率修正系数
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 修正系数
     */
    public float getSpawnRateModifier(ServerWorld world, ChunkPos chunkPos) {
        FactorService factorService = FactorService.getInstance();
        if (factorService == null) {
            return 1.0f;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        return (float) (1.0 + status.getSpawnRateModifier());
    }
    
    /**
     * 检查是否有过载风险
     * @param world 世界
     * @param chunkPos 区块位置
     * @return 是否有过载风险
     */
    public boolean hasOverloadRisk(ServerWorld world, ChunkPos chunkPos) {
        FactorService factorService = FactorService.getInstance();
        if (factorService == null) {
            return false;
        }
        
        double concentration = factorService.getFactor(world);
        TideStatus status = TideStatus.fromConcentration(concentration);
        return status.hasOverloadRisk();
    }
}
