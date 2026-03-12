package com.factorcraft.world.generation;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Factor 晶体矿脉生成
 */
public class FactorCrystalOreFeature extends Feature<DefaultFeatureConfig> {
    
    public FactorCrystalOreFeature() {
        super(DefaultFeatureConfig.CODEC);
    }
    
    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();
        
        // 随机选择生成位置
        int x = origin.getX() + random.nextInt(16);
        int z = origin.getZ() + random.nextInt(16);
        int y = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
        
        if (y < 10) return false;
        
        // 生成晶体簇
        generateCrystalCluster(world, new BlockPos(x, y, z), random);
        
        return true;
    }
    
    private void generateCrystalCluster(StructureWorldAccess world, BlockPos center, Random random) {
        // 生成主晶体
        int size = 3 + random.nextInt(3);
        
        for (int dx = -size; dx <= size; dx++) {
            for (int dy = -size; dy <= size; dy++) {
                for (int dz = -size; dz <= size; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    
                    // 球形生成
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (distance <= size && random.nextDouble() > 0.3) {
                        if (canReplace(world.getBlockState(pos))) {
                            world.setBlockState(pos, Blocks.AMETHYST_CLUSTER.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
        
        // 在中心放置 Factor 晶体
        world.setBlockState(center, Blocks.DIAMOND_BLOCK.getDefaultState(), 2);
    }
    
    private boolean canReplace(BlockState state) {
        return state.isAir() || state.isOf(Blocks.STONE) || state.isOf(Blocks.DEEPSLATE);
    }
}