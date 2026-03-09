package com.factorcraft.api;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.OptionalLong;

/**
 * Factor 系统公共 API
 * 
 * 提供给第三方 Mod 使用的 Factor 系统接口
 * 
 * @since 0.1.0
 */
public interface FactorApi {
    
    /**
     * 获取指定世界的当前 Factor 值
     * 
     * @param world 服务器世界
     * @return Factor 值 (0-100)
     */
    double getFactor(ServerWorld world);
    
    /**
     * 获取指定世界的当前 Factor 等级
     * 
     * @param world 服务器世界
     * @return Factor 等级 (0-4)
     */
    int getTier(ServerWorld world);
    
    /**
     * 预测 Factor 何时达到目标值
     * 
     * @param world 服务器世界
     * @param target 目标 Factor 值
     * @return 预计到达的 tick 数，如果无法预测则返回 empty
     */
    OptionalLong predictCrossing(ServerWorld world, double target);
    
    /**
     * 向世界添加临时 Factor 偏移
     * 
     * @param world 服务器世界
     * @param offset 偏移量 (正数增加，负数减少)
     * @param durationTicks 持续时间 (tick)
     */
    void addFactorOffset(ServerWorld world, double offset, long durationTicks);
    
    /**
     * 在指定位置添加 Factor (用于 BlockEntity)
     * 
     * @param world 服务器世界
     * @param pos 方块位置
     * @param amount 添加量
     */
    void addFactor(ServerWorld world, BlockPos pos, int amount);
    
    /**
     * 从指定位置消耗 Factor (用于 BlockEntity)
     * 
     * @param world 服务器世界
     * @param pos 方块位置
     * @param amount 消耗量
     */
    void consumeFactor(ServerWorld world, BlockPos pos, int amount);
    
    /**
     * 获取维度基准值
     * 
     * @param world 服务器世界
     * @return 维度基准值
     */
    double getDimensionBaseValue(ServerWorld world);
    
    /**
     * 计算跨维度传输倍率
     * 
     * @param fromWorld 源世界
     * @param toWorld 目标世界
     * @return 传输倍率
     */
    double calculateTransferMultiplier(ServerWorld fromWorld, ServerWorld toWorld);
}
