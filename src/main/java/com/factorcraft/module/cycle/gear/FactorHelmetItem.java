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
 * Factor 头盔 - 终极头盔
 * 
 * 特性:
 * - 防护值：7 (超越下界合金 3)
 * - 耐久度：5000
 * - 水下呼吸 (无限)
 * - 夜视效果
 */
public class FactorHelmetItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    
    public FactorHelmetItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ArmorMaterials.NETHERITE, EquipmentType.HELMET,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof LivingEntity living) {
            // 水下呼吸
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 220, 0, false, false));
            // 夜视
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 220, 0, false, false));
        }
    }
}
