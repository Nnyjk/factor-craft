package com.factorcraft.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配方注册表 - 存储和管理所有配方数据
 */
public class RecipeRegistry {
    
    private static final Map<String, FactorFusionRecipeData> FACTOR_FUSION_RECIPES = new HashMap<>();
    private static final Map<String, TraitInfusionRecipeData> TRAIT_INFUSION_RECIPES = new HashMap<>();
    
    /**
     * 注册 Factor 融合配方
     */
    public static void registerFactorFusion(FactorFusionRecipeData recipe) {
        FACTOR_FUSION_RECIPES.put(recipe.getId(), recipe);
    }
    
    /**
     * 注册特性注入配方
     */
    public static void registerTraitInfusion(TraitInfusionRecipeData recipe) {
        TRAIT_INFUSION_RECIPES.put(recipe.getId(), recipe);
    }
    
    /**
     * 获取 Factor 融合配方
     */
    public static FactorFusionRecipeData getFactorFusionRecipe(String recipeId) {
        return FACTOR_FUSION_RECIPES.get(recipeId);
    }
    
    /**
     * 获取特性注入配方
     */
    public static TraitInfusionRecipeData getTraitInfusionRecipe(String recipeId) {
        return TRAIT_INFUSION_RECIPES.get(recipeId);
    }
    
    /**
     * 通过输入物品查找 Factor 融合配方
     */
    public static FactorFusionRecipeData findFactorFusionRecipe(String itemId, int count) {
        for (FactorFusionRecipeData recipe : FACTOR_FUSION_RECIPES.values()) {
            if (recipe.matchesInput(itemId, count)) {
                return recipe;
            }
        }
        return null;
    }
    
    /**
     * 通过输入物品查找特性注入配方
     */
    public static TraitInfusionRecipeData findTraitInfusionRecipe(String itemId, int count, String traitItemId) {
        for (TraitInfusionRecipeData recipe : TRAIT_INFUSION_RECIPES.values()) {
            if (recipe.matchesInput(itemId, count, traitItemId)) {
                return recipe;
            }
        }
        return null;
    }
    
    /**
     * 获取所有 Factor 融合配方
     */
    public static List<FactorFusionRecipeData> getAllFactorFusionRecipes() {
        return new ArrayList<>(FACTOR_FUSION_RECIPES.values());
    }
    
    /**
     * 获取所有特性注入配方
     */
    public static List<TraitInfusionRecipeData> getAllTraitInfusionRecipes() {
        return new ArrayList<>(TRAIT_INFUSION_RECIPES.values());
    }
    
    /**
     * 清除所有配方（用于 datapack 重载）
     */
    public static void clearAll() {
        FACTOR_FUSION_RECIPES.clear();
        TRAIT_INFUSION_RECIPES.clear();
    }
}
