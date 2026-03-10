package com.factorcraft.module.factor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TideSystem 单元测试
 */
public class TideSystemTest {

    @Test
    public void testCalculateCurrentFactor() {
        long tick0 = 0;
        assertEquals(0.5, TideSystem.calculateCurrentFactor(DimensionType.OVERWORLD, tick0), 0.001);
        assertEquals(1.5, TideSystem.calculateCurrentFactor(DimensionType.NETHER, tick0), 0.001);
        assertEquals(3.0, TideSystem.calculateCurrentFactor(DimensionType.END, tick0), 0.001);
    }

    @Test
    public void testCalculateDeviation() {
        // 测试偏离度计算
        assertEquals(0.0, TideSystem.calculateDeviation(0.5, 0.5), 0.001); // 无偏离
        assertEquals(0.2, TideSystem.calculateDeviation(0.6, 0.5), 0.001); // +20% 偏离
        assertEquals(-0.2, TideSystem.calculateDeviation(0.4, 0.5), 0.001); // -20% 偏离
    }

    @Test
    public void testGetStatusFromDeviation() {
        // 测试状态判断
        assertEquals(TideSystem.FactorStatus.STABLE, TideSystem.getStatusFromDeviation(0.05)); // ±5%
        assertEquals(TideSystem.FactorStatus.DEVIATED, TideSystem.getStatusFromDeviation(0.2)); // ±20%
        assertEquals(TideSystem.FactorStatus.FLUCTUATING, TideSystem.getStatusFromDeviation(0.4)); // ±40%
        assertEquals(TideSystem.FactorStatus.VOLATILE, TideSystem.getStatusFromDeviation(0.6)); // ±60%
    }

    @Test
    public void testPredictFutureFactor() {
        long currentTick = 0;
        long futureTick = DimensionType.OVERWORLD.periodTicks() / 4; // 1/4 周期后
        
        double predicted = TideSystem.predictFutureFactor(DimensionType.OVERWORLD, currentTick, futureTick);
        assertEquals(0.7, predicted, 0.001); // 峰值
    }

    @Test
    public void testCalculateChangeRate() {
        long fromTick = 0;
        long toTick = DimensionType.OVERWORLD.periodTicks() / 4;
        
        double rate = TideSystem.calculateChangeRate(DimensionType.OVERWORLD, fromTick, toTick);
        // 从 0.5 变化到 0.7，变化 0.2，时间是 48000 tick
        double expectedRate = (0.7 - 0.5) / 48000.0;
        assertEquals(expectedRate, rate, 0.0000001);
    }

    @Test
    public void testFindNextPeakTick() {
        long currentTick = 0;
        long nextPeak = TideSystem.findNextPeakTick(DimensionType.OVERWORLD, currentTick);
        long expectedPeak = DimensionType.OVERWORLD.periodTicks() / 4;
        assertEquals(expectedPeak, nextPeak);
    }

    @Test
    public void testFindNextTroughTick() {
        long currentTick = 0;
        long nextTrough = TideSystem.findNextTroughTick(DimensionType.OVERWORLD, currentTick);
        long expectedTrough = DimensionType.OVERWORLD.periodTicks() * 3 / 4;
        assertEquals(expectedTrough, nextTrough);
    }

    @Test
    public void testFactorStatusProbabilities() {
        // 验证各状态的灾害概率
        assertEquals(0.0, TideSystem.FactorStatus.STABLE.disasterProbability(), 0.001);
        assertEquals(0.05, TideSystem.FactorStatus.DEVIATED.disasterProbability(), 0.001);
        assertEquals(0.15, TideSystem.FactorStatus.FLUCTUATING.disasterProbability(), 0.001);
        assertEquals(0.30, TideSystem.FactorStatus.VOLATILE.disasterProbability(), 0.001);
    }
}
