package com.factorcraft.registry;

import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    
    public static final RecipeType<ShapelessRecipe> FACTOR_FUSION = 
        RecipeType.register("factorcraft:factor_fusion");
    
    public static final RecipeType<ShapedRecipe> TRAIT_INFUSION = 
        RecipeType.register("factorcraft:trait_infusion");
    
    public static void register() {
        // 注册配方类型
        Registry.register(Registries.RECIPE_TYPE, 
            Identifier.of("factorcraft", "factor_fusion"), 
            FACTOR_FUSION);
        
        Registry.register(Registries.RECIPE_TYPE,
            Identifier.of("factorcraft", "trait_infusion"),
            TRAIT_INFUSION);
    }
}