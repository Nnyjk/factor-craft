package com.factorcraft.module.cycle.gear;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;

import java.util.List;

/**
 * 量子剑 - 终极剑
 * 
 * 特性:
 * - 高伤害 (10.0)
 * - 耐久度 10,000
 * - 范围溅射攻击 (击中目标时伤害周围敌人)
 */
public class QuantumSwordItem extends SwordItem {
    
    private static final int MAX_DAMAGE = 10000;
    private static final float ATTACK_DAMAGE = 10.0f;
    private static final float ATTACK_SPEED = -2.4f;
    
    public QuantumSwordItem(RegistryKey<net.minecraft.item.Item> key) {
        super(ToolMaterial.NETHERITE, ATTACK_DAMAGE, ATTACK_SPEED,
              new Settings().maxDamage(MAX_DAMAGE).fireproof().registryKey(key));
    }
    
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getWorld().isClient) {
            // 范围溅射伤害
            ServerWorld serverWorld = (ServerWorld) target.getWorld();
            double range = 3.0;
            
            // 查找范围内的所有敌人
            List<LivingEntity> nearbyEntities = target.getWorld().getEntitiesByClass(
                LivingEntity.class,
                target.getBoundingBox().expand(range),
                entity -> entity != target && entity != attacker && !entity.isPlayer()
            );
            
            // 创建伤害来源 (使用 player 作为来源)
            DamageSource source = attacker.getDamageSources().playerAttack((PlayerEntity) attacker);
            
            for (LivingEntity entity : nearbyEntities) {
                entity.damage(serverWorld, source, 5.0f);
            }
            
            // 消耗 Factor 充能
            Integer charge = stack.get(FactorGearComponents.FACTOR_CHARGE);
            if (charge != null && charge > 0) {
                stack.set(FactorGearComponents.FACTOR_CHARGE, charge - 2); // 溅射消耗更多
            }
        }
        return true;
    }
}
