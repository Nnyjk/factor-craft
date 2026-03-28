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
 * Factor 虚空实体 - Boss 实体
 */
public class FactorVoidEntity extends HostileEntity {
    
    public FactorVoidEntity(EntityType<? extends FactorVoidEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1000;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 16.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true, false));
        this.targetSelector.add(2, new RevengeGoal(this));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient) {
            for (int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(ParticleTypes.DRAGON_BREATH,
                    this.getX() + this.random.nextGaussian() * 0.5D,
                    this.getY() + 0.5D + this.random.nextGaussian() * 0.5D,
                    this.getZ() + this.random.nextGaussian() * 0.5D,
                    this.random.nextGaussian() * 0.2D, 0.3D, this.random.nextGaussian() * 0.2D);
            }
        }
    }
    
    public static DefaultAttributeContainer.Builder createVoidAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 350.0D)
            .add(EntityAttributes.ATTACK_DAMAGE, 14.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.35D)
            .add(EntityAttributes.FOLLOW_RANGE, 50.0D)
            .add(EntityAttributes.ARMOR, 8.0D);
    }
}
