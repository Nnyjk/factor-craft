package com.factorcraft.module.gear;

import com.factorcraft.api.IFactorContainer;
import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Factor 工具基类
 * 
 * 所有 Factor 工具的共同特性：
 * - 消耗 Factor 提升效率
 * - 可从机器/电池充能
 * - T1-T5 等级系统
 * 
 * 注意：在 Minecraft 1.21.4 中，ToolItem 类已被移除。
 * 具体工具类型应继承对应的工具类（如 PickaxeItem、AxeItem）。
 */
public abstract class FactorToolItem extends Item implements IFactorContainer {
    
    protected final ToolMaterial material;
    protected final int tier;
    protected final double maxFactorStorage;
    protected final double factorCostPerUse;
    protected final float efficiencyBoost;
    
    /**
     * 创建 Factor 工具
     * 
     * @param material 工具材料
     * @param tier Factor 等级 (1-5)
     * @param settings 物品设置
     */
    protected FactorToolItem(ToolMaterial material, int tier, Settings settings) {
        super(settings);
        this.material = material;
        this.tier = tier;
        this.maxFactorStorage = 100.0 * tier; // T1: 100, T5: 500
        this.factorCostPerUse = 5.0 * tier;   // T1: 5, T5: 25
        this.efficiencyBoost = 2.0f * tier;   // T1: +200%, T5: +1000%
    }
    
    // ========== IFactorContainer 实现 ==========
    
    /**
     * 获取物品的 Factor 存储量
     */
    public double getFactorStorage(ItemStack stack) {
        FactorStorage storage = stack.get(FactorCraftDataComponents.FACTOR_STORAGE);
        return storage != null ? storage.amount() : 0.0;
    }
    
    /**
     * 设置物品的 Factor 存储量
     */
    public void setFactorStorage(ItemStack stack, double amount) {
        stack.set(FactorCraftDataComponents.FACTOR_STORAGE, FactorStorage.of(amount));
    }
    
    @Override
    public double getMaxFactorStorage() {
        return maxFactorStorage;
    }
    
    @Override
    public double addFactor(double amount) {
        return amount; // ItemStack 版本使用其他方法
    }
    
    @Override
    public double extractFactor(double amount) {
        return amount; // ItemStack 版本使用其他方法
    }
    
    @Override
    public double getFactorStorage() {
        return 0; // ItemStack 版本使用其他方法
    }
    
    /**
     * 向物品添加 Factor
     */
    public double addFactor(ItemStack stack, double amount) {
        double current = getFactorStorage(stack);
        double space = maxFactorStorage - current;
        double toAdd = Math.min(amount, space);
        setFactorStorage(stack, current + toAdd);
        return toAdd;
    }
    
    /**
     * 从物品抽取 Factor
     */
    public double extractFactor(ItemStack stack, double amount) {
        double current = getFactorStorage(stack);
        double toExtract = Math.min(amount, current);
        setFactorStorage(stack, current - toExtract);
        return toExtract;
    }
    
    /**
     * 消耗 Factor
     */
    public boolean consumeFactor(ItemStack stack, double amount) {
        double current = getFactorStorage(stack);
        if (current >= amount) {
            setFactorStorage(stack, current - amount);
            return true;
        }
        return false;
    }
    
    /**
     * 检查是否可以使用 Factor 加速
     */
    public boolean canUseFactorBoost(ItemStack stack, PlayerEntity player) {
        return getFactorStorage(stack) >= factorCostPerUse;
    }
    
    // ========== Getter 方法 ==========
    
    public ToolMaterial getMaterial() {
        return material;
    }
    
    public int getTier() {
        return tier;
    }
    
    public float getEfficiencyBoost() {
        return efficiencyBoost;
    }
    
    public double getFactorCostPerUse() {
        return factorCostPerUse;
    }
}