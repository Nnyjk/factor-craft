package com.factorcraft.module.technology.machine;

import com.factorcraft.api.IFactorContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * 机器基类
 */
public abstract class MachineBlockEntity extends BlockEntity implements IFactorContainer {
    
    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    /**
     * 每 tick 调用
     */
    public abstract void tick(net.minecraft.world.World world, BlockPos pos, BlockState state);
    
    @Override
    public double addFactor(double amount) {
        return 0.0; // 默认实现，子类覆盖
    }
    
    @Override
    public double extractFactor(double amount) {
        return 0.0; // 默认实现，子类覆盖
    }
    
    @Override
    public double getFactorStorage() {
        return 0.0; // 默认实现，子类覆盖
    }
    
    @Override
    public double getMaxFactorStorage() {
        return 0.0; // 默认实现，子类覆盖
    }
}
