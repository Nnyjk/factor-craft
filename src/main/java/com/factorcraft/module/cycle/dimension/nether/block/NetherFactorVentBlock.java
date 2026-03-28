package com.factorcraft.module.cycle.dimension.nether.block;

import com.factorcraft.module.cycle.dimension.block.entity.nether.NetherFactorVentBlockEntity;
import com.factorcraft.module.cycle.dimension.block.entity.DimensionBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 下界 Factor 喷口方块
 * 高温 Factor，快速扩散，产生燃烧效果
 */
public class NetherFactorVentBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty ACTIVE = Properties.ACTIVE;
    
    public NetherFactorVentBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false));
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(NetherFactorVentBlock::new);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new NetherFactorVentBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, DimensionBlockEntities.NETHER_FACTOR_VENT, NetherFactorVentBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NamedScreenHandlerFactory factory) {
                player.openHandledScreen(factory);
            }
        }
        return ActionResult.SUCCESS;
    }
}
