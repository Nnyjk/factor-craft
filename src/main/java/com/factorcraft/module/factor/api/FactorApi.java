package com.factorcraft.module.factor.api;

import com.factorcraft.module.factor.TideStatus;
import net.minecraft.server.world.ServerWorld;

import java.util.OptionalLong;

/**
 * Factor 系统 API 接口
 * 
 * 提供对 Factor 系统的统一访问
 */
public interface FactorApi {
    
    // ==================== 基础查询 ====================
    
    /**
     * 获取世界的当前 Factor 值
     */
    double getFactor(ServerWorld world);

    /**
     * 获取世界的当前 Tier 等级
     */
    int getTier(ServerWorld world);

    /**
     * 预测 Factor 达到目标值的时间
     */
    OptionalLong predictCrossing(ServerWorld world, double target);

    /**
     * 添加 Factor 偏移量（临时效果）
     */
    void addFactorOffset(ServerWorld world, double offset, long durationTicks);
    
    // ==================== 潮汐系统 ====================
    
    /**
     * 获取世界的潮汐状态
     */
    default TideStatus getTideStatus(ServerWorld world) {
        return TideStatus.STABLE;
    }
    
    /**
     * 获取当前偏离度
     */
    default double getDeviation(ServerWorld world) {
        return 0;
    }
    
    /**
     * 预测下一个潮汐峰值 tick
     */
    default long getNextPeakTick(ServerWorld world) {
        return -1;
    }
    
    /**
     * 预测下一个潮汐谷值 tick
     */
    default long getNextTroughTick(ServerWorld world) {
        return -1;
    }
    
    /**
     * 判断是否为爆发时间
     */
    default boolean isOutbreakTime(ServerWorld world) {
        return false;
    }
    
    /**
     * 获取潮汐周期进度 (0-100%)
     */
    default double getTideCycleProgress(ServerWorld world) {
        return 0;
    }
}