package com.factorcraft.module.gear;

import net.minecraft.item.ItemStack;

/**
 * 装备接口定义
 * 
 * 所有 Factor 装备必须实现此接口
 */
public interface IGear {
    /**
     * 获取装备类型
     */
    GearType getGearType();
    
    /**
     * 获取当前强化等级
     */
    GearUpgradeLevel getUpgradeLevel(ItemStack stack);
    
    /**
     * 设置强化等级
     */
    void setUpgradeLevel(ItemStack stack, GearUpgradeLevel level);
    
    /**
     * 获取装备的 Factor 能量值
     */
    int getFactorEnergy(ItemStack stack);
    
    /**
     * 设置 Factor 能量值
     */
    void setFactorEnergy(ItemStack stack, int energy);
    
    /**
     * 获取最大 Factor 能量值
     */
    int getMaxFactorEnergy(ItemStack stack);
    
    /**
     * 是否已激活特殊能力
     */
    boolean isAbilityUnlocked(ItemStack stack);
    
    /**
     * 解锁特殊能力
     */
    void unlockAbility(ItemStack stack);
}
