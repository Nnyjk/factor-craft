package com.factorcraft.module.factor;

import com.factorcraft.module.factor.state.FactorWorldState;

/**
 * 潮汐系统 - 基于 docs/16_dimensions_and_biomes.md
 * 
 * 负责计算和管理各维度的 Factor 潮汐变化
 */
public class TideSystem {

    /**
     * 计算指定维度在当前 tick 的 Factor 值
     * 
     * @param dimensionType 维度类型
     * @param worldTick 当前世界 tick
     * @return 当前 Factor 值
     */
    public static double calculateCurrentFactor(DimensionType dimensionType, long worldTick) {
        return dimensionType.calculateFactor(worldTick);
    }

    /**
     * 计算 Factor 偏离基准值的百分比
     * 
     * @param currentFactor 当前 Factor 值
     * @param baseValue 基准值
     * @return 偏离百分比（-1.0 到 1.0）
     */
    public static double calculateDeviation(double currentFactor, double baseValue) {
        return (currentFactor - baseValue) / baseValue;
    }

    /**
     * 判断 Factor 状态（基于偏离度）
     * 
     * @param deviation 偏离度（-1.0 到 1.0）
     * @return Factor 状态
     */
    public static FactorStatus getStatusFromDeviation(double deviation) {
        double absDeviation = Math.abs(deviation);
        
        if (absDeviation <= 0.1) {
            return FactorStatus.STABLE;
        } else if (absDeviation <= 0.3) {
            return FactorStatus.DEVIATED;
        } else if (absDeviation <= 0.5) {
            return FactorStatus.FLUCTUATING;
        } else {
            return FactorStatus.VOLATILE;
        }
    }

    /**
     * 预测未来 tick 的 Factor 值
     * 
     * @param dimensionType 维度类型
     * @param currentTick 当前 tick
     * @param futureTick 未来 tick
     * @return 预测的 Factor 值
     */
    public static double predictFutureFactor(DimensionType dimensionType, long currentTick, long futureTick) {
        return dimensionType.calculateFactor(futureTick);
    }

    /**
     * 计算 Factor 变化率（每 tick）
     * 
     * @param dimensionType 维度类型
     * @param fromTick 起始 tick
     * @param toTick 结束 tick
     * @return 平均变化率（Factor/tick）
     */
    public static double calculateChangeRate(DimensionType dimensionType, long fromTick, long toTick) {
        if (fromTick == toTick) {
            return 0.0;
        }
        
        double factorFrom = dimensionType.calculateFactor(fromTick);
        double factorTo = dimensionType.calculateFactor(toTick);
        
        return (factorTo - factorFrom) / (toTick - fromTick);
    }

    /**
     * 查找下一个 Factor 峰值 tick
     * 
     * @param dimensionType 维度类型
     * @param currentTick 当前 tick
     * @return 下一个峰值的 tick
     */
    public static long findNextPeakTick(DimensionType dimensionType, long currentTick) {
        long period = dimensionType.periodTicks();
        long cyclePosition = currentTick % period;
        long quarterPeriod = period / 4;
        
        // 峰值出现在 1/4 周期处
        if (cyclePosition < quarterPeriod) {
            return currentTick + (quarterPeriod - cyclePosition);
        } else {
            return currentTick + (period - cyclePosition) + quarterPeriod;
        }
    }

    /**
     * 查找下一个 Factor 谷值 tick
     * 
     * @param dimensionType 维度类型
     * @param currentTick 当前 tick
     * @return 下一个谷值的 tick
     */
    public static long findNextTroughTick(DimensionType dimensionType, long currentTick) {
        long period = dimensionType.periodTicks();
        long cyclePosition = currentTick % period;
        long threeQuarterPeriod = (period * 3) / 4;
        
        // 谷值出现在 3/4 周期处
        if (cyclePosition < threeQuarterPeriod) {
            return currentTick + (threeQuarterPeriod - cyclePosition);
        } else {
            return currentTick + (period - cyclePosition) + threeQuarterPeriod;
        }
    }

    /**
     * Factor 状态枚举
     */
    public enum FactorStatus {
        STABLE(0.0),      // ±10% 以内，无灾害
        DEVIATED(0.05),   // ±10-30%，低概率灾害
        FLUCTUATING(0.15), // ±30-50%，中概率灾害
        VOLATILE(0.30);   // ±50%+，高概率灾害

        private final double disasterProbability;

        FactorStatus(double disasterProbability) {
            this.disasterProbability = disasterProbability;
        }

        public double disasterProbability() {
            return disasterProbability;
        }
    }
}
