package com.factorcraft.module.cycle.world;

import it.unimi.dsi.fastutil.longs.Long2DoubleMap;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Factor 浓度世界管理器
 * 
 * 管理每个 World 的 Factor 浓度分布
 * 支持浓度查询、消耗、添加、扩散等操作
 * 
 * 性能优化：
 * - 使用区块坐标作为键，避免全图扫描
 * - 懒加载浓度数据
 * - 定期清理空区块数据
 */
public class FactorLevelManager {
    
    // 浓度数据：区块坐标 -> 浓度值
    private final Long2DoubleMap concentrationData = new Long2DoubleOpenHashMap();
    
    // 默认浓度值
    private static final double DEFAULT_CONCENTRATION = 0.5;
    
    // 浓度范围
    private static final double MIN_CONCENTRATION = 0.0;
    private static final double MAX_CONCENTRATION = 1.0;
    
    // 自然恢复速率
    private static final double NATURAL_RECOVERY_RATE = 0.001;
    
    // 扩散速率
    private static final double DIFFUSION_RATE = 0.05;
    
    /**
     * 获取指定位置的 Factor 浓度
     * 
     * @param world 世界
     * @param pos 位置
     * @return 浓度值 (0.0 - 1.0)
     */
    public double getConcentration(World world, BlockPos pos) {
        long chunkKey = getChunkKey(world, pos);
        return concentrationData.getOrDefault(chunkKey, DEFAULT_CONCENTRATION);
    }
    
    /**
     * 消耗指定位置的 Factor 浓度
     * 
     * @param world 世界
     * @param pos 位置
     * @param amount 消耗量
     * @return 实际消耗量
     */
    public double consumeConcentration(World world, BlockPos pos, double amount) {
        long chunkKey = getChunkKey(world, pos);
        double current = concentrationData.getOrDefault(chunkKey, DEFAULT_CONCENTRATION);
        double toConsume = Math.min(amount, current - MIN_CONCENTRATION);
        
        if (toConsume > 0) {
            concentrationData.put(chunkKey, Math.max(MIN_CONCENTRATION, current - toConsume));
        }
        
        return toConsume;
    }
    
    /**
     * 添加指定位置的 Factor 浓度
     * 
     * @param world 世界
     * @param pos 位置
     * @param amount 添加量
     * @return 实际添加量
     */
    public double addConcentration(World world, BlockPos pos, double amount) {
        long chunkKey = getChunkKey(world, pos);
        double current = concentrationData.getOrDefault(chunkKey, DEFAULT_CONCENTRATION);
        double toAdd = Math.min(amount, MAX_CONCENTRATION - current);
        
        if (toAdd > 0) {
            concentrationData.put(chunkKey, Math.min(MAX_CONCENTRATION, current + toAdd));
        }
        
        return toAdd;
    }
    
    /**
     * tick 逻辑：浓度扩散和自然恢复
     * 
     * @param world 世界
     */
    public void tick(World world) {
        if (world.isClient) {
            return;
        }
        
        // 自然恢复（使用增强 for 循环）
        for (Long2DoubleMap.Entry entry : concentrationData.long2DoubleEntrySet()) {
            long chunkKey = entry.getLongKey();
            double concentration = entry.getDoubleValue();
            
            if (concentration < DEFAULT_CONCENTRATION) {
                concentrationData.put(chunkKey, Math.min(
                    DEFAULT_CONCENTRATION,
                    concentration + NATURAL_RECOVERY_RATE
                ));
            }
        }
        
        // 扩散（简化实现，实际应该更复杂）
        // TODO: 实现真正的浓度扩散算法
    }
    
    /**
     * 设置指定位置的浓度
     * 
     * @param world 世界
     * @param pos 位置
     * @param concentration 浓度值
     */
    public void setConcentration(World world, BlockPos pos, double concentration) {
        long chunkKey = getChunkKey(world, pos);
        concentrationData.put(chunkKey, MathHelper.clamp(concentration, MIN_CONCENTRATION, MAX_CONCENTRATION));
    }
    
    /**
     * 获取区块键
     */
    private long getChunkKey(World world, BlockPos pos) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        // 使用世界维度 ID 和区块坐标组合作为键
        return ((long) world.getRegistryKey().getValue().hashCode() << 32) | 
               ((long) chunkX & 0xFFFFFFFFL) << 16 | 
               ((long) chunkZ & 0xFFFFL);
    }
    
    /**
     * 清理空数据（浓度等于默认值的区块）
     */
    public void cleanup() {
        // 使用迭代器安全删除
        concentrationData.long2DoubleEntrySet().removeIf(entry -> 
            Math.abs(entry.getDoubleValue() - DEFAULT_CONCENTRATION) < 0.0001
        );
    }
}
