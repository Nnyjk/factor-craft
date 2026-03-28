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
 * Factor 胸甲 - 终极胸甲
 * 
 * 特性:
 * - 防护值：10 (超越下界合金 8)
 * - 耐久度：5000
 * - 生命恢复 II
 */
public class FactorChestplateItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    
    public FactorChestplateItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ArmorMaterials.NETHERITE, EquipmentType.CHESTPLATE,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof LivingEntity living) {
            // 生命恢复 II
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 220, 1, false, false));
        }
    }
}
