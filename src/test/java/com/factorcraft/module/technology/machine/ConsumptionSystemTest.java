package com.factorcraft.module.technology.machine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 消耗系统测试
 */
public class ConsumptionSystemTest {
    
    // ==================== 存储容量测试 ====================
    
    @Test
    @DisplayName("存储容量应随 Tier 增加")
    void testStorageCapacities() {
        assertTrue(ConsumptionConfig.getMaxStorage(1) < ConsumptionConfig.getMaxStorage(2));
        assertTrue(ConsumptionConfig.getMaxStorage(2) < ConsumptionConfig.getMaxStorage(3));
        assertTrue(ConsumptionConfig.getMaxStorage(3) < ConsumptionConfig.getMaxStorage(4));
        assertTrue(ConsumptionConfig.getMaxStorage(4) < ConsumptionConfig.getMaxStorage(5));
    }
    
    @Test
    @DisplayName("T1 存储应为 500")
    void testT1Storage() {
        assertEquals(500.0, ConsumptionConfig.getMaxStorage(1), 0.001);
    }
    
    @Test
    @DisplayName("T5 存储应为 10000")
    void testT5Storage() {
        assertEquals(10000.0, ConsumptionConfig.getMaxStorage(5), 0.001);
    }
    
    // ==================== 基础产出测试 ====================
    
    @Test
    @DisplayName("基础产出应随 Tier 增加")
    void testBaseOutputs() {
        assertTrue(ConsumptionConfig.getBaseOutput(1) < ConsumptionConfig.getBaseOutput(2));
        assertTrue(ConsumptionConfig.getBaseOutput(2) < ConsumptionConfig.getBaseOutput(3));
        assertTrue(ConsumptionConfig.getBaseOutput(3) < ConsumptionConfig.getBaseOutput(4));
        assertTrue(ConsumptionConfig.getBaseOutput(4) < ConsumptionConfig.getBaseOutput(5));
    }
    
    @Test
    @DisplayName("T1 基础产出应为 50")
    void testT1BaseOutput() {
        assertEquals(50.0, ConsumptionConfig.getBaseOutput(1), 0.001);
    }
    
    @Test
    @DisplayName("T5 基础产出应为 5000")
    void testT5BaseOutput() {
        assertEquals(5000.0, ConsumptionConfig.getBaseOutput(5), 0.001);
    }
    
    // ==================== 消耗时间测试 ====================
    
    @Test
    @DisplayName("消耗时间应随 Tier 增加")
    void testConsumeTimes() {
        assertTrue(ConsumptionConfig.getConsumeTime(1) < ConsumptionConfig.getConsumeTime(2));
        assertTrue(ConsumptionConfig.getConsumeTime(2) < ConsumptionConfig.getConsumeTime(3));
        assertTrue(ConsumptionConfig.getConsumeTime(3) < ConsumptionConfig.getConsumeTime(4));
        assertTrue(ConsumptionConfig.getConsumeTime(4) < ConsumptionConfig.getConsumeTime(5));
    }
    
    // ==================== 维度倍率测试 ====================
    
    @Test
    @DisplayName("主世界维度倍率应为 1.0")
    void testOverworldDimensionMultiplier() {
        assertEquals(1.0, ConsumptionConfig.getDimensionMultiplier("minecraft:overworld"), 0.001);
    }
    
    @Test
    @DisplayName("下界维度倍率应为 1.5")
    void testNetherDimensionMultiplier() {
        assertEquals(1.5, ConsumptionConfig.getDimensionMultiplier("minecraft:the_nether"), 0.001);
    }
    
    @Test
    @DisplayName("末地维度倍率应为 2.0")
    void testEndDimensionMultiplier() {
        assertEquals(2.0, ConsumptionConfig.getDimensionMultiplier("minecraft:the_end"), 0.001);
    }
    
    // ==================== 维度效率测试 ====================
    
    @Test
    @DisplayName("T1 在主世界应为 100% 效率")
    void testT1OverworldEfficiency() {
        assertEquals(1.0, ConsumptionConfig.getDimensionEfficiency("minecraft:overworld", 1), 0.001);
    }
    
    @Test
    @DisplayName("T1 在下界应受惩罚")
    void testT1NetherPenalty() {
        assertEquals(0.1, ConsumptionConfig.getDimensionEfficiency("minecraft:the_nether", 1), 0.001);
    }
    
    @Test
    @DisplayName("T2 在主世界和下界都允许 100% 效率")
    void testT2DualDimensionEfficiency() {
        assertEquals(1.0, ConsumptionConfig.getDimensionEfficiency("minecraft:overworld", 2), 0.001);
        assertEquals(1.0, ConsumptionConfig.getDimensionEfficiency("minecraft:the_nether", 2), 0.001);
    }
    
    @Test
    @DisplayName("T3 在下界应为 100% 效率")
    void testT3NetherEfficiency() {
        assertEquals(1.0, ConsumptionConfig.getDimensionEfficiency("minecraft:the_nether", 3), 0.001);
    }
    
    @Test
    @DisplayName("T4-T5 在末地应为 100% 效率")
    void testT4T5EndEfficiency() {
        assertEquals(1.0, ConsumptionConfig.getDimensionEfficiency("minecraft:the_end", 4), 0.001);
        assertEquals(1.0, ConsumptionConfig.getDimensionEfficiency("minecraft:the_end", 5), 0.001);
    }
    
    // ==================== 消耗配方测试 ====================
    
    @Test
    @DisplayName("石头配方应正确")
    void testStoneRecipe() {
        var recipe = ConsumptionConfig.getRecipeForInput("minecraft:stone");
        assertNotNull(recipe);
        assertEquals(16, recipe.inputCount());
        assertEquals(20, recipe.baseFactorOutput(), 0.001);
        assertEquals(1, recipe.minTier());
    }
    
    @Test
    @DisplayName("钻石配方应需要 T3")
    void testDiamondRecipe() {
        var recipe = ConsumptionConfig.getRecipeForInput("minecraft:diamond");
        assertNotNull(recipe);
        assertEquals(300, recipe.baseFactorOutput(), 0.001);
        assertEquals(3, recipe.minTier());
    }
    
    @Test
    @DisplayName("Factor Craft 材料配方应正确")
    void testFactorCraftMaterialRecipes() {
        var t1 = ConsumptionConfig.getRecipeForInput("factorcraft:dust_copper_ingot");
        assertNotNull(t1);
        assertEquals(10, t1.baseFactorOutput(), 0.001);
        
        var t5 = ConsumptionConfig.getRecipeForInput("factorcraft:void_crystal");
        assertNotNull(t5);
        assertEquals(100000, t5.baseFactorOutput(), 0.001);
        assertEquals(5, t5.minTier());
    }
    
    @Test
    @DisplayName("未知物品应返回 null")
    void testUnknownItem() {
        var recipe = ConsumptionConfig.getRecipeForInput("minecraft:unknown_item");
        assertNull(recipe);
    }
    
    @Test
    @DisplayName("canConsume 应正确判断")
    void testCanConsume() {
        // T1 可以消耗石头
        assertTrue(ConsumptionConfig.canConsume("minecraft:stone", 1));
        
        // T1 不能消耗钻石（需要 T3）
        assertFalse(ConsumptionConfig.canConsume("minecraft:diamond", 1));
        assertTrue(ConsumptionConfig.canConsume("minecraft:diamond", 3));
        
        // T3 不能消耗虚空结晶（需要 T5）
        assertFalse(ConsumptionConfig.canConsume("factorcraft:void_crystal", 3));
        assertTrue(ConsumptionConfig.canConsume("factorcraft:void_crystal", 5));
    }
    
    // ==================== 结构效率测试 ====================
    
    @Test
    @DisplayName("结构效率应正确增长")
    void testEfficiency() {
        assertEquals(1.0, ConsumptionConfig.getEfficiency(1), 0.001);
        assertEquals(1.2, ConsumptionConfig.getEfficiency(2), 0.001);
        assertEquals(1.5, ConsumptionConfig.getEfficiency(3), 0.001);
        assertEquals(1.8, ConsumptionConfig.getEfficiency(4), 0.001);
        assertEquals(2.0, ConsumptionConfig.getEfficiency(5), 0.001);
    }
    
    // ==================== 完整产出计算测试 ====================
    
    @Test
    @DisplayName("完整产出计算：T1 主世界消耗石头")
    void testFullOutputCalculation_T1_Overworld_Stone() {
        var recipe = ConsumptionConfig.getRecipeForInput("minecraft:stone");
        double output = ConsumptionConfig.calculateActualOutput(recipe, 1, "minecraft:overworld");
        
        // 基础 20 × 效率 1.0 × 维度倍率 1.0 × 维度效率 1.0 = 20
        assertEquals(20.0, output, 0.001);
    }
    
    @Test
    @DisplayName("完整产出计算：T3 下界消耗钻石")
    void testFullOutputCalculation_T3_Nether_Diamond() {
        var recipe = ConsumptionConfig.getRecipeForInput("minecraft:diamond");
        double output = ConsumptionConfig.calculateActualOutput(recipe, 3, "minecraft:the_nether");
        
        // 基础 300 × 效率 1.5 × 维度倍率 1.5 × 维度效率 1.0 = 675
        assertEquals(675.0, output, 0.001);
    }
    
    @Test
    @DisplayName("完整产出计算：T5 末地消耗虚空结晶")
    void testFullOutputCalculation_T5_End_VoidCrystal() {
        var recipe = ConsumptionConfig.getRecipeForInput("factorcraft:void_crystal");
        double output = ConsumptionConfig.calculateActualOutput(recipe, 5, "minecraft:the_end");
        
        // 基础 100000 × 效率 2.0 × 维度倍率 2.0 × 维度效率 1.0 = 400000
        assertEquals(400000.0, output, 0.001);
    }
    
    @Test
    @DisplayName("完整产出计算：错误维度惩罚")
    void testFullOutputCalculation_WrongDimension() {
        var recipe = ConsumptionConfig.getRecipeForInput("minecraft:diamond");
        double output = ConsumptionConfig.calculateActualOutput(recipe, 3, "minecraft:overworld");
        
        // T3 推荐下界，在主世界受惩罚
        // 基础 300 × 效率 1.5 × 维度倍率 1.0 × 维度效率 0.1 = 45
        assertEquals(45.0, output, 0.001);
    }
    
    // ==================== 配方数量测试 ====================
    
    @Test
    @DisplayName("配方总数应合理")
    void testRecipeCount() {
        assertTrue(ConsumptionConfig.CONSUMPTION_RECIPES.size() >= 15, 
            "应至少有 15 个消耗配方");
    }
    
    @Test
    @DisplayName("每个配方每物品产出应合理")
    void testFactorPerItem() {
        for (var recipe : ConsumptionConfig.CONSUMPTION_RECIPES.values()) {
            double perItem = recipe.getFactorPerItem();
            assertTrue(perItem > 0, "每物品产出应大于 0");
            assertTrue(perItem < 1000000, "每物品产出应小于 1000000");
        }
    }
}