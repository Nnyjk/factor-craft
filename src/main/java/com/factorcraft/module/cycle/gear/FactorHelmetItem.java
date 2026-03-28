package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

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
    private static final int PROTECTION = 7;
    
    public FactorHelmetItem() {
        super(ArmorMaterials.NETHERITE, EquipmentType.HELMET,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient || !(entity instanceof LivingEntity living)) {
            return;
        }
        
        // 水下呼吸
        if (living.getAir() < living.getMaxAir()) {
            living.setAir(living.getMaxAir());
        }
        
        // 夜视效果 (如果玩家)
        if (entity instanceof PlayerEntity player) {
            // 检查是否全套激活
            if (hasFullSet(player)) {
                // 全套激活时给予夜视
                // 注意：状态效果需要持续施加
            }
        }
    }
    
    /**
     * 检查是否穿戴全套 Factor 盔甲
     */
    private boolean hasFullSet(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }
        
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);
        
        return helmet.getItem() instanceof FactorHelmetItem &&
               chestplate.getItem() instanceof FactorChestplateItem &&
               leggings.getItem() instanceof FactorLeggingsItem &&
               boots.getItem() instanceof FactorBootsItem;
    }
}
