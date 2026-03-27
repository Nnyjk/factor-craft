package com.factorcraft.module.gear;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 装备能力接口
 * 
 * 定义装备特殊能力的通用接口
 */
public interface GearAbility {
    /**
     * 获取能力名称
     */
    String getName();
    
    /**
     * 获取能力描述
     */
    String getDescription();
    
    /**
     * 能力是否已激活
     */
    boolean isActive(ItemStack stack);
    
    /**
     * 激活能力
     */
    void activate(ItemStack stack, PlayerEntity player);
    
    /**
     * 停用能力
     */
    void deactivate(ItemStack stack, PlayerEntity player);
    
    /**
     * 每 tick 调用（用于持续效果）
     */
    default void onTick(ItemStack stack, World world, PlayerEntity player) {
    }
    
    /**
     * 挖掘/攻击时调用
     */
    default void onUse(ItemStack stack, PlayerEntity player) {
    }
}
