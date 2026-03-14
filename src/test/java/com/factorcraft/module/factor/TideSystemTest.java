package com.factorcraft.module.factor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TideSystem 和潮汐相关测试
 */
public class TideSystemTest {

    @Test
    public void testDimensionTypeBaseValues() {
        // 测试基准值
        assertEquals(50, DimensionType.OVERWORLD.baseValue(), 0.001);
        assertEquals(80, DimensionType.NETHER.baseValue(), 0.001);
        assertEquals(20, DimensionType.END.baseValue(), 0.001);
    }
    
    @Test
    public void testDimensionTypeAmplitude() {
        // 测试波动幅度
        assertEquals(12, DimensionType.OVERWORLD.amplitude(), 0.001);
        assertEquals(8, DimensionType.NETHER.amplitude(), 0.001);
        assertEquals(5, DimensionType.END.amplitude(), 0.001);
    }
    
    @Test
    public void testDimensionTypePeriod() {
        // 测试周期（已减半）
        assertEquals(96000, DimensionType.OVERWORLD.periodTicks());
        assertEquals(48000, DimensionType.NETHER.periodTicks());
        assertEquals(144000, DimensionType.END.periodTicks());
    }

    @Test
    public void testCalculateFactor() {
        long tick0 = 0;
        // 在 tick 0 时，sin(0) = 0，所以 Factor = baseValue
        assertEquals(50, DimensionType.OVERWORLD.calculateFactor(tick0), 0.001);
        assertEquals(80, DimensionType.NETHER.calculateFactor(tick0), 0.001);
        assertEquals(20, DimensionType.END.calculateFactor(tick0), 0.001);
    }
    
    @Test
    public void testCalculateFactorAtPeak() {
        // 在 1/4 周期时，sin(π/2) = 1，所以 Factor = baseValue + amplitude
        long quarterPeriod = DimensionType.OVERWORLD.periodTicks() / 4;
        double expected = 50 + 12; // 62
        assertEquals(expected, DimensionType.OVERWORLD.calculateFactor(quarterPeriod), 0.001);
    }
    
    @Test
    public void testCalculateFactorAtTrough() {
        // 在 3/4 周期时，sin(3π/2) = -1，所以 Factor = baseValue - amplitude
        long threeQuarterPeriod = DimensionType.OVERWORLD.periodTicks() * 3 / 4;
        double expected = 50 - 12; // 38
        assertEquals(expected, DimensionType.OVERWORLD.calculateFactor(threeQuarterPeriod), 0.001);
    }

    @Test
    public void testCalculateDeviation() {
        // 测试偏离度计算
        assertEquals(0.0, TideSystem.calculateDeviation(50, 50), 0.001); // 无偏离
        assertEquals(0.2, TideSystem.calculateDeviation(60, 50), 0.001); // +20% 偏离
        assertEquals(-0.2, TideSystem.calculateDeviation(40, 50), 0.001); // -20% 偏离
    }

    @Test
    public void testGetStatusFromDeviation() {
        // 测试状态判断
        assertEquals(TideStatus.STABLE, TideSystem.getStatusFromDeviation(0.05)); // ±5%
        assertEquals(TideStatus.DEVIATED, TideSystem.getStatusFromDeviation(0.2)); // ±20%
        assertEquals(TideStatus.FLUCTUATING, TideSystem.getStatusFromDeviation(0.4)); // ±40%
        assertEquals(TideStatus.VOLATILE, TideSystem.getStatusFromDeviation(0.6)); // ±60%
    }
    
    @Test
    public void testTideStatusProperties() {
        // 测试状态属性
        assertTrue(TideStatus.STABLE.isStable());
        assertFalse(TideStatus.DEVIATED.isStable());
        
        assertFalse(TideStatus.STABLE.shouldTriggerEffects());
        assertTrue(TideStatus.VOLATILE.shouldTriggerEffects());
        
        assertEquals(0.0, TideStatus.STABLE.baseEffectChance(), 0.001);
        assertEquals(0.30, TideStatus.VOLATILE.baseEffectChance(), 0.001);
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
    public void testDimensionTypeFromKey() {
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("minecraft:overworld"));
        assertEquals(DimensionType.NETHER, DimensionType.fromKey("minecraft:the_nether"));
        assertEquals(DimensionType.END, DimensionType.fromKey("minecraft:the_end"));
        
        // 测试部分匹配
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("overworld"));
        assertEquals(DimensionType.NETHER, DimensionType.fromKey("the_nether"));
        
        // 测试默认值
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("unknown"));
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey(null));
    }
    
    @Test
    public void testTransferMultiplier() {
        // 测试跨维度传输倍率
        double overworldToNether = DimensionType.OVERWORLD.calculateTransferMultiplierTo(DimensionType.NETHER);
        assertEquals(50.0 / 80.0, overworldToNether, 0.001);
        
        double netherToOverworld = DimensionType.NETHER.calculateTransferMultiplierTo(DimensionType.OVERWORLD);
        assertEquals(80.0 / 50.0, netherToOverworld, 0.001);
    }
}