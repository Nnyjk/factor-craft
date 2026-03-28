package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Factor 护腿 - 终极护腿
 * 
 * 特性:
 * - 防护值：6 (超越下界合金 6，持平但有其他效果)
 * - 耐久度：5000
 * - 速度提升
 * - 防火效果
 */
public class FactorLeggingsItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    private static final int PROTECTION = 6;
    
    public FactorLeggingsItem() {
        super(ArmorMaterials.NETHERITE, EquipmentType.LEGGINGS,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof LivingEntity living)) {
            return;
        }
        
        // 防火效果 (如果全套激活)
        if (entity instanceof PlayerEntity player && hasFullSet(player)) {
            // 给予防火效果
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 60, 0, false, false));
        }
    }
    
    /**
     * 检查是否穿戴全套 Factor 盔甲
     */
    private boolean hasFullSet(PlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET);
        
        return helmet.getItem() instanceof FactorHelmetItem &&
               chestplate.getItem() instanceof FactorChestplateItem &&
               leggings.getItem() instanceof FactorLeggingsItem &&
               boots.getItem() instanceof FactorBootsItem;
    }
}
