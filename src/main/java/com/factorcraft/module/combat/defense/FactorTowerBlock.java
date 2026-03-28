package com.factorcraft.module.combat.defense;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 塔 - 高级防御塔
 */
public class FactorTowerBlock extends DefenseTowerBlock {
    
    public FactorTowerBlock(Settings settings) {
        super(settings, 16, 6, 15);
    }
    
    @Override
    public String getTowerType() {
        return "factor";
    }
    
    @Override
    public void attackTarget(World world, BlockPos pos, LivingEntity target) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            target.damage(serverWorld, serverWorld.getDamageSources().magic(), (float)this.damage);
            
            for (int i = 0; i < 8; ++i) {
                world.addParticle(ParticleTypes.MYCELIUM,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    world.random.nextGaussian() * 0.15, 0.25, world.random.nextGaussian() * 0.15);
            }
        }
    }
}
