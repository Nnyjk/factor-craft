package com.factorcraft.module.cycle.dimension.gate.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

/**
 * 传送稳定器方块
 * 稳定维度传送门，减少传送误差
 */
public class GateStabilizerBlock extends Block {
    public static final BooleanProperty ACTIVE = Properties.ACTIVE;
    
    public GateStabilizerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
}
