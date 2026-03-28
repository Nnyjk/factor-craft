package com.factorcraft.module.entity.optimization;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.performance.PerformanceConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * 实体激活范围管理
 * 
 * 根据实体与玩家的距离分级管理实体 AI 更新频率
 * 减少远处实体的计算开销
 */
public class EntityActivationRange {
    
    // 激活范围级别
    public enum ActivationLevel {
        VERY_NEAR,    // 16 格 - 完全激活
        NEAR,         // 32 格 - 正常激活
        NORMAL,       // 64 格 - 降低频率
        FAR,          // 96 格 - 最小激活
        INACTIVE      // > 96 格 - 暂停 AI
    }
    
    // 配置的范围值
    private final double rangeVeryNear;
    private final double rangeNear;
    private final double rangeNormal;
    private final double rangeFar;
    
    // 更新频率（ticks）
    private static final int UPDATE_FREQUENCY_VERY_NEAR = 1;   // 每 tick 更新
    private static final int UPDATE_FREQUENCY_NEAR = 1;        // 每 tick 更新
    private static final int UPDATE_FREQUENCY_NORMAL = 2;      // 每 2 ticks 更新
    private static final int UPDATE_FREQUENCY_FAR = 5;         // 每 5 ticks 更新
    
    public EntityActivationRange() {
        PerformanceConfig config = PerformanceConfig.getInstance();
        this.rangeVeryNear = config.entityActivationRangeVeryNear;
        this.rangeNear = config.entityActivationRangeNear;
        this.rangeNormal = config.entityActivationRangeNormal;
        this.rangeFar = config.entityActivationRangeFar;
    }
    
    /**
     * 获取实体的激活级别
     * 
     * @param entity 实体
     * @param world 世界
     * @return 激活级别
     */
    public ActivationLevel getActivationLevel(Entity entity, ServerWorld world) {
        if (!PerformanceConfig.getInstance().enableEntityActivationRange) {
            return ActivationLevel.VERY_NEAR;
        }
        
        // 查找最近的玩家
        double minDistance = Double.MAX_VALUE;
        
        for (var player : world.getPlayers()) {
            double distance = entity.squaredDistanceTo(player);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        
        // 没有玩家时，使用最低激活级别
        if (minDistance == Double.MAX_VALUE) {
            return ActivationLevel.INACTIVE;
        }
        
        double distance = Math.sqrt(minDistance);
        
        if (distance <= rangeVeryNear) {
            return ActivationLevel.VERY_NEAR;
        } else if (distance <= rangeNear) {
            return ActivationLevel.NEAR;
        } else if (distance <= rangeNormal) {
            return ActivationLevel.NORMAL;
        } else if (distance <= rangeFar) {
            return ActivationLevel.FAR;
        } else {
            return ActivationLevel.INACTIVE;
        }
    }
    
    /**
     * 检查实体是否应该在此 tick 更新
     * 
     * @param entity 实体
     * @param world 世界
     * @param tickCount 当前 tick 数
     * @return 是否应该更新
     */
    public boolean shouldUpdate(Entity entity, ServerWorld world, long tickCount) {
        ActivationLevel level = getActivationLevel(entity, world);
        
        switch (level) {
            case VERY_NEAR:
            case NEAR:
                return true;
            case NORMAL:
                return tickCount % UPDATE_FREQUENCY_NORMAL == 0;
            case FAR:
                return tickCount % UPDATE_FREQUENCY_FAR == 0;
            case INACTIVE:
                return false;
            default:
                return true;
        }
    }
    
    /**
     * 获取实体的更新频率
     * 
     * @param entity 实体
     * @param world 世界
     * @return 更新频率（ticks）
     */
    public int getUpdateFrequency(Entity entity, ServerWorld world) {
        ActivationLevel level = getActivationLevel(entity, world);
        
        return switch (level) {
            case VERY_NEAR, NEAR -> UPDATE_FREQUENCY_VERY_NEAR;
            case NORMAL -> UPDATE_FREQUENCY_NORMAL;
            case FAR -> UPDATE_FREQUENCY_FAR;
            case INACTIVE -> Integer.MAX_VALUE;
        };
    }
    
    /**
     * 检查实体是否在激活范围内
     * 
     * @param entity 实体
     * @param world 世界
     * @return 是否激活
     */
    public boolean isEntityActive(Entity entity, ServerWorld world) {
        ActivationLevel level = getActivationLevel(entity, world);
        return level != ActivationLevel.INACTIVE;
    }
    
    /**
     * 获取实体的搜索范围
     * 
     * 根据激活级别调整实体的目标搜索范围
     * 
     * @param entity 实体
     * @param world 世界
     * @param baseRange 基础搜索范围
     * @return 调整后的搜索范围
     */
    public double getSearchRange(Entity entity, ServerWorld world, double baseRange) {
        ActivationLevel level = getActivationLevel(entity, world);
        
        return switch (level) {
            case VERY_NEAR -> baseRange;
            case NEAR -> baseRange * 0.8;
            case NORMAL -> baseRange * 0.5;
            case FAR -> baseRange * 0.25;
            case INACTIVE -> 0;
        };
    }
    
    /**
     * 获取 Chunk 的实体活跃玩家数量
     * 
     * @param world 世界
     * @param chunkPos Chunk 位置
     * @return 活跃玩家数量
     */
    public int getActivePlayerCount(ServerWorld world, ChunkPos chunkPos) {
        int count = 0;
        double rangeSquared = rangeFar * rangeFar;
        
        Box chunkBox = new Box(
            chunkPos.getStartX(), 0, chunkPos.getStartZ(),
            chunkPos.getEndX(), 256, chunkPos.getEndZ()
        );
        
        for (var player : world.getPlayers()) {
            Vec3d playerPos = player.getPos();
            if (chunkBox.contains(playerPos)) {
                count++;
            } else {
                // 计算 Box 到玩家位置的最近点（Fabric 1.21.4 无 closestPointTo 方法）
                double closestX = Math.max(chunkBox.minX, Math.min(playerPos.x, chunkBox.maxX));
                double closestY = Math.max(chunkBox.minY, Math.min(playerPos.y, chunkBox.maxY));
                double closestZ = Math.max(chunkBox.minZ, Math.min(playerPos.z, chunkBox.maxZ));
                
                double dx = playerPos.x - closestX;
                double dy = playerPos.y - closestY;
                double dz = playerPos.z - closestZ;
                double distanceSquared = dx * dx + dy * dy + dz * dz;
                
                if (distanceSquared <= rangeSquared) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * 获取激活级别名称
     */
    public static String getLevelName(ActivationLevel level) {
        return switch (level) {
            case VERY_NEAR -> "Very Near";
            case NEAR -> "Near";
            case NORMAL -> "Normal";
            case FAR -> "Far";
            case INACTIVE -> "Inactive";
        };
    }
}
