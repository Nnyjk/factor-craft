package com.factorcraft.module.factor;

import net.minecraft.server.world.ServerWorld;

/**
 * 潮汐工具类
 * 
 * 提供潮汐相关的静态计算方法
 * 主要由 FactorService 内部调用，也可供外部查询
 */
public final class TideSystem {
    
    private TideSystem() {
        // 工具类，禁止实例化
    }

    /**
     * 计算偏离基准值的百分比
     */
    public static double calculateDeviation(double currentFactor, double baseValue) {
        if (baseValue == 0) return 0;
        return (currentFactor - baseValue) / baseValue;
    }

    /**
     * 根据偏离度获取潮汐状态
     */
    public static TideStatus getStatusFromDeviation(double deviation) {
        double absDeviation = Math.abs(deviation);
        
        if (absDeviation <= 0.1) {
            return TideStatus.STABLE;
        } else if (absDeviation <= 0.3) {
            return TideStatus.DEVIATED;
        } else if (absDeviation <= 0.5) {
            return TideStatus.FLUCTUATING;
        } else {
            return TideStatus.VOLATILE;
        }
    }

    /**
     * 查找下一个潮汐峰值 tick
     */
    public static long findNextPeakTick(DimensionType dimensionType, long currentTick) {
        long period = dimensionType.periodTicks();
        long cyclePosition = currentTick % period;
        long quarterPeriod = period / 4;
        
        if (cyclePosition < quarterPeriod) {
            return currentTick + (quarterPeriod - cyclePosition);
        } else {
            return currentTick + (period - cyclePosition) + quarterPeriod;
        }
    }

    /**
     * 查找下一个潮汐谷值 tick
     */
    public static long findNextTroughTick(DimensionType dimensionType, long currentTick) {
        long period = dimensionType.periodTicks();
        long cyclePosition = currentTick % period;
        long threeQuarterPeriod = (period * 3) / 4;
        
        if (cyclePosition < threeQuarterPeriod) {
            return currentTick + (threeQuarterPeriod - cyclePosition);
        } else {
            return currentTick + (period - cyclePosition) + threeQuarterPeriod;
        }
    }

    /**
     * 判断是否为爆发时间（Factor 处于高位）
     */
    public static boolean isOutbreakTime(ServerWorld world, FactorService service) {
        double deviation = service.getDeviation(world);
        return deviation > 0.5;
    }
    
    /**
     * 获取潮汐周期描述（用于调试）
     */
    public static String getCycleDescription(DimensionType type, long currentTick) {
        long period = type.periodTicks();
        long position = currentTick % period;
        double progress = (double) position / period * 100;
        double factor = type.calculateFactor(currentTick);
        
        return String.format("%s: factor=%.1f, cycle=%.1f%%, period=%d ticks",
            type.name(), factor, progress, period);
    }
}