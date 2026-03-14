package com.factorcraft.module.event;

import com.factorcraft.module.factor.DimensionType;
import com.factorcraft.module.factor.TideStatus;
import net.minecraft.server.world.ServerWorld;

/**
 * 潮汐事件 - 定期触发，供其他模块监听
 * 
 * 触发频率：每 1200 ticks (60秒)
 * 
 * 用途：
 * - 生物生成调整
 * - 环境效果（粒子、音效）
 * - 特殊事件触发
 * - UI 显示更新
 */
public record FactorTideEvent(
    ServerWorld world,
    double currentFactor,
    double deviation,
    TideStatus status,
    DimensionType dimensionType
) {
    /**
     * 是否为高 Factor 状态
     */
    public boolean isHighFactor() {
        return deviation > 0.3;
    }
    
    /**
     * 是否为低 Factor 状态
     */
    public boolean isLowFactor() {
        return deviation < -0.3;
    }
    
    /**
     * 获取效果触发概率
     */
    public double getEffectProbability() {
        return status.baseEffectChance();
    }
    
    /**
     * 获取维度名称
     */
    public String getDimensionName() {
        return dimensionType.name();
    }
}