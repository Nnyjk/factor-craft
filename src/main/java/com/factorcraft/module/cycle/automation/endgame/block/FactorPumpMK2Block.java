package com.factorcraft.module.cycle.automation.endgame.block;

import com.factorcraft.module.cycle.automation.endgame.block.entity.FactorPumpMK2BlockEntity;
import com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Factor 泵 MK-II
 * 高速 Factor 液体传输设备
 * 传输速度：1000 mB/tick
 * 支持多方向输出
 */
public class FactorPumpMK2Block extends BlockWithEntity implements BlockEntityProvider {
    
    public static final MapCodec<FactorPumpMK2Block> CODEC = createCodec(FactorPumpMK2Block::new);
    
    public FactorPumpMK2Block(Settings settings) {
        super(settings);
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorPumpMK2BlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FactorPumpMK2BlockEntity pump) {
            player.openHandledScreen((NamedScreenHandlerFactory) pump);
        }
        
        return ActionResult.CONSUME;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, EndgameAutomationBlockEntities.FACTOR_PUMP_MK2, FactorPumpMK2BlockEntity::tick);
    }
}
