package com.factorcraft.registry;

import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 配方类型注册 - Fabric 1.21.4
 * 
 * 仅注册 RecipeType 用于 JEI/REI 显示
 * 实际配方加载由 RecipeLoader 通过 JSON 完成
 */
public class ModRecipes {
    
    public static final RecipeType<?> FACTOR_FUSION = register("factor_fusion");
    public static final RecipeType<?> TRAIT_INFUSION = register("trait_infusion");
    
    /**
     * 注册配方类型
     * 注意：不能直接使用 RecipeType.register()，因为它会添加 minecraft: 前缀
     */
    private static RecipeType<?> register(String name) {
        Identifier id = Identifier.of("factorcraft", name);
        RecipeType<?> type = new RecipeType<>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
        return Registry.register(Registries.RECIPE_TYPE, id, type);
    }
    
    public static void register() {
        // 静态初始化时已完成注册
    }
}