package com.factorcraft.module.factor;

/**
 * 对齐 docs/01 中 Tier 分段。
 */
public enum FactorTier {
    DEPLETED(0, Double.NEGATIVE_INFINITY, 10),
    LOW_ENERGY(1, 10, 30),
    STABLE(2, 30, 70),
    HIGH_ENERGY(3, 70, 90),
    OVERLOAD(4, 90, Double.POSITIVE_INFINITY);

    private final int level;
    private final double minInclusive;
    private final double maxExclusive;

    FactorTier(int level, double minInclusive, double maxExclusive) {
        this.level = level;
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }

    public int level() {
        return level;
    }

    public double minInclusive() {
        return minInclusive;
    }

    public double maxExclusive() {
        return maxExclusive;
    }

    public static FactorTier fromFactor(double value) {
        for (FactorTier tier : values()) {
            if (value >= tier.minInclusive && value < tier.maxExclusive) {
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
