package com.factorcraft.recipe;

import net.minecraft.item.ItemStack;

/**
 * Factor 融合配方数据
 * 
 * 用于定义使用 Factor 进行物品合成的配方
 */
public class FactorFusionRecipeData {
    
    private final String id;
    private final String group;
    private final String inputItem;
    private final int inputCount;
    private final ItemStack output;
    private final double factorCost;
    private final int craftTime;
    private final String category;
    
    public FactorFusionRecipeData(
        String id,
        String group,
        String inputItem,
        int inputCount,
        ItemStack output,
        double factorCost,
        int craftTime,
        String category
    ) {
        this.id = id;
        this.group = group;
        this.inputItem = inputItem;
        this.inputCount = inputCount;
        this.output = output;
        this.factorCost = factorCost;
        this.craftTime = craftTime;
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
    
    public String getCategory() {
        return category;
    }
    
    /**
     * 检查物品是否匹配输入
     */
    public boolean matchesInput(String itemId, int count) {
        return this.inputItem.equals(itemId) && count >= this.inputCount;
    }
}
