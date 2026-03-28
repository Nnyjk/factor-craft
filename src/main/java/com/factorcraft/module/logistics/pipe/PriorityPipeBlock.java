package com.factorcraft.module.logistics.pipe;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 优先级管道
 * 
 * 功能：
 * - 可配置优先级（1-10）
 * - 高优先级优先传输
 */
public class PriorityPipeBlock extends Block implements BlockEntityProvider {
    
    public static Settings createSettings(RegistryKey<Block> key) {
        return Settings.create().registryKey(key)
            .strength(2.0f, 10.0f)
            .nonOpaque()
            .ticksRandomly();
    }
    
    public PriorityPipeBlock(Settings settings) {
        super(settings);
    }
    
    public PriorityPipeBlock() {
        this(createSettings(null));
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PriorityPipeBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : (w, p, s, be) -> {
            if (be instanceof PriorityPipeBlockEntity pipe) {
                pipe.tick();
            }
        };
    }
}
