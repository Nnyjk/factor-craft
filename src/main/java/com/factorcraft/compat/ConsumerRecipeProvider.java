package com.factorcraft.compat;

import com.factorcraft.FactorCraftMod;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费者配方提供者
 */
public class ConsumerRecipeProvider implements RecipeProvider {
    
    private static final Identifier TYPE_ID = Identifier.of(FactorCraftMod.MOD_ID, "consumer");
    private static final String DISPLAY_NAME_KEY = "factorcraft.rei.category.consumer";
    
    private final List<RecipeInfo> recipes = new ArrayList<>();
    
    public ConsumerRecipeProvider() {
        loadRecipes();
    }
    
    private void loadRecipes() {
        // 从配置加载配方
        // TODO: 实现 JSON 配置加载
    }
    
    @Override
    public Identifier getRecipeTypeId() {
        return TYPE_ID;
    }
    
    @Override
    public String getDisplayNameKey() {
        return DISPLAY_NAME_KEY;
    }
    
    @Override
    public List<RecipeInfo> getRecipes() {
        return recipes;
    }
}