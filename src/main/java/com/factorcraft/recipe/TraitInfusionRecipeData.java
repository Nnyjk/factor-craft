package com.factorcraft.recipe;

import net.minecraft.item.ItemStack;

/**
 * 特性注入配方数据
 * 
 * 用于定义培育器的特性注入配方
 */
public class TraitInfusionRecipeData {
    
    private final String id;
    private final String group;
    private final String inputItem;
    private final String traitItem;
    private final int inputCount;
    private final ItemStack output;
    private final double factorCost;
    private final int craftTime;
    private final double successRate;
    private final String category;
    
    public TraitInfusionRecipeData(
        String id,
        String group,
        String inputItem,
        String traitItem,
        int inputCount,
        ItemStack output,
        double factorCost,
        int craftTime,
        double successRate,
        String category
    ) {
        this.id = id;
        this.group = group;
        this.inputItem = inputItem;
        this.traitItem = traitItem;
        this.inputCount = inputCount;
        this.output = output;
        this.factorCost = factorCost;
        this.craftTime = craftTime;
        this.successRate = successRate;
        this.category = category;
    }
    
    public String getId() {
        return id;
    }
    
    public String getGroup() {
        return group;
    }
    
    public String getInputItem() {
        return inputItem;
    }
    
    public String getTraitItem() {
        return traitItem;
    }
    
    public int getInputCount() {
        return inputCount;
    }
    
    public ItemStack getOutput() {
        return output;
    }
    
    public double getFactorCost() {
        return factorCost;
    }
    
    public int getCraftTime() {
        return craftTime;
    }
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public String getCategory() {
        return category;
    }
    
    /**
     * 检查物品是否匹配输入
     */
    public boolean matchesInput(String itemId, int count, String traitItemId) {
        return this.inputItem.equals(itemId) && 
               count >= this.inputCount &&
               this.traitItem.equals(traitItemId);
    }
}
