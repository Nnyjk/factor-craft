package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;

/**
 * 量子剑 - 终极武器
 * 
 * 特性:
 * - 超越下界合金的伤害 (10.0)
 * - 耐久度 10,000
 * - 范围攻击 (击中时伤害周围敌人)
 * - 击退增强
 */
public class QuantumSwordItem extends SwordItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 10.0f;
    private static final float ATTACK_SPEED = -2.4f;
    
    public QuantumSwordItem() {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof());
    }
    
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(target.getWorld() instanceof ServerWorld world)) {
            return super.postHit(stack, target, attacker);
        }
        
        // 范围攻击 - 伤害周围 3 格内的敌人
        double range = 3.0;
        var nearbyEntities = world.getEntitiesByClass(
            LivingEntity.class,
            target.getBoundingBox().expand(range),
            entity -> entity != target && entity != attacker && !entity.isDead()
        );
        
        for (LivingEntity nearby : nearbyEntities) {
            // 造成 50% 的伤害
            float splashDamage = ATTACK_DAMAGE * 0.5f;
            nearby.damage(world, world.getDamageSources().playerAttack((PlayerEntity) attacker), splashDamage);
            
            // 击退效果
            double knockbackStrength = 1.5;
            double dx = nearby.getX() - attacker.getX();
            double dz = nearby.getZ() - attacker.getZ();
            nearby.takeKnockback(knockbackStrength, dx, dz);
        }
        
        // 消耗耐久
        stack.damage(1, attacker, EquipmentSlot.MAINHAND);
        
        return super.postHit(stack, target, attacker);
    }
}
