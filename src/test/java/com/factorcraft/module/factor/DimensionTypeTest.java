package com.factorcraft.module.factor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DimensionType 单元测试
 */
public class DimensionTypeTest {

    @Test
    public void testBaseValues() {
        // 验证基准值正确
        assertEquals(0.5, DimensionType.OVERWORLD.baseValue(), 0.001);
        assertEquals(1.5, DimensionType.NETHER.baseValue(), 0.001);
        assertEquals(3.0, DimensionType.END.baseValue(), 0.001);
    }

    @Test
    public void testAmplitudes() {
        // 验证波动幅度正确
        assertEquals(0.2, DimensionType.OVERWORLD.amplitude(), 0.001);
        assertEquals(0.6, DimensionType.NETHER.amplitude(), 0.001);
        assertEquals(1.2, DimensionType.END.amplitude(), 0.001);
    }

    @Test
    public void testPeriods() {
        // 验证周期正确
        assertEquals(192000L, DimensionType.OVERWORLD.periodTicks());
        assertEquals(96000L, DimensionType.NETHER.periodTicks());
        assertEquals(288000L, DimensionType.END.periodTicks());
    }

    @Test
    public void testFactorRange() {
        // 验证 Factor 范围正确
        // 主世界：0.5 ± 0.2 = 0.3 ~ 0.7
        assertEquals(0.3, DimensionType.OVERWORLD.getMinFactor(), 0.001);
        assertEquals(0.7, DimensionType.OVERWORLD.getMaxFactor(), 0.001);
        
        // 下界：1.5 ± 0.6 = 0.9 ~ 2.1
        assertEquals(0.9, DimensionType.NETHER.getMinFactor(), 0.001);
        assertEquals(2.1, DimensionType.NETHER.getMaxFactor(), 0.001);
        
        // 末地：3.0 ± 1.2 = 1.8 ~ 4.2
        assertEquals(1.8, DimensionType.END.getMinFactor(), 0.001);
        assertEquals(4.2, DimensionType.END.getMaxFactor(), 0.001);
    }

    @Test
    public void testCalculateFactor() {
        // 测试 tick 0 时的 Factor 值（sin(0) = 0，应该等于基准值）
        long tick0 = 0;
        assertEquals(0.5, DimensionType.OVERWORLD.calculateFactor(tick0), 0.001);
        assertEquals(1.5, DimensionType.NETHER.calculateFactor(tick0), 0.001);
        assertEquals(3.0, DimensionType.END.calculateFactor(tick0), 0.001);
    }

    @Test
    public void testCalculateFactorAtPeak() {
        // 测试峰值时的 Factor 值（1/4 周期，sin(π/2) = 1）
        long peakTick = DimensionType.OVERWORLD.periodTicks() / 4;
        double peakFactor = DimensionType.OVERWORLD.calculateFactor(peakTick);
        assertEquals(0.7, peakFactor, 0.001); // 0.5 + 0.2
    }

    @Test
    public void testCalculateFactorAtTrough() {
        // 测试谷值时的 Factor 值（3/4 周期，sin(3π/2) = -1）
        long troughTick = DimensionType.OVERWORLD.periodTicks() * 3 / 4;
        double troughFactor = DimensionType.OVERWORLD.calculateFactor(troughTick);
        assertEquals(0.3, troughFactor, 0.001); // 0.5 - 0.2
    }

    @Test
    public void testTransferMultiplier() {
        // 测试传输倍率
        // 下界→主世界：1.5 / 0.5 = 3.0
        assertEquals(3.0, DimensionType.NETHER.calculateTransferMultiplierTo(DimensionType.OVERWORLD), 0.001);
        
        // 末地→主世界：3.0 / 0.5 = 6.0
        assertEquals(6.0, DimensionType.END.calculateTransferMultiplierTo(DimensionType.OVERWORLD), 0.001);
        
        // 末地→下界：3.0 / 1.5 = 2.0
        assertEquals(2.0, DimensionType.END.calculateTransferMultiplierTo(DimensionType.NETHER), 0.001);
    }

    @Test
    public void testFromKey() {
        // 测试从 key 获取 DimensionType
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("overworld"));
        assertEquals(DimensionType.NETHER, DimensionType.fromKey("the_nether"));
        assertEquals(DimensionType.END, DimensionType.fromKey("the_end"));
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("unknown")); // 默认
    }
}
