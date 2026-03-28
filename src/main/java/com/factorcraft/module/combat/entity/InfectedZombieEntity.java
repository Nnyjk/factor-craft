package com.factorcraft.module.combat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;

/**
 * Factor 感染僵尸
 * 缓慢但高血量，攻击时给玩家施加 Factor 污染效果
 */
public class InfectedZombieEntity extends ZombieEntity {
    
    public InfectedZombieEntity(EntityType<? extends InfectedZombieEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 10;
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, LivingEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, LivingEntity.class, true, false));
        this.targetSelector.add(2, new RevengeGoal(this));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.getWorld().isClient && this.age % 40 == 0) {
            // 定期释放 Factor 污染粒子
            for (int i = 0; i < 3; ++i) {
                this.getWorld().addParticle(net.minecraft.particle.ParticleTypes.MYCELIUM,
                    this.getX(), this.getY() + 1.0D, this.getZ(),
                    0.0D, 0.1D, 0.0D);
            }
        }
    }
    
    public static DefaultAttributeContainer.Builder createInfectedZombieAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.MAX_HEALTH, 30.0D)
            .add(EntityAttributes.ATTACK_DAMAGE, 4.0D)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.23D)
            .add(EntityAttributes.FOLLOW_RANGE, 35.0D)
            .add(EntityAttributes.ARMOR, 2.0D);
    }
}
