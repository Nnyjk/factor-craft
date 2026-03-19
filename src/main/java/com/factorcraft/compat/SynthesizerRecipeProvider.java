package com.factorcraft.compat;

import com.factorcraft.FactorCraftMod;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 合成器配方提供者
 */
public class SynthesizerRecipeProvider implements RecipeProvider {
    
    private static final Identifier TYPE_ID = Identifier.of(FactorCraftMod.MOD_ID, "synthesizer");
    private static final String DISPLAY_NAME_KEY = "factorcraft.rei.category.synthesizer";
    
    private final List<RecipeInfo> recipes = new ArrayList<>();
    
    public SynthesizerRecipeProvider() {
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