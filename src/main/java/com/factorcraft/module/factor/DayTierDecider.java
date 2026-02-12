package com.factorcraft.module.factor;

public final class DayTierDecider {
    private DayTierDecider() {}

    public static int resolveTier(double predicted, int currentTier, double hysteresis) {
        FactorTier target = FactorTier.fromFactor(predicted);
        if (target.level() == currentTier) {
            return currentTier;
        }

        if (target.level() > currentTier) {
            return target.level();
        }

        FactorTier current = FactorTier.fromLevel(currentTier);
        // 迟滞仅用于“降档保护”：预测值只要未跌破当前档位下边界-hysteresis，就保持当前 Tier。
        if (predicted >= current.minInclusive() - hysteresis) {
            return currentTier;
        }

        return target.level();
    }
}
