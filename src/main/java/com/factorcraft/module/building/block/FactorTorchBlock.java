package com.factorcraft.module.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Factor 火把 - 永久光源
 */
public class FactorTorchBlock extends Block {
    
    protected static final VoxelShape SHAPE = Block.createCuboidShape(6, 0, 6, 10, 10, 10);
    
    public FactorTorchBlock() {
        super(Settings.copy(Blocks.TORCH)
            .luminance(state -> 10)
            .nonOpaque());
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.7;
        double f = (double)pos.getZ() + 0.5;
        
        world.addParticle(ParticleTypes.END_ROD, d, e, f, 0.0, 0.0, 0.0);
    }
}