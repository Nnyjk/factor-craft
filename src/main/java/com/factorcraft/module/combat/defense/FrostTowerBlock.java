package com.factorcraft.module.combat.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 冰冻塔 - 减速防御塔
 */
public class FrostTowerBlock extends DefenseTowerBlock {
    
    public FrostTowerBlock(Settings settings) {
        super(settings, 10, 3, 25);
    }
    
    @Override
    public String getTowerType() {
        return "frost";
    }
    
    @Override
    public void attackTarget(World world, BlockPos pos, LivingEntity target) {
        if (!world.isClient) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 1));
            
            for (int i = 0; i < 5; ++i) {
                world.addParticle(ParticleTypes.SNOWFLAKE,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    world.random.nextGaussian() * 0.1, 0.2, world.random.nextGaussian() * 0.1);
            }
        }
    }
}
