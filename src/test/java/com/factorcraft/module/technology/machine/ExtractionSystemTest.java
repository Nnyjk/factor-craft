package com.factorcraft.module.technology.machine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 提取系统测试
 */
public class ExtractionSystemTest {
    
    // ==================== ExtractionConfig 测试 ====================
    
    @Test
    @DisplayName("基础提取速率应按设计倍数增长")
    void testBaseRates() {
        assertEquals(1.0, ExtractionConfig.getBaseRate(1), 0.001);
        assertEquals(2.0, ExtractionConfig.getBaseRate(2), 0.001);
        assertEquals(4.0, ExtractionConfig.getBaseRate(3), 0.001);
        assertEquals(8.0, ExtractionConfig.getBaseRate(4), 0.001);
        assertEquals(16.0, ExtractionConfig.getBaseRate(5), 0.001);
    }
    
    @Test
    @DisplayName("无效 Tier 应返回 T1 默认值")
    void testInvalidTierDefaults() {
        assertEquals(ExtractionConfig.BASE_RATE_T1, ExtractionConfig.getBaseRate(0), 0.001);
        assertEquals(ExtractionConfig.BASE_RATE_T1, ExtractionConfig.getBaseRate(6), 0.001);
        assertEquals(ExtractionConfig.BASE_RATE_T1, ExtractionConfig.getBaseRate(-1), 0.001);
    }
    
    @Test
    @DisplayName("存储容量应正确增长")
    void testMaxStorage() {
        assertTrue(ExtractionConfig.getMaxStorage(2) > ExtractionConfig.getMaxStorage(1));
        assertTrue(ExtractionConfig.getMaxStorage(3) > ExtractionConfig.getMaxStorage(2));
        assertTrue(ExtractionConfig.getMaxStorage(5) > 10000); // T5 应超过 10000
    }
    
    @Test
    @DisplayName("影响范围应正确设置")
    void testRanges() {
        assertEquals(1, ExtractionConfig.getRange(1));   // T1: 1 区块
        assertEquals(3, ExtractionConfig.getRange(2));   // T2: 3×3
        assertEquals(5, ExtractionConfig.getRange(3));   // T3: 5×5
        assertEquals(9, ExtractionConfig.getRange(4));   // T4: 9×9
        assertEquals(15, ExtractionConfig.getRange(5));  // T5: 15×15
    }
    
    @Test
    @DisplayName("结构效率应正确增长")
    void testEfficiency() {
        assertEquals(1.00, ExtractionConfig.getEfficiency(1), 0.001);
        assertEquals(1.20, ExtractionConfig.getEfficiency(2), 0.001);
        assertEquals(1.50, ExtractionConfig.getEfficiency(3), 0.001);
        assertEquals(1.80, ExtractionConfig.getEfficiency(4), 0.001);
        assertEquals(2.00, ExtractionConfig.getEfficiency(5), 0.001);
    }
    
    // ==================== 浓度系数测试 ====================
    
    @Test
    @DisplayName("高浓度应返回 1.2 倍系数")
    void testHighConcentrationCoefficient() {
        assertEquals(1.2, ExtractionConfig.getConcentrationCoefficient(100), 0.001);
        assertEquals(1.2, ExtractionConfig.getConcentrationCoefficient(60), 0.001);
        assertEquals(1.2, ExtractionConfig.getConcentrationCoefficient(50.1), 0.001);
    }
    
    @Test
    @DisplayName("正常浓度应返回 1.0 倍系数")
    void testNormalConcentrationCoefficient() {
        assertEquals(1.0, ExtractionConfig.getConcentrationCoefficient(50), 0.001);
        assertEquals(1.0, ExtractionConfig.getConcentrationCoefficient(40), 0.001);
        assertEquals(1.0, ExtractionConfig.getConcentrationCoefficient(30.1), 0.001);
    }
    
    @Test
    @DisplayName("低浓度应返回 0.8 倍系数")
    void testLowConcentrationCoefficient() {
        assertEquals(0.8, ExtractionConfig.getConcentrationCoefficient(30), 0.001);
        assertEquals(0.8, ExtractionConfig.getConcentrationCoefficient(20), 0.001);
        assertEquals(0.8, ExtractionConfig.getConcentrationCoefficient(10.1), 0.001);
    }
    
    @Test
    @DisplayName("枯竭浓度应返回 0.5 倍系数")
    void testDepletedConcentrationCoefficient() {
        assertEquals(0.5, ExtractionConfig.getConcentrationCoefficient(10), 0.001);
        assertEquals(0.5, ExtractionConfig.getConcentrationCoefficient(5), 0.001);
        assertEquals(0.5, ExtractionConfig.getConcentrationCoefficient(1), 0.001);
    }
    
    // ==================== 维度效率测试 ====================
    
    @Test
    @DisplayName("T1-T2 在主世界应为 100% 效率")
    void testT1T2OverworldEfficiency() {
        assertEquals(1.0, ExtractionConfig.getDimensionEfficiency("minecraft:overworld", 1), 0.001);
        assertEquals(1.0, ExtractionConfig.getDimensionEfficiency("minecraft:overworld", 2), 0.001);
    }
    
    @Test
    @DisplayName("T1-T2 在非主世界应受惩罚")
    void testT1T2WrongDimensionPenalty() {
        assertEquals(0.1, ExtractionConfig.getDimensionEfficiency("minecraft:the_nether", 1), 0.001);
        assertEquals(0.1, ExtractionConfig.getDimensionEfficiency("minecraft:the_end", 2), 0.001);
    }
    
    @Test
    @DisplayName("T3-T4 在下界应为 100% 效率")
    void testT3T4NetherEfficiency() {
        assertEquals(1.0, ExtractionConfig.getDimensionEfficiency("minecraft:the_nether", 3), 0.001);
        assertEquals(1.0, ExtractionConfig.getDimensionEfficiency("minecraft:the_nether", 4), 0.001);
    }
    
    @Test
    @DisplayName("T5 在末地应为 100% 效率")
    void testT5EndEfficiency() {
        assertEquals(1.0, ExtractionConfig.getDimensionEfficiency("minecraft:the_end", 5), 0.001);
    }
    
    @Test
    @DisplayName("T5 在非末地应受惩罚")
    void testT5WrongDimensionPenalty() {
        assertEquals(0.1, ExtractionConfig.getDimensionEfficiency("minecraft:overworld", 5), 0.001);
        assertEquals(0.1, ExtractionConfig.getDimensionEfficiency("minecraft:the_nether", 5), 0.001);
    }
    
    // ==================== 完整提取计算测试 ====================
    
    @Test
    @DisplayName("完整提取计算：T1 主世界 高浓度")
    void testFullExtractionCalculation_T1_Overworld_HighConc() {
        // T1 星辰收集器在主世界，高浓度区块
        double baseRate = ExtractionConfig.getBaseRate(1);              // 1.0
        double activity = 0.5;  // 主世界基准活性
        double concCoeff = ExtractionConfig.getConcentrationCoefficient(60);  // 1.2
        double structEff = ExtractionConfig.getEfficiency(1);           // 1.0
        double dimEff = ExtractionConfig.getDimensionEfficiency("minecraft:overworld", 1); // 1.0
        
        double actualExtract = baseRate * activity * concCoeff * structEff * dimEff;
        
        // 1.0 × 0.5 × 1.2 × 1.0 × 1.0 = 0.6
        assertEquals(0.6, actualExtract, 0.001);
    }
    
    @Test
    @DisplayName("完整提取计算：T3 下界 正常浓度")
    void testFullExtractionCalculation_T3_Nether_NormalConc() {
        // T3 星云汲取器在下界，正常浓度区块
        double baseRate = ExtractionConfig.getBaseRate(3);              // 4.0
        double activity = 1.5;  // 下界基准活性
        double concCoeff = ExtractionConfig.getConcentrationCoefficient(40);  // 1.0
        double structEff = ExtractionConfig.getEfficiency(3);           // 1.5
        double dimEff = ExtractionConfig.getDimensionEfficiency("minecraft:the_nether", 3); // 1.0
        
        double actualExtract = baseRate * activity * concCoeff * structEff * dimEff;
        
        // 4.0 × 1.5 × 1.0 × 1.5 × 1.0 = 9.0
        assertEquals(9.0, actualExtract, 0.001);
    }
    
    @Test
    @DisplayName("完整提取计算：T2 在错误维度应大幅降低")
    void testFullExtractionCalculation_WrongDimension() {
        // T2 星辰阵列在末地（错误维度）
        double baseRate = ExtractionConfig.getBaseRate(2);              // 2.0
        double activity = 3.0;  // 末地高活性
        double concCoeff = ExtractionConfig.getConcentrationCoefficient(80);  // 1.2
        double structEff = ExtractionConfig.getEfficiency(2);           // 1.2
        double dimEff = ExtractionConfig.getDimensionEfficiency("minecraft:the_end", 2); // 0.1 (惩罚)
        
        double actualExtract = baseRate * activity * concCoeff * structEff * dimEff;
        
        // 2.0 × 3.0 × 1.2 × 1.2 × 0.1 = 0.864
        assertEquals(0.864, actualExtract, 0.001);
    }
    
    @Test
    @DisplayName("最低浓度阈值检查")
    void testMinConcentrationThreshold() {
        assertTrue(5.0 >= ExtractionConfig.MIN_CONCENTRATION_THRESHOLD);
        assertTrue(4.9 < ExtractionConfig.MIN_CONCENTRATION_THRESHOLD);
    }
}