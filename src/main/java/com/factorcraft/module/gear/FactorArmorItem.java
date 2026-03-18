package com.factorcraft.module.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Factor 护甲基类
 * 
 * 所有 Factor 护甲的共同特性：
 * - Factor 存储容量（胸甲）
 * - Factor 浓度显示（头盔）
 * - Factor 扩散抗性（护腿）
 * - 区域移动加速（靴子）
 */
public class FactorArmorItem extends ArmorItem {
    
    protected final int tier;
    protected final double maxFactorStorage;
    
    public FactorArmorItem(ArmorMaterial material, EquipmentType type, int tier, Settings settings) {
        super(material, type, settings);
        this.tier = tier;
        this.maxFactorStorage = getStorageForType(type, tier);
    }
    
    protected static double getStorageForType(EquipmentType type, int tier) {
        // 胸甲存储 Factor，其他部位为辅助功能
        if (type == EquipmentType.CHESTPLATE) {
            return switch (tier) {
                case 1 -> 100.0;
                case 2 -> 250.0;
                case 3 -> 500.0;
                case 4 -> 1000.0;
                case 5 -> 2000.0;
                default -> 100.0;
            };
        }
        return 0.0;
    }
    
    /**
     * 获取护甲等级
     */
    public int getTier() {
        return tier;
    }
    
    /**
     * 获取 Factor 存储容量
     */
    public double getMaxFactorStorage() {
        return maxFactorStorage;
    }
    
    /**
     * 从 ItemStack 获取存储的 Factor
     */
    protected double getFactorStorage(ItemStack stack) {
        var component = stack.get(com.factorcraft.component.FactorCraftDataComponents.FACTOR_STORAGE);
        return component != null ? component.amount() : 0.0;
    }
    
    /**
     * 设置 ItemStack 的 Factor 存储
     */
    protected void setFactorStorage(ItemStack stack, double amount) {
        stack.set(com.factorcraft.component.FactorCraftDataComponents.FACTOR_STORAGE,
            com.factorcraft.component.type.FactorStorage.of(Math.min(amount, maxFactorStorage)));
    }
    
    /**
     * 添加 Factor（充能）
     */
    public double addFactor(ItemStack stack, double amount) {
        double current = getFactorStorage(stack);
        double space = maxFactorStorage - current;
        double toAdd = Math.min(amount, space);
        setFactorStorage(stack, current + toAdd);
        return toAdd;
    }
    
    /**
     * 提取 Factor
     */
    public double extractFactor(ItemStack stack, double amount) {
        double current = getFactorStorage(stack);
        double toExtract = Math.min(amount, current);
        setFactorStorage(stack, current - toExtract);
        return toExtract;
    }
    
    @Override
    public void onCraft(ItemStack stack, World world) {
        super.onCraft(stack, world);
        if (maxFactorStorage > 0) {
            setFactorStorage(stack, 0.0);
        }
    }
    
    /**
     * 检查是否为全套 Factor 护甲
     */
    public static boolean hasFullSet(PlayerEntity player) {
        return isFactorArmor(player.getEquippedStack(EquipmentSlot.HEAD)) &&
               isFactorArmor(player.getEquippedStack(EquipmentSlot.CHEST)) &&
               isFactorArmor(player.getEquippedStack(EquipmentSlot.LEGS)) &&
               isFactorArmor(player.getEquippedStack(EquipmentSlot.FEET));
    }
    
    /**
     * 检查物品是否为 Factor 护甲
     */
    public static boolean isFactorArmor(ItemStack stack) {
        return stack.getItem() instanceof FactorArmorItem;
    }
    
    /**
     * 获取套装奖励
     */
    public static double getSetBonus(int tier) {
        return switch (tier) {
            case 1 -> 0.05;  // 5% 加成
            case 2 -> 0.10;
            case 3 -> 0.15;
            case 4 -> 0.20;
            case 5 -> 0.30;  // 30% 加成
            default -> 0.0;
        };
    }
}