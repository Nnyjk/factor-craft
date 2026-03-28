package com.factorcraft.module.cycle.energy.block;

import com.factorcraft.module.cycle.energy.block.entity.FactorPumpBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 泵方块
 * 
 * 加速 Factor 浓度传输的方块
 * 消耗 Factor 浓度运行，支持红石控制
 */
public class FactorPumpBlock extends BlockWithEntity {
    
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    
    public FactorPumpBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ENABLED, true));
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(FactorPumpBlock::new);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorPumpBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, 
                               PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof FactorPumpBlockEntity pump) {
            // 显示泵状态
            double stored = pump.getBufferFactor();
            double rate = pump.getConsumptionRate();
            boolean active = pump.isActive();
            
            player.sendMessage(Text.literal(String.format(
                "Factor Pump: %.2f | Rate: %.2f/tick | Active: %s",
                stored, rate, active ? "Yes" : "No"
            )), true);
            
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, 
                                                                   BlockEntityType<T> type) {
        return validateTicker(type, FactorPumpBlockEntity.TYPE, FactorPumpBlockEntity::tick);
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!newState.isOf(state.getBlock())) {
            // 方块被移除时清理逻辑
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
