package com.factorcraft.world.structure;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

/**
 * Factor 祭坛结构生成器
 */
public class FactorAltarGenerator {
    
    public static final int SPAWN_CHANCE = 1000; // 1/1000 区块
    
    /**
     * 生成 Factor 祭坛
     */
    public static void generate(StructureWorldAccess world, BlockPos center, Random random) {
        // 清理地面
        clearArea(world, center, 7, 5, 7);
        
        // 建造基座
        buildBase(world, center, random);
        
        // 建造祭坛核心
        buildAltarCore(world, center.up(), random);
        
        // 放置装饰
        placeDecorations(world, center, random);
    }
    
    private static void clearArea(StructureWorldAccess world, BlockPos center, int rx, int ry, int rz) {
        for (int x = -rx; x <= rx; x++) {
            for (int y = -ry; y <= ry; y++) {
                for (int z = -rz; z <= rz; z++) {
                    BlockPos pos = center.add(x, y, z);
                    if (world.getBlockState(pos).isAir()) continue;
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                }
            }
        }
    }
    
    private static void buildBase(StructureWorldAccess world, BlockPos center, Random random) {
        // 石砖平台
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos pos = center.add(x, -1, z);
                world.setBlockState(pos, Blocks.STONE_BRICKS.getDefaultState(), 2);
                
                // 随机损坏
                if (random.nextDouble() < 0.2) {
                    world.setBlockState(pos, Blocks.CRACKED_STONE_BRICKS.getDefaultState(), 2);
                }
            }
        }
        
        // 4 个角柱
        int[][] corners = {{-3, -3}, {-3, 3}, {3, -3}, {3, 3}};
        for (int[] corner : corners) {
            for (int y = 0; y <= 3; y++) {
                BlockPos pos = center.add(corner[0], y, corner[1]);
                world.setBlockState(pos, Blocks.POLISHED_ANDESITE.getDefaultState(), 2);
            }
        }
    }
    
    private static void buildAltarCore(StructureWorldAccess world, BlockPos center, Random random) {
        // 中央祭坛
        world.setBlockState(center, Blocks.POLISHED_DIORITE.getDefaultState(), 2);
        world.setBlockState(center.up(), Blocks.CHISELED_QUARTZ_BLOCK.getDefaultState(), 2);
        
        // Factor 核心
        world.setBlockState(center.up(2), Blocks.BEACON.getDefaultState(), 2);
        
        // 周围光柱
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos pos = center.add(x, 0, z);
                world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState(), 2);
            }
        }
    }
    
    private static void placeDecorations(StructureWorldAccess world, BlockPos center, Random random) {
        // 放置火把
        int[][] torchPositions = {{-2, 0, -2}, {-2, 0, 2}, {2, 0, -2}, {2, 0, 2}};
        for (int[] pos : torchPositions) {
            BlockPos torchPos = center.add(pos[0], pos[1], pos[2]);
            world.setBlockState(torchPos, Blocks.WALL_TORCH.getDefaultState(), 2);
        }
        
        // 放置箱子
        if (random.nextDouble() < 0.5) {
            BlockPos chestPos = center.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
            world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 2);
        }
    }
}