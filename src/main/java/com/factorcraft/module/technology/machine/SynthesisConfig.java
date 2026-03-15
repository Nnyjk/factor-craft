package com.factorcraft.module.technology.machine;

import java.util.Map;

/**
 * 合成结构配置
 * 
 * 定义 T1-T5 合成结构的参数和升级配方
 */
public final class SynthesisConfig {
    
    private SynthesisConfig() {}
    
    // ==================== 合成时间 ====================
    // 单位: tick
    
    public static final int CRAFT_TIME_T1 = 1200;   // 60 秒
    public static final int CRAFT_TIME_T2 = 2400;   // 120 秒
    public static final int CRAFT_TIME_T3 = 3600;   // 180 秒
    public static final int CRAFT_TIME_T4 = 6000;   // 300 秒
    public static final int CRAFT_TIME_T5 = 12000;  // 600 秒
    
    public static int getCraftTime(int tier) {
        return switch (tier) {
            case 1 -> CRAFT_TIME_T1;
            case 2 -> CRAFT_TIME_T2;
            case 3 -> CRAFT_TIME_T3;
            case 4 -> CRAFT_TIME_T4;
            case 5 -> CRAFT_TIME_T5;
            default -> CRAFT_TIME_T1;
        };
    }
    
    // ==================== Factor 缓冲区 ====================
    // 单位: Factor
    
    public static final double MAX_BUFFER_T1 = 2_000.0;
    public static final double MAX_BUFFER_T2 = 10_000.0;
    public static final double MAX_BUFFER_T3 = 50_000.0;
    public static final double MAX_BUFFER_T4 = 250_000.0;
    public static final double MAX_BUFFER_T5 = 1_000_000.0;
    
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
    
    // ==================== 转换率 ====================
    // 输入:输出 比例
    
    public static final int CONVERSION_RATE_T1 = 2;   // 2:1
    public static final int CONVERSION_RATE_T2 = 4;   // 4:1
    public static final int CONVERSION_RATE_T3 = 6;   // 6:1
    public static final int CONVERSION_RATE_T4 = 10;  // 10:1
    public static final int CONVERSION_RATE_T5 = 15;  // 15:1
    
    public static int getConversionRate(int tier) {
        return switch (tier) {
            case 1 -> CONVERSION_RATE_T1;
            case 2 -> CONVERSION_RATE_T2;
            case 3 -> CONVERSION_RATE_T3;
            case 4 -> CONVERSION_RATE_T4;
            case 5 -> CONVERSION_RATE_T5;
            default -> CONVERSION_RATE_T1;
        };
    }
    
    // ==================== 推荐维度 ====================
    
    public static final String RECOMMENDED_DIM_T1 = "minecraft:overworld";
    public static final String RECOMMENDED_DIM_T2 = "minecraft:the_nether";
    public static final String RECOMMENDED_DIM_T3 = "minecraft:the_nether";  // 或末地
    public static final String RECOMMENDED_DIM_T4 = "minecraft:the_end";
    public static final String RECOMMENDED_DIM_T5 = "minecraft:the_end";
    
    public static String getRecommendedDimension(int tier) {
        return switch (tier) {
            case 1 -> RECOMMENDED_DIM_T1;
            case 2, 3 -> RECOMMENDED_DIM_T2;
            case 4, 5 -> RECOMMENDED_DIM_T4;
            default -> null;
        };
    }
    
    /**
     * 维度惩罚倍率
     */
    public static final double DIMENSION_PENALTY = 0.1;
    
    /**
     * 计算维度效率
     */
    public static double getDimensionEfficiency(String currentDimension, int tier) {
        String recommended = getRecommendedDimension(tier);
        if (recommended == null) return 1.0;
        
        // T3 允许下界或末地
        if (tier == 3) {
            return (currentDimension.equals("minecraft:the_nether") || 
                    currentDimension.equals("minecraft:the_end")) ? 1.0 : DIMENSION_PENALTY;
        }
        
        return recommended.equals(currentDimension) ? 1.0 : DIMENSION_PENALTY;
    }
    
    // ==================== 材料升级配方 ====================
    
    /**
     * 升级配方定义
     */
    public record UpgradeRecipe(
        String id,
        int fromTier,
        int toTier,
        String inputItem,
        int inputCount,
        String outputItem,
        int outputCount,
        double factorCost,
        int craftTime
    ) {
        /**
         * 获取每个输入的 Factor 成本
         */
        public double getFactorPerInput() {
            return factorCost / inputCount;
        }
    }
    
    /**
     * 所有升级配方
     */
    public static final Map<String, UpgradeRecipe> UPGRADE_RECIPES = Map.of(
        "t1_to_t2", new UpgradeRecipe(
            "t1_to_t2",
            1, 2,
            "factorcraft:dust_copper_ingot", 64,
            "factorcraft:shadow_steel_ingot", 32,
            1_000,
            CRAFT_TIME_T1
        ),
        "t2_to_t3", new UpgradeRecipe(
            "t2_to_t3",
            2, 3,
            "factorcraft:shadow_steel_ingot", 128,
            "factorcraft:stardust_ingot", 32,
            5_000,
            CRAFT_TIME_T2
        ),
        "t3_to_t4", new UpgradeRecipe(
            "t3_to_t4",
            3, 4,
            "factorcraft:stardust_ingot", 256,
            "factorcraft:ancient_alloy", 42,
            25_000,
            CRAFT_TIME_T3
        ),
        "t4_to_t5", new UpgradeRecipe(
            "t4_to_t5",
            4, 5,
            "factorcraft:ancient_alloy", 512,
            "factorcraft:void_crystal", 51,
            125_000,
            CRAFT_TIME_T4
        )
    );
    
    /**
     * 根据 Tier 获取可用配方
     */
    public static UpgradeRecipe getRecipeForTier(int tier) {
        return switch (tier) {
            case 1 -> UPGRADE_RECIPES.get("t1_to_t2");
            case 2 -> UPGRADE_RECIPES.get("t2_to_t3");
            case 3 -> UPGRADE_RECIPES.get("t3_to_t4");
            case 4 -> UPGRADE_RECIPES.get("t4_to_t5");
            default -> null;
        };
    }
    
    /**
     * 根据输入物品获取配方
     */
    public static UpgradeRecipe getRecipeForInput(String itemId) {
        for (UpgradeRecipe recipe : UPGRADE_RECIPES.values()) {
            if (recipe.inputItem().equals(itemId)) {
                return recipe;
            }
        }
        return null;
    }
    
    // ==================== 效率计算 ====================
    
    /**
     * 结构效率
     */
    public static final double EFFICIENCY_T1 = 1.0;
    public static final double EFFICIENCY_T2 = 1.1;
    public static final double EFFICIENCY_T3 = 1.2;
    public static final double EFFICIENCY_T4 = 1.3;
    public static final double EFFICIENCY_T5 = 1.5;
    
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
     * 计算实际合成时间（考虑效率和维度）
     */
    public static int getActualCraftTime(int tier, String dimension) {
        double efficiency = getEfficiency(tier);
        double dimEfficiency = getDimensionEfficiency(dimension, tier);
        
        // 低效率 = 更长时间
        double multiplier = 1.0 / (efficiency * dimEfficiency);
        return (int) (getCraftTime(tier) * multiplier);
    }
}