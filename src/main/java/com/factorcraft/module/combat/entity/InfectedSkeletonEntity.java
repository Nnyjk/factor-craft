package com.factorcraft.module.combat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

/**
 * Factor 感染骷髅
 * 远程攻击，射出 Factor 箭矢（带负面效果）
 */
public class InfectedSkeletonEntity extends SkeletonEntity {
    
    public InfectedSkeletonEntity(EntityType<? extends InfectedSkeletonEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 12;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.add(2, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true, false));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient && this.age % 60 == 0) {
            for (int i = 0; i < 2; ++i) {
                this.getWorld().addParticle(ParticleTypes.MYCELIUM,
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    0.0D, 0.1D, 0.0D);
            }
        }
    }
    
    public static DefaultAttributeContainer.Builder createInfectedSkeletonAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20.0D)
            .add(EntityAttributes.ATTACK_DAMAGE, 3.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25D)
            .add(EntityAttributes.FOLLOW_RANGE, 50.0D)
            .add(EntityAttributes.ARMOR, 1.0D);
    }
}
