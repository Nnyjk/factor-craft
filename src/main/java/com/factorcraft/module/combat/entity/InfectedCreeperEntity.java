package com.factorcraft.module.combat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

/**
 * Factor 感染苦力怕
 * 爆炸后留下 Factor 污染区域
 */
public class InfectedCreeperEntity extends CreeperEntity {
    
    public InfectedCreeperEntity(EntityType<? extends InfectedCreeperEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 15;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }
    
    public static DefaultAttributeContainer.Builder createInfectedCreeperAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 25.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.25D)
            .add(EntityAttributes.FOLLOW_RANGE, 35.0D);
    }
    
    @Override
    public void onDeath(net.minecraft.entity.damage.DamageSource damageSource) {
        super.onDeath(damageSource);
        // 死亡时释放 Factor 粒子
        World world = this.getWorld();
        if (!world.isClient) {
            for (int i = 0; i < 30; i++) {
                world.addParticle(
                    ParticleTypes.MYCELIUM,
                    this.getX() + world.random.nextGaussian() * 0.5D,
                    this.getY() + 1.0D + world.random.nextGaussian() * 0.5D,
                    this.getZ() + world.random.nextGaussian() * 0.5D,
                    world.random.nextGaussian() * 0.1D,
                    0.2D,
                    world.random.nextGaussian() * 0.1D
                );
            }
        }
    }
}
