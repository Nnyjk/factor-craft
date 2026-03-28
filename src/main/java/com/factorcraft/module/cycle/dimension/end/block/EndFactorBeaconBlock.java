package com.factorcraft.module.cycle.dimension.end.block;

import com.factorcraft.module.cycle.dimension.block.entity.end.EndFactorBeaconBlockEntity;
import com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 末地 Factor 信标
 * 提供增益效果，稳定 Factor 浓度
 */
public class EndFactorBeaconBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty ACTIVE = Properties.ACTIVE;
    
    public EndFactorBeaconBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false));
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(EndFactorBeaconBlock::new);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EndFactorBeaconBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, DimensionBlockEntities.END_FACTOR_BEACON, EndFactorBeaconBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EndFactorBeaconBlockEntity beacon) {
                beacon.toggleActive();
                world.setBlockState(pos, state.with(ACTIVE, !state.get(ACTIVE)));
            }
        }
        return ActionResult.SUCCESS;
    }
}
