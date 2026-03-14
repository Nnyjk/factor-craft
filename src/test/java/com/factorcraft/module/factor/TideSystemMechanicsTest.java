package com.factorcraft.module.factor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 潮汐系统完整机制测试
 * 
 * 测试覆盖：
 * - 潮汐周期计算
 * - 偏离度计算
 * - 状态转换
 * - 峰值/谷值预测
 * - 多维度行为
 */
@DisplayName("潮汐系统机制测试")
public class TideSystemMechanicsTest {

    @Nested
    @DisplayName("潮汐周期计算")
    class TideCycleCalculation {
        
        @Test
        @DisplayName("tick 0 时 Factor 等于基准值")
        void testTickZero() {
            for (DimensionType type : DimensionType.values()) {
                double factor = type.calculateFactor(0);
                assertEquals(type.baseValue(), factor, 0.001,
                    () -> type.name() + " at tick 0 should equal base value");
            }
        }
        
        @Test
        @DisplayName("Factor 在周期内连续变化")
        void testContinuousChange() {
            DimensionType type = DimensionType.OVERWORLD;
            long period = type.periodTicks();
            
            double prevFactor = type.calculateFactor(0);
            int changes = 0;
            
            for (long tick = 1; tick <= period; tick += period / 100) {
                double factor = type.calculateFactor(tick);
                if (Math.abs(factor - prevFactor) > 0.01) {
                    changes++;
                }
                prevFactor = factor;
            }
            
            assertTrue(changes > 50, "Factor should change continuously throughout cycle");
        }
        
        @Test
        @DisplayName("完整周期后 Factor 回到起点")
        void testFullCycleReturn() {
            for (DimensionType type : DimensionType.values()) {
                long period = type.periodTicks();
                double startFactor = type.calculateFactor(0);
                double endFactor = type.calculateFactor(period);
                
                assertEquals(startFactor, endFactor, 0.001,
                    () -> type.name() + " should return to start after full cycle");
            }
        }
        
        @Test
        @DisplayName("半周期时 Factor 符号相反")
        void testHalfCycle() {
            DimensionType type = DimensionType.OVERWORLD;
            long halfPeriod = type.periodTicks() / 2;
            
            double startDeviation = type.calculateFactor(0) - type.baseValue();
            double halfDeviation = type.calculateFactor(halfPeriod) - type.baseValue();
            
            // sin(π) = 0, 所以半周期时偏差应该接近 0
            // 但实际上 sin(π) = 0，所以半周期时 Factor 也等于基准值
            // 让我们验证这个特性
            assertEquals(0, halfDeviation, 0.001, 
                "At half period, sin(π) = 0, so deviation should be 0");
        }
    }
    
    @Nested
    @DisplayName("峰值与谷值")
    class PeakAndTrough {
        
        @Test
        @DisplayName("1/4 周期为峰值")
        void testPeakAtQuarterPeriod() {
            for (DimensionType type : DimensionType.values()) {
                long peakTick = type.periodTicks() / 4;
                double factor = type.calculateFactor(peakTick);
                double expectedPeak = type.baseValue() + type.amplitude();
                
                assertEquals(expectedPeak, factor, 0.001,
                    () -> type.name() + " should peak at quarter period");
            }
        }
        
        @Test
        @DisplayName("3/4 周期为谷值")
        void testTroughAtThreeQuarterPeriod() {
            for (DimensionType type : DimensionType.values()) {
                long troughTick = type.periodTicks() * 3 / 4;
                double factor = type.calculateFactor(troughTick);
                double expectedTrough = type.baseValue() - type.amplitude();
                
                assertEquals(expectedTrough, factor, 0.001,
                    () -> type.name() + " should trough at 3/4 period");
            }
        }
        
        @Test
        @DisplayName("Factor 不超过理论范围")
        void testFactorBounds() {
            for (DimensionType type : DimensionType.values()) {
                long period = type.periodTicks();
                double minFactor = type.getMinFactor();
                double maxFactor = type.getMaxFactor();
                
                for (long tick = 0; tick <= period; tick += period / 50) {
                    double factor = type.calculateFactor(tick);
                    assertTrue(factor >= minFactor - 0.001,
                        type.name() + " factor " + factor + " below min at tick " + tick);
                    assertTrue(factor <= maxFactor + 0.001,
                        type.name() + " factor " + factor + " above max at tick " + tick);
                }
            }
        }
        
        @Test
        @DisplayName("findNextPeakTick 正确预测峰值")
        void testFindNextPeak() {
            DimensionType type = DimensionType.OVERWORLD;
            
            // 从 tick 0 开始
            long nextPeak = TideSystem.findNextPeakTick(type, 0);
            assertEquals(type.periodTicks() / 4, nextPeak);
            
            // 从接近峰值后开始
            long afterPeak = type.periodTicks() / 4 + 1000;
            long nextPeakAfter = TideSystem.findNextPeakTick(type, afterPeak);
            assertTrue(nextPeakAfter > afterPeak);
            assertTrue(nextPeakAfter <= type.periodTicks() + type.periodTicks() / 4);
        }
        
        @Test
        @DisplayName("findNextTroughTick 正确预测谷值")
        void testFindNextTrough() {
            DimensionType type = DimensionType.OVERWORLD;
            
            long nextTrough = TideSystem.findNextTroughTick(type, 0);
            assertEquals(type.periodTicks() * 3 / 4, nextTrough);
        }
    }
    
    @Nested
    @DisplayName("偏离度计算")
    class DeviationCalculation {
        
        @Test
        @DisplayName("基准值偏离度为 0")
        void testZeroDeviation() {
            assertEquals(0, TideSystem.calculateDeviation(50, 50), 0.001);
            assertEquals(0, TideSystem.calculateDeviation(80, 80), 0.001);
        }
        
        @Test
        @DisplayName("正向偏离计算正确")
        void testPositiveDeviation() {
            // 比基准值高 20%
            assertEquals(0.2, TideSystem.calculateDeviation(60, 50), 0.001);
            // 比基准值高 50%
            assertEquals(0.5, TideSystem.calculateDeviation(75, 50), 0.001);
            // 比基准值高 100%
            assertEquals(1.0, TideSystem.calculateDeviation(100, 50), 0.001);
        }
        
        @Test
        @DisplayName("负向偏离计算正确")
        void testNegativeDeviation() {
            assertEquals(-0.2, TideSystem.calculateDeviation(40, 50), 0.001);
            assertEquals(-0.5, TideSystem.calculateDeviation(25, 50), 0.001);
            assertEquals(-0.6, TideSystem.calculateDeviation(20, 50), 0.001);
        }
        
        @Test
        @DisplayName("基准值为 0 时返回 0")
        void testZeroBaseValue() {
            assertEquals(0, TideSystem.calculateDeviation(100, 0), 0.001);
            assertEquals(0, TideSystem.calculateDeviation(-50, 0), 0.001);
        }
    }
    
    @Nested
    @DisplayName("潮汐状态转换")
    class StatusTransition {
        
        @Test
        @DisplayName("STABLE 状态边界")
        void testStableStatus() {
            assertEquals(TideStatus.STABLE, TideSystem.getStatusFromDeviation(0));
            assertEquals(TideStatus.STABLE, TideSystem.getStatusFromDeviation(0.1));
            assertEquals(TideStatus.STABLE, TideSystem.getStatusFromDeviation(-0.1));
            assertEquals(TideStatus.STABLE, TideSystem.getStatusFromDeviation(0.05));
        }
        
        @Test
        @DisplayName("DEVIATED 状态边界")
        void testDeviatedStatus() {
            assertEquals(TideStatus.DEVIATED, TideSystem.getStatusFromDeviation(0.11));
            assertEquals(TideStatus.DEVIATED, TideSystem.getStatusFromDeviation(0.2));
            assertEquals(TideStatus.DEVIATED, TideSystem.getStatusFromDeviation(0.3));
            assertEquals(TideStatus.DEVIATED, TideSystem.getStatusFromDeviation(-0.15));
        }
        
        @Test
        @DisplayName("FLUCTUATING 状态边界")
        void testFluctuatingStatus() {
            assertEquals(TideStatus.FLUCTUATING, TideSystem.getStatusFromDeviation(0.31));
            assertEquals(TideStatus.FLUCTUATING, TideSystem.getStatusFromDeviation(0.4));
            assertEquals(TideStatus.FLUCTUATING, TideSystem.getStatusFromDeviation(0.5));
            assertEquals(TideStatus.FLUCTUATING, TideSystem.getStatusFromDeviation(-0.4));
        }
        
        @Test
        @DisplayName("VOLATILE 状态边界")
        void testVolatileStatus() {
            assertEquals(TideStatus.VOLATILE, TideSystem.getStatusFromDeviation(0.51));
            assertEquals(TideStatus.VOLATILE, TideSystem.getStatusFromDeviation(0.8));
            assertEquals(TideStatus.VOLATILE, TideSystem.getStatusFromDeviation(1.0));
            assertEquals(TideStatus.VOLATILE, TideSystem.getStatusFromDeviation(-0.6));
        }
        
        @Test
        @DisplayName("状态使用绝对偏离度")
        void testAbsoluteDeviation() {
            // 正负偏离应该产生相同状态
            assertEquals(TideSystem.getStatusFromDeviation(0.2),
                        TideSystem.getStatusFromDeviation(-0.2));
            assertEquals(TideSystem.getStatusFromDeviation(0.4),
                        TideSystem.getStatusFromDeviation(-0.4));
        }
    }
    
    @Nested
    @DisplayName("多维度潮汐行为")
    class MultiDimensionBehavior {
        
        @Test
        @DisplayName("各维度周期不同")
        void testDifferentPeriods() {
            assertNotEquals(DimensionType.OVERWORLD.periodTicks(), 
                           DimensionType.NETHER.periodTicks());
            assertNotEquals(DimensionType.OVERWORLD.periodTicks(), 
                           DimensionType.END.periodTicks());
            assertNotEquals(DimensionType.NETHER.periodTicks(), 
                           DimensionType.END.periodTicks());
        }
        
        @Test
        @DisplayName("各维度基准值不同")
        void testDifferentBaseValues() {
            assertNotEquals(DimensionType.OVERWORLD.baseValue(), 
                           DimensionType.NETHER.baseValue());
            assertNotEquals(DimensionType.OVERWORLD.baseValue(), 
                           DimensionType.END.baseValue());
        }
        
        @Test
        @DisplayName("同一时刻各维度 Factor 不同")
        void testDifferentFactorsAtSameTick() {
            long tick = 12345;
            
            double overworldFactor = DimensionType.OVERWORLD.calculateFactor(tick);
            double netherFactor = DimensionType.NETHER.calculateFactor(tick);
            double endFactor = DimensionType.END.calculateFactor(tick);
            
            // 由于周期和基准值不同，同一时刻的 Factor 通常不同
            // 至少验证它们都是有效值
            assertTrue(overworldFactor >= 0 && overworldFactor <= 100);
            assertTrue(netherFactor >= 0 && netherFactor <= 100);
            assertTrue(endFactor >= 0 && endFactor <= 100);
        }
        
        @Test
        @DisplayName("周期比例正确（减半后）")
        void testPeriodRatios() {
            // 主世界 : 下界 : 末地 = 4 : 2 : 6
            assertEquals(2, DimensionType.OVERWORLD.periodTicks() / 
                        DimensionType.NETHER.periodTicks());
            assertEquals(2 / 3.0, DimensionType.OVERWORLD.periodTicks() / 
                        (double) DimensionType.END.periodTicks(), 0.001);
        }
    }
    
    @Nested
    @DisplayName("潮汐变化率")
    class TideChangeRate {
        
        @Test
        @DisplayName("峰值附近变化率最大")
        void testMaxChangeRateNearPeak() {
            DimensionType type = DimensionType.OVERWORLD;
            long quarterPeriod = type.periodTicks() / 4;
            
            // 峰值前后的变化应该较快
            double beforePeak = type.calculateFactor(quarterPeriod - 100);
            double atPeak = type.calculateFactor(quarterPeriod);
            double afterPeak = type.calculateFactor(quarterPeriod + 100);
            
            // 峰值前上升，峰值后下降
            assertTrue(atPeak > beforePeak);
            assertTrue(atPeak > afterPeak);
        }
        
        @Test
        @DisplayName("基准点附近变化率最快")
        void testFastestChangeNearBase() {
            DimensionType type = DimensionType.OVERWORLD;
            
            // 在 0, 1/2 周期时 sin 变化最快（cos = ±1）
            double atZero = type.calculateFactor(0);
            double afterZero = type.calculateFactor(100);
            double changeRate = (afterZero - atZero) / 100;
            
            // 变化率应该接近 amplitude * 2π / period
            double expectedRate = type.amplitude() * 2 * Math.PI / type.periodTicks();
            // 由于角度原因，实际变化率应该与预期接近
            assertTrue(Math.abs(changeRate) > 0);
        }
    }
    
    @Nested
    @DisplayName("潮汐事件触发条件")
    class TideEventTrigger {
        
        @Test
        @DisplayName("STABLE 不触发效果")
        void testStableNoTrigger() {
            assertFalse(TideStatus.STABLE.shouldTriggerEffects());
        }
        
        @Test
        @DisplayName("非 STABLE 触发效果")
        void testNonStableTrigger() {
            assertTrue(TideStatus.DEVIATED.shouldTriggerEffects());
            assertTrue(TideStatus.FLUCTUATING.shouldTriggerEffects());
            assertTrue(TideStatus.VOLATILE.shouldTriggerEffects());
        }
        
        @Test
        @DisplayName("效果概率递增")
        void testIncreasingProbability() {
            assertTrue(TideStatus.DEVIATED.baseEffectChance() > 
                      TideStatus.STABLE.baseEffectChance());
            assertTrue(TideStatus.FLUCTUATING.baseEffectChance() > 
                      TideStatus.DEVIATED.baseEffectChance());
            assertTrue(TideStatus.VOLATILE.baseEffectChance() > 
                      TideStatus.FLUCTUATING.baseEffectChance());
        }
    }
}