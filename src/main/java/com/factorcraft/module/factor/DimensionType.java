package com.factorcraft.module.factor;

/**
 * 维度类型定义 - 基于 docs/16_dimensions_and_biomes.md
 * 
 * 维度基准值体系（以 1.0 为参考标准）：
 * - 主世界：0.5（低稳定度区域）
 * - 下界：1.5（高稳定度区域）
 * - 末地：3.0（极高稳定度区域）
 */
public enum DimensionType {
    OVERWORLD("overworld", 0.5, 0.2, 192000),
    NETHER("the_nether", 1.5, 0.6, 96000),
    END("the_end", 3.0, 1.2, 288000);

    private final String key;
    private final double baseValue;      // 基准值
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
     * 计算当前 tick 的 Factor 值
     * 公式：Factor(t) = baseValue + amplitude × sin(2π × t / period)
     */
    public double calculateFactor(long worldTick) {
        double progress = (2.0 * Math.PI * worldTick) / periodTicks;
        return baseValue + amplitude * Math.sin(progress);
    }

    /**
     * 获取 Factor 范围
     */
    public double getMinFactor() {
        return baseValue - amplitude;
    }

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
        for (DimensionType type : values()) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        return OVERWORLD; // 默认
    }
}
