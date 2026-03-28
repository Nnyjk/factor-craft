package com.factorcraft.module.cycle.energy.block;

import com.factorcraft.module.cycle.energy.block.entity.FactorReactorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 反应堆方块
 * 
 * 5x5x5 多方块结构，将高密度 Factor 转化为能量输出
 * 需要冷却剂防止过热
 */
public class FactorReactorBlock extends BlockWithEntity {
    
    public static final MapCodec<FactorReactorBlock> CODEC = createCodec(FactorReactorBlock::new);
    
    public static Block FACTOR_REACTOR;
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    public FactorReactorBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorReactorBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, FactorReactorBlockEntity.TYPE, FactorReactorBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FactorReactorBlockEntity reactor) {
                player.openHandledScreen(reactor);
            }
        }
        return ActionResult.SUCCESS;
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FactorReactorBlockEntity reactor) {
                // 掉落物品
                reactor.dropInventory();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
