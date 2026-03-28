package com.factorcraft.module.combat.defense;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 闪电塔 - 高伤害防御塔
 */
public class LightningTowerBlock extends DefenseTowerBlock {
    
    public LightningTowerBlock(Settings settings) {
        super(settings, 14, 8, 40);
    }
    
    @Override
    public String getTowerType() {
        return "lightning";
    }
    
    @Override
    public void attackTarget(World world, BlockPos pos, LivingEntity target) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            target.damage(serverWorld, serverWorld.getDamageSources().magic(), (float)this.damage);
            
            for (int i = 0; i < 10; ++i) {
                world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    world.random.nextGaussian() * 0.2, 0.3, world.random.nextGaussian() * 0.2);
            }
        }
    }
}
