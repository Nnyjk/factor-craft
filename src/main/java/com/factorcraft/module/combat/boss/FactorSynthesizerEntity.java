package com.factorcraft.module.combat.boss;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

/**
 * Factor 合成体 - Boss 实体
 */
public class FactorSynthesizerEntity extends HostileEntity {
    
    public FactorSynthesizerEntity(EntityType<? extends FactorSynthesizerEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 800;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.8D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 12.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true, false));
        this.targetSelector.add(2, new RevengeGoal(this));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient) {
            for (int i = 0; i < 5; ++i) {
                this.getWorld().addParticle(ParticleTypes.PORTAL,
                    this.getX() + this.random.nextGaussian() * 0.5D,
                    this.getY() + 1.0D + this.random.nextGaussian() * 0.5D,
                    this.getZ() + this.random.nextGaussian() * 0.5D,
                    this.random.nextGaussian() * 0.1D, 0.2D, this.random.nextGaussian() * 0.1D);
            }
        }
    }
    
    public static DefaultAttributeContainer.Builder createSynthesizerAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 400.0D)
            .add(EntityAttributes.ATTACK_DAMAGE, 12.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25D)
            .add(EntityAttributes.FOLLOW_RANGE, 45.0D)
            .add(EntityAttributes.ARMOR, 10.0D)
            .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.8D);
    }
}
