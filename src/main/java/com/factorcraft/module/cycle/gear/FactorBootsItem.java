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
 * Factor 靴子 - 终极靴子
 * 
 * 特性:
 * - 防护值：3 (超越下界合金 3)
 * - 耐久度：5000
 * - 飞行能力 (创造模式飞行)
 * - 无掉落伤害
 * - 速度提升
 */
public class FactorBootsItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    private static final int PROTECTION = 3;
    
    public FactorBootsItem() {
        super(ArmorMaterials.NETHERITE, EquipmentType.BOOTS,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof LivingEntity living)) {
            return;
        }
        
        // 如果全套激活
        if (entity instanceof PlayerEntity player && hasFullSet(player)) {
            // 速度提升
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 1, false, false));
            
            // 无掉落伤害通过伤害免疫事件处理
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
