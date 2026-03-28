package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Factor 靴子 - 终极靴子
 * 
 * 特性:
 * - 防护值：3 (与下界合金相同)
 * - 耐久度：5000
 * - 飞行能力
 * - 无掉落伤害
 */
public class FactorBootsItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    
    public FactorBootsItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ArmorMaterials.NETHERITE, EquipmentType.BOOTS,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof LivingEntity living) {
            // 缓落 (无掉落伤害)
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 220, 0, false, false));
        }
    }
}
