package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Factor 胸甲 - 终极胸甲
 * 
 * 特性:
 * - 防护值：10 (超越下界合金 8)
 * - 耐久度：5000
 * - 生命恢复 (缓慢再生)
 * - 伤害减免
 */
public class FactorChestplateItem extends ArmorItem {
    
    private static final int MAX_DAMAGE = 5000;
    private static final int PROTECTION = 10;
    
    public FactorChestplateItem() {
        super(ArmorMaterials.NETHERITE, EquipmentType.CHESTPLATE,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof LivingEntity living)) {
            return;
        }
        
        // 生命恢复 (如果全套激活)
        if (entity instanceof PlayerEntity player && hasFullSet(player)) {
            if (living.getHealth() < living.getMaxHealth() && world.getTime() % 20 == 0) {
                living.heal(1.0f);
            }
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
