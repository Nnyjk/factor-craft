package com.factorcraft.module.combat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

/**
 * Factor 感染史莱姆
 * 分裂时释放 Factor 气体
 */
public class InfectedSlimeEntity extends SlimeEntity {
    
    public InfectedSlimeEntity(EntityType<? extends InfectedSlimeEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.add(3, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true));
    }
    
    public static DefaultAttributeContainer.Builder createInfectedSlimeAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 16.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.3D)
            .add(EntityAttributes.FOLLOW_RANGE, 20.0D);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 分裂时释放 Factor 粒子
        if (!this.getWorld().isClient && this.age % 20 == 0) {
            for (int i = 0; i < 5; i++) {
                this.getWorld().addParticle(
                    ParticleTypes.MYCELIUM,
                    this.getX() + this.getWorld().random.nextGaussian() * 0.3D,
                    this.getY() + 0.5D + this.getWorld().random.nextGaussian() * 0.3D,
                    this.getZ() + this.getWorld().random.nextGaussian() * 0.3D,
                    0.0D,
                    0.1D,
                    0.0D
                );
            }
        }
    }
}
