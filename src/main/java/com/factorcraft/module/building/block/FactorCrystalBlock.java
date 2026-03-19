package com.factorcraft.module.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Factor 晶体块 - 压缩 Factor 晶体
 */
public class FactorCrystalBlock extends Block {
    
    public FactorCrystalBlock() {
        super(Settings.copy(Blocks.AMETHYST_BLOCK)
            .luminance(state -> 8)
            .nonOpaque());
    }
    
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(5) == 0) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + 1.0;
            double f = (double)pos.getZ() + random.nextDouble();
            
            world.addParticle(ParticleTypes.END_ROD, d, e, f, 0.0, 0.05, 0.0);
        }
    }
}