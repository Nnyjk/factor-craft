package com.factorcraft.compat.rei;

import com.factorcraft.compat.rei.category.ConsumerCategory;
import com.factorcraft.compat.rei.category.ExtractorCategory;
import com.factorcraft.compat.rei.category.SynthesizerCategory;
import com.factorcraft.compat.rei.display.ConsumerDisplay;
import com.factorcraft.compat.rei.display.ExtractorDisplay;
import com.factorcraft.compat.rei.display.SynthesizerDisplay;
import com.factorcraft.recipe.RecipeRegistry;
import com.factorcraft.recipe.FactorFusionRecipeData;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.ArrayList;
import java.util.List;

/**
 * REI 客户端插件
 * 注册配方分类和显示
 */
public class FactorCraftREIClientPlugin implements REIClientPlugin {
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ExtractorCategory());
        registry.add(new ConsumerCategory());
        registry.add(new SynthesizerCategory());
    }
    
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // 注册 Factor 融合配方
        for (FactorFusionRecipeData recipe : RecipeRegistry.getAllFactorFusionRecipes()) {
            List inputs = new ArrayList();
            List outputs = new ArrayList();
            
            // 添加输出物品
            if (recipe.getOutput() != null) {
                outputs.add(EntryStacks.of(recipe.getOutput()));
            }
            
            // 根据配方类别添加到对应的 REI 类别
            String category = recipe.getCategory();
            if ("extractor".equals(category)) {
                registry.add(ExtractorDisplay.create(inputs, outputs, 1, recipe.getCraftTime(), (long)recipe.getFactorCost()));
            } else if ("consumer".equals(category)) {
                registry.add(ConsumerDisplay.create(inputs, outputs, recipe.getCraftTime()));
            } else if ("synthesizer".equals(category)) {
                registry.add(SynthesizerDisplay.create(inputs, outputs, recipe.getCraftTime(), (long)recipe.getFactorCost()));
            }
        }
    }
}