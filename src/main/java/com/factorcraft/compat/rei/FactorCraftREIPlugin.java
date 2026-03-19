package com.factorcraft.compat.rei;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.compat.rei.display.ExtractorDisplay;
import com.factorcraft.compat.rei.display.ConsumerDisplay;
import com.factorcraft.compat.rei.display.SynthesizerDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;

/**
 * REI 插件入口点
 * 注册 Factor Craft 的配方分类
 */
public class FactorCraftREIPlugin implements REIServerPlugin {
    
    public static final CategoryIdentifier<ExtractorDisplay> EXTRACTOR = 
        CategoryIdentifier.of(FactorCraftMod.MOD_ID, "extractor");
    
    public static final CategoryIdentifier<ConsumerDisplay> CONSUMER = 
        CategoryIdentifier.of(FactorCraftMod.MOD_ID, "consumer");
    
    public static final CategoryIdentifier<SynthesizerDisplay> SYNTHESIZER = 
        CategoryIdentifier.of(FactorCraftMod.MOD_ID, "synthesizer");
}