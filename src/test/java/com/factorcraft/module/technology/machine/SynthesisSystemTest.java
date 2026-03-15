package com.factorcraft.module.technology.machine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 合成系统测试
 */
public class SynthesisSystemTest {
    
    // ==================== 合成时间测试 ====================
    
    @Test
    @DisplayName("合成时间应随 Tier 增加")
    void testCraftTimes() {
        assertTrue(SynthesisConfig.getCraftTime(1) < SynthesisConfig.getCraftTime(2));
        assertTrue(SynthesisConfig.getCraftTime(2) < SynthesisConfig.getCraftTime(3));
        assertTrue(SynthesisConfig.getCraftTime(3) < SynthesisConfig.getCraftTime(4));
        assertTrue(SynthesisConfig.getCraftTime(4) < SynthesisConfig.getCraftTime(5));
    }
    
    @Test
    @DisplayName("T1 合成时间应为 60 秒")
    void testT1CraftTime() {
        assertEquals(1200, SynthesisConfig.getCraftTime(1)); // 60 秒 = 1200 ticks
    }
    
    @Test
    @DisplayName("T5 合成时间应为 600 秒")
    void testT5CraftTime() {
        assertEquals(12000, SynthesisConfig.getCraftTime(5)); // 600 秒 = 12000 ticks
    }
    
    // ==================== Factor 缓冲区测试 ====================
    
    @Test
    @DisplayName("Factor 缓冲区应随 Tier 增加")
    void testBufferSizes() {
        assertTrue(SynthesisConfig.getMaxBuffer(1) < SynthesisConfig.getMaxBuffer(2));
        assertTrue(SynthesisConfig.getMaxBuffer(2) < SynthesisConfig.getMaxBuffer(3));
        assertTrue(SynthesisConfig.getMaxBuffer(3) < SynthesisConfig.getMaxBuffer(4));
        assertTrue(SynthesisConfig.getMaxBuffer(4) < SynthesisConfig.getMaxBuffer(5));
    }
    
    @Test
    @DisplayName("T1 缓冲区应能容纳一次 T1→T2 升级")
    void testT1BufferCanHoldOneUpgrade() {
        double buffer = SynthesisConfig.getMaxBuffer(1);
        double cost = SynthesisConfig.UPGRADE_RECIPES.get("t1_to_t2").factorCost();
        assertTrue(buffer >= cost, 
            "T1 缓冲区 " + buffer + " 应能容纳 T1→T2 升级成本 " + cost);
    }
    
    // ==================== 转换率测试 ====================
    
    @Test
    @DisplayName("转换率应正确设置")
    void testConversionRates() {
        assertEquals(2, SynthesisConfig.getConversionRate(1));   // 2:1
        assertEquals(4, SynthesisConfig.getConversionRate(2));   // 4:1
        assertEquals(6, SynthesisConfig.getConversionRate(3));   // 6:1
        assertEquals(10, SynthesisConfig.getConversionRate(4));  // 10:1
        assertEquals(15, SynthesisConfig.getConversionRate(5));  // 15:1
    }
    
    // ==================== 维度效率测试 ====================
    
    @Test
    @DisplayName("T1 在主世界应为 100% 效率")
    void testT1OverworldEfficiency() {
        assertEquals(1.0, SynthesisConfig.getDimensionEfficiency("minecraft:overworld", 1), 0.001);
    }
    
    @Test
    @DisplayName("T1 在下界应受惩罚")
    void testT1NetherPenalty() {
        assertEquals(0.1, SynthesisConfig.getDimensionEfficiency("minecraft:the_nether", 1), 0.001);
    }
    
    @Test
    @DisplayName("T2-T3 在下界应为 100% 效率")
    void testT2T3NetherEfficiency() {
        assertEquals(1.0, SynthesisConfig.getDimensionEfficiency("minecraft:the_nether", 2), 0.001);
        assertEquals(1.0, SynthesisConfig.getDimensionEfficiency("minecraft:the_nether", 3), 0.001);
    }
    
    @Test
    @DisplayName("T3 在末地也允许 100% 效率")
    void testT3EndEfficiency() {
        assertEquals(1.0, SynthesisConfig.getDimensionEfficiency("minecraft:the_end", 3), 0.001);
    }
    
    @Test
    @DisplayName("T4-T5 在末地应为 100% 效率")
    void testT4T5EndEfficiency() {
        assertEquals(1.0, SynthesisConfig.getDimensionEfficiency("minecraft:the_end", 4), 0.001);
        assertEquals(1.0, SynthesisConfig.getDimensionEfficiency("minecraft:the_end", 5), 0.001);
    }
    
    @Test
    @DisplayName("T4 在非末地应受惩罚")
    void testT4WrongDimensionPenalty() {
        assertEquals(0.1, SynthesisConfig.getDimensionEfficiency("minecraft:overworld", 4), 0.001);
        assertEquals(0.1, SynthesisConfig.getDimensionEfficiency("minecraft:the_nether", 4), 0.001);
    }
    
    // ==================== 升级配方测试 ====================
    
    @Test
    @DisplayName("T1→T2 配方应正确")
    void testT1ToT2Recipe() {
        var recipe = SynthesisConfig.getRecipeForTier(1);
        assertNotNull(recipe);
        assertEquals(1, recipe.fromTier());
        assertEquals(2, recipe.toTier());
        assertEquals(64, recipe.inputCount());
        assertEquals(32, recipe.outputCount());
        assertEquals(1000, recipe.factorCost(), 0.001);
    }
    
    @Test
    @DisplayName("T2→T3 配方应正确")
    void testT2ToT3Recipe() {
        var recipe = SynthesisConfig.getRecipeForTier(2);
        assertNotNull(recipe);
        assertEquals(2, recipe.fromTier());
        assertEquals(3, recipe.toTier());
        assertEquals(128, recipe.inputCount());
        assertEquals(32, recipe.outputCount());
        assertEquals(5000, recipe.factorCost(), 0.001);
    }
    
    @Test
    @DisplayName("T3→T4 配方应正确")
    void testT3ToT4Recipe() {
        var recipe = SynthesisConfig.getRecipeForTier(3);
        assertNotNull(recipe);
        assertEquals(3, recipe.fromTier());
        assertEquals(4, recipe.toTier());
        assertEquals(256, recipe.inputCount());
        assertEquals(42, recipe.outputCount());
        assertEquals(25000, recipe.factorCost(), 0.001);
    }
    
    @Test
    @DisplayName("T4→T5 配方应正确")
    void testT4ToT5Recipe() {
        var recipe = SynthesisConfig.getRecipeForTier(4);
        assertNotNull(recipe);
        assertEquals(4, recipe.fromTier());
        assertEquals(5, recipe.toTier());
        assertEquals(512, recipe.inputCount());
        assertEquals(51, recipe.outputCount());
        assertEquals(125000, recipe.factorCost(), 0.001);
    }
    
    // ==================== 效率测试 ====================
    
    @Test
    @DisplayName("结构效率应正确增长")
    void testEfficiency() {
        assertEquals(1.0, SynthesisConfig.getEfficiency(1), 0.001);
        assertEquals(1.1, SynthesisConfig.getEfficiency(2), 0.001);
        assertEquals(1.2, SynthesisConfig.getEfficiency(3), 0.001);
        assertEquals(1.3, SynthesisConfig.getEfficiency(4), 0.001);
        assertEquals(1.5, SynthesisConfig.getEfficiency(5), 0.001);
    }
    
    // ==================== 完整合成计算测试 ====================
    
    @Test
    @DisplayName("实际合成时间：T1 主世界")
    void testActualCraftTime_T1_Overworld() {
        int actualTime = SynthesisConfig.getActualCraftTime(1, "minecraft:overworld");
        int expected = (int) (1200 / (1.0 * 1.0)); // 基础时间 / 效率
        assertEquals(expected, actualTime);
    }
    
    @Test
    @DisplayName("实际合成时间：T1 下界（惩罚）")
    void testActualCraftTime_T1_Nether() {
        int actualTime = SynthesisConfig.getActualCraftTime(1, "minecraft:the_nether");
        int expected = (int) (1200 / (1.0 * 0.1)); // 基础时间 / (效率 × 维度惩罚)
        assertEquals(expected, actualTime);
        assertTrue(actualTime > 1200, "惩罚应增加合成时间");
    }
    
    @Test
    @DisplayName("实际合成时间：T5 末地")
    void testActualCraftTime_T5_End() {
        int actualTime = SynthesisConfig.getActualCraftTime(5, "minecraft:the_end");
        int expected = (int) (12000 / (1.5 * 1.0)); // T5 效率 1.5
        assertEquals(expected, actualTime);
        assertTrue(actualTime < 12000, "T5 效率应减少合成时间");
    }
    
    // ==================== Factor 成本分析 ====================
    
    @Test
    @DisplayName("每单位输入的 Factor 成本")
    void testFactorPerInput() {
        var r1 = SynthesisConfig.UPGRADE_RECIPES.get("t1_to_t2");
        assertEquals(1000.0 / 64, r1.getFactorPerInput(), 0.001);
        
        var r4 = SynthesisConfig.UPGRADE_RECIPES.get("t4_to_t5");
        assertEquals(125000.0 / 512, r4.getFactorPerInput(), 0.001);
    }
    
    @Test
    @DisplayName("升级路径的 Factor 成本递增")
    void testFactorCostProgression() {
        var r1 = SynthesisConfig.UPGRADE_RECIPES.get("t1_to_t2");
        var r2 = SynthesisConfig.UPGRADE_RECIPES.get("t2_to_t3");
        var r3 = SynthesisConfig.UPGRADE_RECIPES.get("t3_to_t4");
        var r4 = SynthesisConfig.UPGRADE_RECIPES.get("t4_to_t5");
        
        assertTrue(r1.factorCost() < r2.factorCost());
        assertTrue(r2.factorCost() < r3.factorCost());
        assertTrue(r3.factorCost() < r4.factorCost());
    }
}