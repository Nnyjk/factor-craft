package com.factorcraft.module.technology.machine;

import java.util.Map;

/**
 * 培育结构配置
 * 
 * 定义 T1-T5 培育结构的参数和培育配方
 * 培育: 消耗 Factor 产出物品
 */
public final class BreedingConfig {
    
    private BreedingConfig() {}
    
    // ==================== Factor 消耗 ====================
    // 单位: Factor
    
    public static final double MAX_BUFFER_T1 = 1_000.0;
    public static final double MAX_BUFFER_T2 = 5_000.0;
    public static final double MAX_BUFFER_T3 = 25_000.0;
    public static final double MAX_BUFFER_T4 = 100_000.0;
    public static final double MAX_BUFFER_T5 = 500_000.0;
    
    public static double getMaxBuffer(int tier) {
        return switch (tier) {
            case 1 -> MAX_BUFFER_T1;
            case 2 -> MAX_BUFFER_T2;
            case 3 -> MAX_BUFFER_T3;
            case 4 -> MAX_BUFFER_T4;
            case 5 -> MAX_BUFFER_T5;
            default -> MAX_BUFFER_T1;
        };
    }
    
    // ==================== 培育时间 ====================
    // 单位: tick
    
    public static final int BREED_TIME_T1 = 600;    // 30 秒
    public static final int BREED_TIME_T2 = 1200;   // 60 秒
    public static final int BREED_TIME_T3 = 2400;   // 120 秒
    public static final int BREED_TIME_T4 = 4800;   // 240 秒
    public static final int BREED_TIME_T5 = 9600;   // 480 秒
    
    public static int getBreedTime(int tier) {
        return switch (tier) {
            case 1 -> BREED_TIME_T1;
            case 2 -> BREED_TIME_T2;
            case 3 -> BREED_TIME_T3;
            case 4 -> BREED_TIME_T4;
            case 5 -> BREED_TIME_T5;
            default -> BREED_TIME_T1;
        };
    }
    
    // ==================== 效率 ====================
    
    /**
     * 结构效率（影响 Factor 消耗）
     */
    public static final double EFFICIENCY_T1 = 1.0;
    public static final double EFFICIENCY_T2 = 0.9;   // 节省 10% Factor
    public static final double EFFICIENCY_T3 = 0.75;  // 节省 25% Factor
    public static final double EFFICIENCY_T4 = 0.6;   // 节省 40% Factor
    public static final double EFFICIENCY_T5 = 0.5;   // 节省 50% Factor
    
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
    
    // ==================== 维度效率 ====================
    
    /**
     * 推荐维度
     */
    public static final String RECOMMENDED_DIM_T1 = "minecraft:overworld";
    public static final String RECOMMENDED_DIM_T2 = "minecraft:overworld";
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
    
    // ==================== 培育配方 ====================
    
    /**
     * 培育配方定义
     */
    public record BreedingRecipe(
        String id,
        String outputItem,
        int outputCount,
        double factorCost,
        int breedTime,
        int minTier,
        String requiredDimension  // null = 任意维度
    ) {
        /**
         * 获取每个物品的 Factor 成本
         */
        public double getFactorPerItem() {
            return factorCost / outputCount;
        }
    }
    
    /**
     * 培育配方表
     */
    public static final Map<String, BreedingRecipe> BREEDING_RECIPES = Map.ofEntries(
        // T1 基础方块
        Map.entry("cobblestone", new BreedingRecipe(
            "cobblestone", "minecraft:cobblestone", 32, 50, 400, 1, null
        )),
        Map.entry("stone", new BreedingRecipe(
            "stone", "minecraft:stone", 32, 60, 400, 1, null
        )),
        Map.entry("dirt", new BreedingRecipe(
            "dirt", "minecraft:dirt", 64, 30, 300, 1, null
        )),
        Map.entry("oak_log", new BreedingRecipe(
            "oak_log", "minecraft:oak_log", 16, 80, 500, 1, null
        )),
        
        // T2 有用材料
        Map.entry("iron_ingot", new BreedingRecipe(
            "iron_ingot", "minecraft:iron_ingot", 4, 200, 600, 2, null
        )),
        Map.entry("copper_ingot", new BreedingRecipe(
            "copper_ingot", "minecraft:copper_ingot", 8, 150, 600, 2, null
        )),
        Map.entry("gold_ingot", new BreedingRecipe(
            "gold_ingot", "minecraft:gold_ingot", 2, 300, 800, 2, null
        )),
        
        // T3 稀有材料
        Map.entry("diamond", new BreedingRecipe(
            "diamond", "minecraft:diamond", 1, 1000, 1200, 3, null
        )),
        Map.entry("obsidian", new BreedingRecipe(
            "obsidian", "minecraft:obsidian", 8, 400, 800, 3, null
        )),
        Map.entry("blaze_rod", new BreedingRecipe(
            "blaze_rod", "minecraft:blaze_rod", 4, 500, 1000, 3, "minecraft:the_nether"
        )),
        
        // T4 高阶材料
        Map.entry("netherite_scrap", new BreedingRecipe(
            "netherite_scrap", "minecraft:netherite_scrap", 1, 2000, 2000, 4, null
        )),
        Map.entry("elytra", new BreedingRecipe(
            "elytra", "minecraft:elytra", 1, 5000, 3000, 4, "minecraft:the_end"
        )),
        Map.entry("end_crystal", new BreedingRecipe(
            "end_crystal", "minecraft:end_crystal", 1, 1500, 1500, 4, "minecraft:the_end"
        )),
        
        // T5 顶级材料
        Map.entry("netherite_ingot", new BreedingRecipe(
            "netherite_ingot", "minecraft:netherite_ingot", 1, 4000, 2400, 5, null
        )),
        Map.entry("enchanted_golden_apple", new BreedingRecipe(
            "enchanted_golden_apple", "minecraft:enchanted_golden_apple", 1, 10000, 4000, 5, null
        )),
        Map.entry("beacon", new BreedingRecipe(
            "beacon", "minecraft:beacon", 1, 8000, 3600, 5, null
        )),
        
        // Factor Craft 逆向
        Map.entry("dust_copper_reverse", new BreedingRecipe(
            "dust_copper_reverse", "factorcraft:dust_copper_ingot", 1, 30, 200, 1, null
        )),
        Map.entry("shadow_steel_reverse", new BreedingRecipe(
            "shadow_steel_reverse", "factorcraft:shadow_steel_ingot", 1, 300, 400, 2, null
        )),
        Map.entry("stardust_reverse", new BreedingRecipe(
            "stardust_reverse", "factorcraft:stardust_ingot", 1, 3000, 600, 3, null
        )),
        Map.entry("ancient_alloy_reverse", new BreedingRecipe(
            "ancient_alloy_reverse", "factorcraft:ancient_alloy", 1, 30000, 1000, 4, null
        ))
    );
    
    /**
     * 根据 ID 获取配方
     */
    public static BreedingRecipe getRecipe(String recipeId) {
        return BREEDING_RECIPES.get(recipeId);
    }
    
    /**
     * 根据输出物品获取配方（返回第一个匹配的）
     */
    public static BreedingRecipe getRecipeByOutput(String outputItemId) {
        for (BreedingRecipe recipe : BREEDING_RECIPES.values()) {
            if (recipe.outputItem().equals(outputItemId)) {
                return recipe;
            }
        }
        return null;
    }
    
    /**
     * 检查配方是否可用
     */
    public static boolean canBreed(String recipeId, int tier, String dimension) {
        BreedingRecipe recipe = getRecipe(recipeId);
        if (recipe == null) return false;
        
        // 检查 Tier
        if (tier < recipe.minTier()) return false;
        
        // 检查维度要求
        if (recipe.requiredDimension() != null) {
            return recipe.requiredDimension().equals(dimension);
        }
        
        return true;
    }
    
    // ==================== 实际成本计算 ====================
    
    /**
     * 计算实际 Factor 成本
     * 
     * @param recipe 培育配方
     * @param tier 结构等级
     * @param dimension 当前维度
     * @return 实际 Factor 成本
     */
    public static double calculateActualCost(BreedingRecipe recipe, int tier, String dimension) {
        double base = recipe.factorCost();
        double efficiency = getEfficiency(tier);
        double dimEff = getDimensionEfficiency(dimension, tier);
        
        // 成本 = 基础成本 × 效率 × 维度效率
        // 效率越高成本越低
        return base * efficiency * dimEff;
    }
    
    /**
     * 计算实际培育时间
     */
    public static int calculateActualTime(BreedingRecipe recipe, int tier) {
        // 使用配方时间或结构默认时间
        int baseTime = recipe.breedTime() > 0 ? recipe.breedTime() : getBreedTime(tier);
        // 高等级加速
        double speedMultiplier = switch (tier) {
            case 1 -> 1.0;
            case 2 -> 0.9;
            case 3 -> 0.8;
            case 4 -> 0.7;
            case 5 -> 0.6;
            default -> 1.0;
        };
        return (int) (baseTime * speedMultiplier);
    }
    
    /**
     * 根据 Tier 获取可用配方（返回第一个匹配的）
     */
    public static BreedingRecipe getRecipeForTier(int tier) {
        for (BreedingRecipe recipe : BREEDING_RECIPES.values()) {
            if (recipe.minTier() == tier) {
                return recipe;
            }
        }
        return null;
    }
}