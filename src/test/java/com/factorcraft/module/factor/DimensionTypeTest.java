package com.factorcraft.module.factor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DimensionType 单元测试
 */
public class DimensionTypeTest {

    @Test
    public void testBaseValues() {
        // 验证基准值正确 (0-100 范围)
        assertEquals(50, DimensionType.OVERWORLD.baseValue(), 0.001);
        assertEquals(80, DimensionType.NETHER.baseValue(), 0.001);
        assertEquals(20, DimensionType.END.baseValue(), 0.001);
    }

    @Test
    public void testAmplitudes() {
        // 验证波动幅度正确
        assertEquals(12, DimensionType.OVERWORLD.amplitude(), 0.001);
        assertEquals(8, DimensionType.NETHER.amplitude(), 0.001);
        assertEquals(5, DimensionType.END.amplitude(), 0.001);
    }

    @Test
    public void testPeriods() {
        // 验证周期正确（已减半）
        assertEquals(96000L, DimensionType.OVERWORLD.periodTicks());   // 4 游戏日
        assertEquals(48000L, DimensionType.NETHER.periodTicks());      // 2 游戏日
        assertEquals(144000L, DimensionType.END.periodTicks());        // 6 游戏日
    }

    @Test
    public void testFactorRange() {
        // 验证 Factor 范围正确
        // 主世界：50 ± 12 = 38 ~ 62
        assertEquals(38, DimensionType.OVERWORLD.getMinFactor(), 0.001);
        assertEquals(62, DimensionType.OVERWORLD.getMaxFactor(), 0.001);
        
        // 下界：80 ± 8 = 72 ~ 88
        assertEquals(72, DimensionType.NETHER.getMinFactor(), 0.001);
        assertEquals(88, DimensionType.NETHER.getMaxFactor(), 0.001);
        
        // 末地：20 ± 5 = 15 ~ 25
        assertEquals(15, DimensionType.END.getMinFactor(), 0.001);
        assertEquals(25, DimensionType.END.getMaxFactor(), 0.001);
    }

    @Test
    public void testCalculateFactor() {
        // 测试 tick 0 时的 Factor 值（sin(0) = 0，应该等于基准值）
        long tick0 = 0;
        assertEquals(50, DimensionType.OVERWORLD.calculateFactor(tick0), 0.001);
        assertEquals(80, DimensionType.NETHER.calculateFactor(tick0), 0.001);
        assertEquals(20, DimensionType.END.calculateFactor(tick0), 0.001);
    }

    @Test
    public void testCalculateFactorAtPeak() {
        // 测试峰值时的 Factor 值（1/4 周期，sin(π/2) = 1）
        long peakTick = DimensionType.OVERWORLD.periodTicks() / 4;
        double peakFactor = DimensionType.OVERWORLD.calculateFactor(peakTick);
        assertEquals(62, peakFactor, 0.001); // 50 + 12
    }

    @Test
    public void testCalculateFactorAtTrough() {
        // 测试谷值时的 Factor 值（3/4 周期，sin(3π/2) = -1）
        long troughTick = DimensionType.OVERWORLD.periodTicks() * 3 / 4;
        double troughFactor = DimensionType.OVERWORLD.calculateFactor(troughTick);
        assertEquals(38, troughFactor, 0.001); // 50 - 12
    }

    @Test
    public void testTransferMultiplier() {
        // 测试传输倍率
        // 下界→主世界：80 / 50 = 1.6
        assertEquals(1.6, DimensionType.NETHER.calculateTransferMultiplierTo(DimensionType.OVERWORLD), 0.001);
        
        // 主世界→末地：50 / 20 = 2.5
        assertEquals(2.5, DimensionType.OVERWORLD.calculateTransferMultiplierTo(DimensionType.END), 0.001);
        
        // 末地→下界：20 / 80 = 0.25
        assertEquals(0.25, DimensionType.END.calculateTransferMultiplierTo(DimensionType.NETHER), 0.001);
    }

    @Test
    public void testFromKey() {
        // 测试从 key 获取 DimensionType
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("minecraft:overworld"));
        assertEquals(DimensionType.NETHER, DimensionType.fromKey("minecraft:the_nether"));
        assertEquals(DimensionType.END, DimensionType.fromKey("minecraft:the_end"));
        
        // 测试部分匹配
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("overworld"));
        assertEquals(DimensionType.NETHER, DimensionType.fromKey("the_nether"));
        
        // 默认值
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey("unknown"));
        assertEquals(DimensionType.OVERWORLD, DimensionType.fromKey(null));
    }
}