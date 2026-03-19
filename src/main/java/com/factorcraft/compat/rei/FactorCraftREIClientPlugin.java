package com.factorcraft.compat.rei;

import com.factorcraft.compat.rei.category.ConsumerCategory;
import com.factorcraft.compat.rei.category.ExtractorCategory;
import com.factorcraft.compat.rei.category.SynthesizerCategory;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;

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
}