package com.factorcraft.module.factor;

/**
 * 维度类型定义 - Factor 潮汐系统参数
 * 
 * 维度基准值体系：
 * - 主世界：0.5（基准，中等活性）
 * - 下界：1.5（高活性，周期短）
 * - 末地：3.0（超高活性，周期长）
 * 
 * Factor 范围 = 基准值 ± 幅度
 * 跨维度传输倍率 = 发送端基准 / 接收端基准
 */
public enum DimensionType {
    //                        key                  基准值  幅度   周期(ticks)
    OVERWORLD("minecraft:overworld",      0.5,   0.2,   192000),  // 8 游戏日, 范围 0.3-0.7
    NETHER("minecraft:the_nether",        1.5,   0.6,    96000),  // 4 游戏日, 范围 0.9-2.1
    END("minecraft:the_end",              3.0,   1.2,   288000);  // 12 游戏日, 范围 1.8-4.2

    private final String key;
    private final double baseValue;      // 基准值
    private final double amplitude;      // 潮汐波动幅度
    private final long periodTicks;      // 潮汐周期（tick）

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
     * 
     * 示例：
     * - 下界→主世界: 1.5/0.5 = 3.0x (增益)
     * - 主世界→下界: 0.5/1.5 = 0.33x (损失)
     * - 末地→主世界: 3.0/0.5 = 6.0x (大增益)
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
        return String.format("%s: %.2f (%.1f%% into cycle)", 
            name(), factor, cycleProgress);
    }
}