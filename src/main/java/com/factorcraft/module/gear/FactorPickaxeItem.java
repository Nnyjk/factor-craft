package com.factorcraft.module.gear;

import com.factorcraft.api.IFactorContainer;
import com.factorcraft.component.FactorCraftDataComponents;
import com.factorcraft.component.type.FactorStorage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 镐
 * 
 * 消耗 Factor 加速挖掘石头和矿石
 */
public class FactorPickaxeItem extends PickaxeItem implements IFactorContainer {
    
    private final int tier;
    private final double maxFactorStorage;
    private final double factorCostPerUse;
    private final float efficiencyBoost;
    
    /**
     * 创建 Factor 镐
     * 
     * @param material 工具材料
     * @param tier Factor 等级 (1-5)
     * @param settings 物品设置
     */
    public FactorPickaxeItem(ToolMaterial material, int tier, Settings settings) {
        super(material, 1.0f, -2.8f, settings); // 标准镐参数
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
    
    // ========== 工具行为 ==========
    
    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        float baseSpeed = super.getMiningSpeed(stack, state);
        
        // 如果有 Factor 且工具适用于该方块
        if (baseSpeed > 1.0f && getFactorStorage(stack) >= factorCostPerUse) {
            return baseSpeed + efficiencyBoost;
        }
        
        return baseSpeed;
    }
    
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, net.minecraft.entity.LivingEntity miner) {
        if (!world.isClient && miner instanceof ServerPlayerEntity serverPlayer) {
            // 检查是否应该消耗 Factor
            float baseSpeed = super.getMiningSpeed(stack, state);
            if (baseSpeed > 1.0f && getFactorStorage(stack) >= factorCostPerUse) {
                // 消耗 Factor
                consumeFactor(stack, factorCostPerUse);
                stack.set(FactorCraftDataComponents.FACTOR_BOOSTED, true);
            } else {
                stack.set(FactorCraftDataComponents.FACTOR_BOOSTED, false);
            }
        }
        return super.postMine(stack, world, state, pos, miner);
    }
    
    @Override
    public void onCraft(ItemStack stack, World world) {
        super.onCraft(stack, world);
        // 初始化 Factor 存储
        setFactorStorage(stack, 0.0);
    }
    
    // ========== Getter 方法 ==========
    
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