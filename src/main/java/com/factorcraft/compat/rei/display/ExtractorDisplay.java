package com.factorcraft.compat.rei.display;

import com.factorcraft.compat.rei.FactorCraftREIPlugin;
import com.factorcraft.compat.rei.category.ExtractorCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

/**
 * 提取器配方显示
 */
public class ExtractorDisplay implements Display {
    
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final int tier;
    private final int processingTime;
    private final long factorOutput;
    
    public ExtractorDisplay(
        List<EntryIngredient> inputs,
        List<EntryIngredient> outputs,
        int tier,
        int processingTime,
        long factorOutput
    ) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.tier = tier;
        this.processingTime = processingTime;
        this.factorOutput = factorOutput;
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
        return FactorCraftREIPlugin.EXTRACTOR;
    }
    
    public int getTier() {
        return tier;
    }
    
    public int getProcessingTime() {
        return processingTime;
    }
    
    public long getFactorOutput() {
        return factorOutput;
    }
    
    /**
     * 创建提取器配方显示
     */
    public static ExtractorDisplay create(List inputs, List outputs, int tier, int processingTime, long factorOutput) {
        return new ExtractorDisplay(inputs, outputs, tier, processingTime, factorOutput);
    }
}