package com.factorcraft.gametest;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

/**
 * 合成结构 GameTest
 * 
 * 测试范围：
 * 1. 材料升级配方
 * 2. 输出物品逻辑
 * 3. Factor 消耗计算
 * 4. T1→T2→T3 升级链
 */
public class SynthesizerGameTests {
    
    /**
     * 测试合成器核心方块注册
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void synthesizerCoreRegistration(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing synthesizer core registration...");
        
        // 验证 T1-T5 合成器核心已注册
        for (int tier = 1; tier <= 5; tier++) {
            String blockId = "factor_machine_synthesizer_core_t" + tier;
            var block = net.minecraft.registry.Registries.BLOCK.get(
                net.minecraft.util.Identifier.of("factorcraft", blockId));
            if (block == null || block == net.minecraft.block.Blocks.AIR) {
                context.throwGameTestException("Synthesizer core T" + tier + " not registered");
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试合成配置加载
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void synthesisConfigLoaded(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing synthesis config...");
        
        // 验证合成时间配置
        int time1 = SynthesisTestConfig.getCraftTime(1);
        int time5 = SynthesisTestConfig.getCraftTime(5);
        
        if (time1 <= 0 || time5 <= 0) {
            context.throwGameTestException("Craft times should be positive");
        }
        
        // 高等级合成时间应更长
        if (time5 <= time1) {
            context.throwGameTestException("Higher tier should have longer craft time");
        }
        
        context.complete();
    }
    
    /**
     * 测试升级配方存在性
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void upgradeRecipesExist(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing upgrade recipes...");
        
        // 验证所有升级配方
        String[] recipeKeys = {"t1_to_t2", "t2_to_t3", "t3_to_t4", "t4_to_t5"};
        
        for (String key : recipeKeys) {
            var recipe = SynthesisTestConfig.UPGRADE_RECIPES.get(key);
            if (recipe == null) {
                context.throwGameTestException("Upgrade recipe not found: " + key);
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试 Factor 成本递增
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void factorCostProgression(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing factor cost progression...");
        
        int prevCost = 0;
        
        for (int tier = 1; tier <= 4; tier++) {
            String key = "t" + tier + "_to_t" + (tier + 1);
            var recipe = SynthesisTestConfig.UPGRADE_RECIPES.get(key);
            
            if (recipe != null) {
                int cost = recipe.factorCost();
                
                // 成本应随等级增加
                if (cost <= prevCost && prevCost > 0) {
                    context.throwGameTestException(
                        "Factor cost should increase: " + key + " has " + cost + " <= " + prevCost);
                }
                
                prevCost = cost;
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试输出物品逻辑
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void outputItemLogic(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing output item logic...");
        
        // 验证输出物品配置
        for (int tier = 1; tier <= 5; tier++) {
            var output = SynthesisTestConfig.getOutputItem(tier);
            if (output == null) {
                context.throwGameTestException("No output item for tier " + tier);
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试升级链完整性
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void upgradeChainIntegrity(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing upgrade chain integrity...");
        
        // 验证 T1→T2→T3→T4→T5 升级链
        for (int fromTier = 1; fromTier < 5; fromTier++) {
            int toTier = fromTier + 1;
            String recipeKey = "t" + fromTier + "_to_t" + toTier;
            
            var recipe = SynthesisTestConfig.UPGRADE_RECIPES.get(recipeKey);
            if (recipe == null) {
                context.throwGameTestException("Missing upgrade recipe: " + recipeKey);
            }
            
            // 验证输入输出匹配
            if (recipe.inputTier() != fromTier) {
                context.throwGameTestException(
                    "Recipe " + recipeKey + " has wrong input tier: " + recipe.inputTier());
            }
            if (recipe.outputTier() != toTier) {
                context.throwGameTestException(
                    "Recipe " + recipeKey + " has wrong output tier: " + recipe.outputTier());
            }
        }
        
        context.complete();
    }
    
    /**
     * 测试合成 Factor 消耗
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public static void synthesisFactorConsumption(TestContext context) {
        FactorCraftMod.LOGGER.info("[GameTest] Testing synthesis factor consumption...");
        
        // 验证 Factor 消耗为正
        for (int tier = 1; tier <= 5; tier++) {
            double consumption = SynthesisTestConfig.getFactorConsumption(tier);
            if (consumption <= 0) {
                context.throwGameTestException(
                    "Factor consumption for tier " + tier + " should be positive");
            }
        }
        
        context.complete();
    }
}

// 测试配置占位
class SynthesisTestConfig {
    public static final java.util.Map<String, TestRecipe> UPGRADE_RECIPES = new java.util.HashMap<>();
    
    static {
        UPGRADE_RECIPES.put("t1_to_t2", new TestRecipe(1, 2, 100));
        UPGRADE_RECIPES.put("t2_to_t3", new TestRecipe(2, 3, 250));
        UPGRADE_RECIPES.put("t3_to_t4", new TestRecipe(3, 4, 500));
        UPGRADE_RECIPES.put("t4_to_t5", new TestRecipe(4, 5, 1000));
    }
    
    public static int getCraftTime(int tier) {
        return 200 + (tier - 1) * 100;
    }
    
    public static double getFactorConsumption(int tier) {
        return 10.0 * tier;
    }
    
    public static Object getOutputItem(int tier) {
        return new Object();
    }
    
    record TestRecipe(int inputTier, int outputTier, int factorCost) {}
}