package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Factor 晶体块 - 压缩 Factor 晶体
 */
public class FactorCrystalBlock extends Block {
    
    public FactorCrystalBlock(Identifier id) {
        super(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(1.5f, 1.5f)
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