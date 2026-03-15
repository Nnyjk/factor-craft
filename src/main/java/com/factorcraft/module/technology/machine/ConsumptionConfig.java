package com.factorcraft.module.technology.machine;

import java.util.Map;

/**
 * 消耗结构配置
 * 
 * 定义 T1-T5 消耗结构的参数和消耗配方
 */
public final class ConsumptionConfig {
    
    private ConsumptionConfig() {}
    
    // ==================== Factor 存储 ====================
    // 单位: Factor
    
    public static final double MAX_STORAGE_T1 = 500.0;
    public static final double MAX_STORAGE_T2 = 1_000.0;
    public static final double MAX_STORAGE_T3 = 2_000.0;
    public static final double MAX_STORAGE_T4 = 5_000.0;
    public static final double MAX_STORAGE_T5 = 10_000.0;
    
    public static double getMaxStorage(int tier) {
        return switch (tier) {
            case 1 -> MAX_STORAGE_T1;
            case 2 -> MAX_STORAGE_T2;
            case 3 -> MAX_STORAGE_T3;
            case 4 -> MAX_STORAGE_T4;
            case 5 -> MAX_STORAGE_T5;
            default -> MAX_STORAGE_T1;
        };
    }
    
    // ==================== 基础产出 ====================
    // 单位: Factor/批次
    
    public static final double BASE_OUTPUT_T1 = 50.0;
    public static final double BASE_OUTPUT_T2 = 150.0;
    public static final double BASE_OUTPUT_T3 = 500.0;
    public static final double BASE_OUTPUT_T4 = 2_000.0;
    public static final double BASE_OUTPUT_T5 = 5_000.0;
    
    public static double getBaseOutput(int tier) {
        return switch (tier) {
            case 1 -> BASE_OUTPUT_T1;
            case 2 -> BASE_OUTPUT_T2;
            case 3 -> BASE_OUTPUT_T3;
            case 4 -> BASE_OUTPUT_T4;
            case 5 -> BASE_OUTPUT_T5;
            default -> BASE_OUTPUT_T1;
        };
    }
    
    // ==================== 消耗时间 ====================
    // 单位: tick
    
    public static final int CONSUME_TIME_T1 = 200;   // 10 秒
    public static final int CONSUME_TIME_T2 = 300;   // 15 秒
    public static final int CONSUME_TIME_T3 = 400;   // 20 秒
    public static final int CONSUME_TIME_T4 = 500;   // 25 秒
    public static final int CONSUME_TIME_T5 = 600;   // 30 秒
    
    public static int getConsumeTime(int tier) {
        return switch (tier) {
            case 1 -> CONSUME_TIME_T1;
            case 2 -> CONSUME_TIME_T2;
            case 3 -> CONSUME_TIME_T3;
            case 4 -> CONSUME_TIME_T4;
            case 5 -> CONSUME_TIME_T5;
            default -> CONSUME_TIME_T1;
        };
    }
    
    // ==================== 维度倍率 ====================
    
    /**
     * 维度倍率范围
     */
    public static final double DIMENSION_MULT_MIN = 1.0;
    public static final double DIMENSION_MULT_MAX = 2.0;
    
    /**
     * 计算维度倍率
     * 高活性维度有更高倍率
     */
    public static double getDimensionMultiplier(String dimension) {
        return switch (dimension) {
            case "minecraft:the_end" -> 2.0;
            case "minecraft:the_nether" -> 1.5;
            default -> 1.0;
        };
    }
    
    // ==================== 推荐维度 ====================
    
    public static final String RECOMMENDED_DIM_T1 = "minecraft:overworld";
    public static final String RECOMMENDED_DIM_T2 = "minecraft:overworld";  // 或下界
    public static final String RECOMMENDED_DIM_T3 = "minecraft:the_nether";
    public static final String RECOMMENDED_DIM_T4 = "minecraft:the_end";
    public static final String RECOMMENDED_DIM_T5 = "minecraft:the_end";
    
    public static String getRecommendedDimension(int tier) {
        return switch (tier) {
            case 1 -> RECOMMENDED_DIM_T1;
            case 2 -> RECOMMENDED_DIM_T2;
            case 3 -> RECOMMENDED_DIM_T3;
            case 4, 5 -> RECOMMENDED_DIM_T4;
            default -> null;
        };
    }
    
    /**
     * 维度惩罚
     */
    public static final double DIMENSION_PENALTY = 0.1;
    
    /**
     * 计算维度效率
     */
    public static double getDimensionEfficiency(String currentDimension, int tier) {
        String recommended = getRecommendedDimension(tier);
        if (recommended == null) return 1.0;
        
        // T2 允许主世界或下界
        if (tier == 2) {
            return (currentDimension.equals("minecraft:overworld") || 
                    currentDimension.equals("minecraft:the_nether")) ? 1.0 : DIMENSION_PENALTY;
        }
        
        return recommended.equals(currentDimension) ? 1.0 : DIMENSION_PENALTY;
    }
    
    // ==================== 消耗配方 ====================
    
    /**
     * 消耗配方定义
     */
    public record ConsumptionRecipe(
        String id,
        String inputItem,
        int inputCount,
        double baseFactorOutput,
        int consumeTime,
        int minTier
    ) {
        /**
         * 获取每个输入物品的 Factor 产出
         */
        public double getFactorPerItem() {
            return baseFactorOutput / inputCount;
        }
    }
    
    /**
     * 消耗配方表
     * 按物品类型分类
     */
    public static final Map<String, ConsumptionRecipe> CONSUMPTION_RECIPES = Map.ofEntries(
        // 普通方块 (T1+)
        Map.entry("stone", new ConsumptionRecipe(
            "stone", "minecraft:stone", 16, 20, 100, 1
        )),
        Map.entry("cobblestone", new ConsumptionRecipe(
            "cobblestone", "minecraft:cobblestone", 16, 15, 100, 1
        )),
        Map.entry("dirt", new ConsumptionRecipe(
            "dirt", "minecraft:dirt", 16, 10, 100, 1
        )),
        Map.entry("wood", new ConsumptionRecipe(
            "wood", "minecraft:oak_log", 8, 30, 150, 1
        )),
        
        // 基础材料 (T2+)
        Map.entry("iron_ingot", new ConsumptionRecipe(
            "iron_ingot", "minecraft:iron_ingot", 4, 50, 200, 2
        )),
        Map.entry("copper_ingot", new ConsumptionRecipe(
            "copper_ingot", "minecraft:copper_ingot", 4, 40, 200, 2
        )),
        Map.entry("gold_ingot", new ConsumptionRecipe(
            "gold_ingot", "minecraft:gold_ingot", 2, 80, 250, 2
        )),
        
        // 稀有材料 (T3+)
        Map.entry("diamond", new ConsumptionRecipe(
            "diamond", "minecraft:diamond", 1, 300, 400, 3
        )),
        Map.entry("netherite_scrap", new ConsumptionRecipe(
            "netherite_scrap", "minecraft:netherite_scrap", 1, 500, 500, 3
        )),
        Map.entry("ancient_debris", new ConsumptionRecipe(
            "ancient_debris", "minecraft:ancient_debris", 1, 800, 600, 3
        )),
        
        // 高阶材料 (T4+)
        Map.entry("netherite_ingot", new ConsumptionRecipe(
            "netherite_ingot", "minecraft:netherite_ingot", 1, 1500, 500, 4
        )),
        Map.entry("elytra", new ConsumptionRecipe(
            "elytra", "minecraft:elytra", 1, 2000, 600, 4
        )),
        
        // 顶级材料 (T5)
        Map.entry("enchanted_golden_apple", new ConsumptionRecipe(
            "enchanted_golden_apple", "minecraft:enchanted_golden_apple", 1, 5000, 600, 5
        )),
        
        // Factor Craft 材料
        Map.entry("dust_copper_ingot", new ConsumptionRecipe(
            "dust_copper_ingot", "factorcraft:dust_copper_ingot", 1, 10, 50, 1
        )),
        Map.entry("shadow_steel_ingot", new ConsumptionRecipe(
            "shadow_steel_ingot", "factorcraft:shadow_steel_ingot", 1, 100, 100, 2
        )),
        Map.entry("stardust_ingot", new ConsumptionRecipe(
            "stardust_ingot", "factorcraft:stardust_ingot", 1, 1000, 150, 3
        )),
        Map.entry("ancient_alloy", new ConsumptionRecipe(
            "ancient_alloy", "factorcraft:ancient_alloy", 1, 10000, 200, 4
        )),
        Map.entry("void_crystal", new ConsumptionRecipe(
            "void_crystal", "factorcraft:void_crystal", 1, 100000, 300, 5
        ))
    );
    
    /**
     * 根据输入物品 ID 获取配方
     */
    public static ConsumptionRecipe getRecipeForInput(String itemId) {
        // 精确匹配
        if (CONSUMPTION_RECIPES.containsKey(itemId)) {
            return CONSUMPTION_RECIPES.get(itemId);
        }
        
        // 模糊匹配（去掉 minecraft: 前缀）
        String shortId = itemId.contains(":") ? itemId.split(":")[1] : itemId;
        for (ConsumptionRecipe recipe : CONSUMPTION_RECIPES.values()) {
            String recipeShortId = recipe.inputItem().contains(":") ? 
                recipe.inputItem().split(":")[1] : recipe.inputItem();
            if (recipeShortId.equals(shortId)) {
                return recipe;
            }
        }
        
        return null;
    }
    
    /**
     * 检查物品是否可被消耗
     */
    public static boolean canConsume(String itemId, int tier) {
        ConsumptionRecipe recipe = getRecipeForInput(itemId);
        return recipe != null && tier >= recipe.minTier();
    }
    
    // ==================== 效率计算 ====================
    
    /**
     * 结构效率
     */
    public static final double EFFICIENCY_T1 = 1.0;
    public static final double EFFICIENCY_T2 = 1.2;
    public static final double EFFICIENCY_T3 = 1.5;
    public static final double EFFICIENCY_T4 = 1.8;
    public static final double EFFICIENCY_T5 = 2.0;
    
    public static double getEfficiency(int tier) {
        return switch (tier) {
            case 1 -> EFFICIENCY_T1;
            case 2 -> EFFICIENCY_T2;
            case 3 -> EFFICIENCY_T3;
            case 4 -> EFFICIENCY_T4;
            case 5 -> EFFICIENCY_T5;
            default -> EFFICIENCY_T1;
        };
    }
    
    /**
     * 计算实际 Factor 产出
     * 
     * @param recipe 消耗配方
     * @param tier 结构等级
     * @param dimension 当前维度
     * @return 实际 Factor 产出
     */
    public static double calculateActualOutput(ConsumptionRecipe recipe, int tier, String dimension) {
        double base = recipe.baseFactorOutput();
        double efficiency = getEfficiency(tier);
        double dimMult = getDimensionMultiplier(dimension);
        double dimEff = getDimensionEfficiency(dimension, tier);
        
        return base * efficiency * dimMult * dimEff;
    }
}