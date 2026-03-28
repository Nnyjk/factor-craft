package com.factorcraft.module.cycle.dimension.gate.block;

import com.factorcraft.module.cycle.dimension.block.entity.gate.GateControllerBlockEntity;
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
 * 传送控制器方块
 * 控制维度传送门的运行
 */
public class GateControllerBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty ACTIVE = Properties.ACTIVE;
    
    public GateControllerBlock(AbstractBlock.Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(ACTIVE, false));
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(GateControllerBlock::new);
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GateControllerBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, DimensionBlockEntities.GATE_CONTROLLER, GateControllerBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GateControllerBlockEntity controller) {
                controller.openUI(player);
            }
        }
        return ActionResult.SUCCESS;
    }
}
