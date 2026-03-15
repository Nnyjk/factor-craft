package com.factorcraft.module.factor;

/**
 * 日切 Tier 决策器
 * 
 * 根据预测的 Factor 值和基准值决定目标 Tier
 * 使用偏离度体系，带迟滞保护
 */
public final class DayTierDecider {
    private DayTierDecider() {}

    /**
     * 根据预测值决定目标 Tier
     * 
     * @param predicted 预测的 Factor 值
     * @param baseValue 该维度的基准值
     * @param currentTier 当前 Tier 等级
     * @param hysteresis 迟滞阈值（偏离度）
     * @return 目标 Tier 等级
     */
    public static int resolveTier(double predicted, double baseValue, int currentTier, double hysteresis) {
        FactorTier target = FactorTier.fromFactor(predicted, baseValue);
        if (target.level() == currentTier) {
            return currentTier;
        }

        // 升档直接升
        if (target.level() > currentTier) {
            return target.level();
        }

        // 降档保护：偏离度未跌破当前档位下边界 - hysteresis 时，保持当前 Tier
        FactorTier current = FactorTier.fromLevel(currentTier);
        double predictedDeviation = baseValue == 0 ? 0 : (predicted - baseValue) / baseValue;
        
        if (predictedDeviation >= current.minDeviation() - hysteresis) {
            return currentTier;
        }

        return target.level();
    }
}