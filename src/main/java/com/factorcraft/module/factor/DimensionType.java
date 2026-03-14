package com.factorcraft.module.factor;

/**
 * 维度类型定义 - Factor 潮汐系统参数
 * 
 * 维度基准值体系（0-100 范围）：
 * - 主世界：50（中等稳定度，波动较大）
 * - 下界：80（高稳定度，周期短）
 * - 末地：20（低稳定度，周期长）
 * 
 * 周期已减半以加快游戏节奏
 */
public enum DimensionType {
    //                        key                  基准值  幅度   周期(ticks)
    OVERWORLD("minecraft:overworld",      50,   12,   96000),   // 4 游戏日
    NETHER("minecraft:the_nether",        80,    8,   48000),   // 2 游戏日
    END("minecraft:the_end",              20,    5,   144000);  // 6 游戏日

    private final String key;
    private final double baseValue;      // 基准值 (0-100)
    private final double amplitude;       // 潮汐波动幅度
    private final long periodTicks;       // 潮汐周期（tick）

    DimensionType(String key, double baseValue, double amplitude, long periodTicks) {
        this.key = key;
        this.baseValue = baseValue;
        this.amplitude = amplitude;
        this.periodTicks = periodTicks;
    }

    public String key() {
        return key;
    }

    public double baseValue() {
        return baseValue;
    }

    public double amplitude() {
        return amplitude;
    }

    public long periodTicks() {
        return periodTicks;
    }

    /**
     * 计算当前 tick 的潮汐 Factor 值
     * 公式：Factor(t) = baseValue + amplitude × sin(2π × t / period)
     */
    public double calculateFactor(long worldTick) {
        double progress = (2.0 * Math.PI * worldTick) / periodTicks;
        return baseValue + amplitude * Math.sin(progress);
    }

    /**
     * 获取 Factor 最小值
     */
    public double getMinFactor() {
        return baseValue - amplitude;
    }

    /**
     * 获取 Factor 最大值
     */
    public double getMaxFactor() {
        return baseValue + amplitude;
    }

    /**
     * 计算传输到目标维度的倍率
     * 公式：倍率 = 发送端基准 / 接收端基准
     */
    public double calculateTransferMultiplierTo(DimensionType target) {
        return this.baseValue / target.baseValue;
    }

    /**
     * 从 Minecraft 维度 ID 获取 DimensionType
     */
    public static DimensionType fromKey(String key) {
        if (key == null) return OVERWORLD;
        
        for (DimensionType type : values()) {
            if (type.key.equals(key) || key.contains(type.key.replace("minecraft:", ""))) {
                return type;
            }
        }
        return OVERWORLD;
    }
    
    /**
     * 获取潮汐描述（用于调试/显示）
     */
    public String getTideDescription(long worldTick) {
        double factor = calculateFactor(worldTick);
        double cycleProgress = (worldTick % periodTicks) / (double) periodTicks * 100;
        return String.format("%s: %.1f (%.1f%% into cycle)", 
            name(), factor, cycleProgress);
    }
}