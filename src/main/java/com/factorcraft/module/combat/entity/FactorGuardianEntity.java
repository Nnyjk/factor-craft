package com.factorcraft.module.combat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

/**
 * Factor Guardian - Boss 实体
 * 守护高浓度 Factor 区域的强大敌人
 */
public class FactorGuardianEntity extends HostileEntity {
    
    public FactorGuardianEntity(EntityType<? extends FactorGuardianEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 500;
    }
    
    public static DefaultAttributeContainer.Builder createGuardianAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 300.0)
            .add(EntityAttributes.ATTACK_DAMAGE, 15.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3)
            .add(EntityAttributes.FOLLOW_RANGE, 50.0)
            .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(EntityAttributes.ARMOR, 10.0);
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    public boolean isFireImmune() {
        return true;
    }
}