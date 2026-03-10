package com.factorcraft.module.technology.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * Factor 机器基类
 */
public abstract class FactorMachineBlockEntity extends BlockEntity {
    
    public FactorMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    /**
     * 每 tick 调用
     */
    public abstract void tick(net.minecraft.world.World world, BlockPos pos, BlockState state);
}
