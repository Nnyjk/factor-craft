package com.factorcraft.compat.rei.display;

import com.factorcraft.compat.rei.FactorCraftREIPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

/**
 * 合成器配方显示
 */
public class SynthesizerDisplay implements Display {
    
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final int tier;
    private final int processingTime;
    private final long factorCost;
    
    public SynthesizerDisplay(
        List<EntryIngredient> inputs,
        List<EntryIngredient> outputs,
        int tier,
        int processingTime,
        long factorCost
    ) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.tier = tier;
        this.processingTime = processingTime;
        this.factorCost = factorCost;
    }
    
    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }
    
    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }
    
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return FactorCraftREIPlugin.SYNTHESIZER;
    }
    
    public int getTier() {
        return tier;
    }
    
    public int getProcessingTime() {
        return processingTime;
    }
    
    public long getFactorCost() {
        return factorCost;
    }
    
    /**
     * 创建合成器配方显示
     */
    public static SynthesizerDisplay create(List inputs, List outputs, int processingTime, long factorCost) {
        return new SynthesizerDisplay(inputs, outputs, 1, processingTime, factorCost);
    }
}