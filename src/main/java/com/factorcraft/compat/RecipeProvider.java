package com.factorcraft.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * 配方提供者接口。
 * 供 REI/JEI 等配方查看器集成使用。
 */
public interface RecipeProvider {
    
    /**
     * 获取配方类型标识符
     */
    Identifier getRecipeTypeId();
    
    /**
     * 获取配方显示名称（翻译键）
     */
    String getDisplayNameKey();
    
    /**
     * 获取所有配方
     */
    List<RecipeInfo> getRecipes();
    
    /**
     * 配方信息
     */
    record RecipeInfo(
        String id,
        int tier,
        List<ItemStack> inputs,
        List<ItemStack> outputs,
        int processingTime,
        long factorCost,
        long factorOutput
    ) {}
}