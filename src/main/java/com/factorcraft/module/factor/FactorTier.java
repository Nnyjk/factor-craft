package com.factorcraft.module.factor;

/**
 * Factor 能量等级 - 基于偏离度划分
 * 
 * Tier 由 Factor 相对于维度基准值的偏离程度决定：
 * - 偏离度 = (当前值 - 基准值) / 基准值
 * 
 * 这样不同维度的 Tier 标准一致，但绝对值不同
 */
public enum FactorTier {
    /** 枯竭：偏离 -50% 以下 */
    DEPLETED(0, -1.0, -0.5, "depleted"),
    
    /** 低能：偏离 -50% 到 -20% */
    LOW_ENERGY(1, -0.5, -0.2, "low_energy"),
    
    /** 稳定：偏离 -20% 到 +20% */
    STABLE(2, -0.2, 0.2, "stable"),
    
    /** 高能：偏离 +20% 到 +50% */
    HIGH_ENERGY(3, 0.2, 0.5, "high_energy"),
    
    /** 过载：偏离 +50% 以上 */
    OVERLOAD(4, 0.5, Double.POSITIVE_INFINITY, "overload");

    private final int level;
    private final double minDeviation;
    private final double maxDeviation;
    private final String name;

    FactorTier(int level, double minDeviation, double maxDeviation, String name) {
        this.level = level;
        this.minDeviation = minDeviation;
        this.maxDeviation = maxDeviation;
        this.name = name;
    }

    public int level() {
        return level;
    }

    public double minDeviation() {
        return minDeviation;
    }

    public double maxDeviation() {
        return maxDeviation;
    }
    
    public String getName() {
        return name;
    }

    /**
     * 根据 Factor 值和基准值计算 Tier
     * 
     * @param factor 当前 Factor 值
     * @param baseValue 该维度的基准值
     * @return 对应的 Tier
     */
    public static FactorTier fromFactor(double factor, double baseValue) {
        double deviation = baseValue == 0 ? 0 : (factor - baseValue) / baseValue;
        return fromDeviation(deviation);
    }
    
    /**
     * 根据偏离度获取 Tier
     */
    public static FactorTier fromDeviation(double deviation) {
        for (FactorTier tier : values()) {
            if (deviation >= tier.minDeviation && deviation < tier.maxDeviation) {
                return tier;
            }
        }
        return OVERLOAD;
    }

    public static FactorTier fromLevel(int level) {
        for (FactorTier tier : values()) {
            if (tier.level == level) {
                return tier;
            }
        }
        return DEPLETED;
    }
}