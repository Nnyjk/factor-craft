package com.factorcraft.compat.rei.category;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.compat.rei.FactorCraftREIPlugin;
import com.factorcraft.compat.rei.display.SynthesizerDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * REI 配方分类：Factor 合成器
 */
public class SynthesizerCategory implements DisplayCategory<SynthesizerDisplay> {
    
    @Override
    public CategoryIdentifier<SynthesizerDisplay> getCategoryIdentifier() {
        return FactorCraftREIPlugin.SYNTHESIZER;
    }
    
    @Override
    public Text getTitle() {
        return Text.translatable("factorcraft.rei.category.synthesizer");
    }
    
    @Override
    public Renderer getIcon() {
        return EntryStacks.of(new ItemStack(net.minecraft.item.Items.SMITHING_TABLE));
    }
}