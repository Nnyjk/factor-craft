package com.factorcraft.module.cycle.dimension.gate.block;

import com.factorcraft.module.cycle.dimension.block.entity.gate.DimensionalGateBlockEntity;
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
 * 维度之门方块
 * 多方块传送阵，支持跨维度传输
 */
public class DimensionalGateBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty ACTIVATED = Properties.ACTIVE;
    
    public DimensionalGateBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVATED, false));
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(DimensionalGateBlock::new);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DimensionalGateBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, DimensionBlockEntities.DIMENSIONAL_GATE, DimensionalGateBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DimensionalGateBlockEntity gate) {
                gate.tryTeleport(player);
            }
        }
        return ActionResult.SUCCESS;
    }
}
