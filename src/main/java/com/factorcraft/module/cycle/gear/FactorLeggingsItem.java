package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Factor 护腿 - 终极护腿
 * 
 * 特性:
 * - 防护值：6 (超越下界合金 6)
 * - 耐久度：5000
 * - 防火
 * - 速度提升
 */
public class FactorLeggingsItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    
    public FactorLeggingsItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ArmorMaterials.NETHERITE, EquipmentType.LEGGINGS,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof LivingEntity living) {
            // 防火
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 220, 0, false, false));
            // 速度提升
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 220, 1, false, false));
        }
    }
}
