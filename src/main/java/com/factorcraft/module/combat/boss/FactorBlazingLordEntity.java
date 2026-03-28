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
 * Factor 炽热领主 - Boss 实体
 */
public class FactorBlazingLordEntity extends HostileEntity {
    
    private int flameAttackCooldown = 0;
    
    public FactorBlazingLordEntity(EntityType<? extends FactorBlazingLordEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1200;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.9D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.9D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 14.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true, false));
        this.targetSelector.add(2, new RevengeGoal(this));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient) {
            for (int i = 0; i < 6; ++i) {
                this.getWorld().addParticle(ParticleTypes.FLAME,
                    this.getX() + this.random.nextGaussian() * 0.5D,
                    this.getY() + 0.5D + this.random.nextGaussian() * 0.5D,
                    this.getZ() + this.random.nextGaussian() * 0.5D,
                    this.random.nextGaussian() * 0.1D, 0.15D, this.random.nextGaussian() * 0.1D);
            }
            
            if (this.flameAttackCooldown > 0) {
                this.flameAttackCooldown--;
            }
        }
    }
    
    public static DefaultAttributeContainer.Builder createBlazingLordAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 450.0D)
            .add(EntityAttributes.ATTACK_DAMAGE, 16.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.28D)
            .add(EntityAttributes.FOLLOW_RANGE, 48.0D)
            .add(EntityAttributes.ARMOR, 12.0D)
            .add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.6D);
    }
}
