package com.factorcraft.module.logistics.pipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 单向管道
 * 
 * 功能：
 * - 只允许一个方向的 Factor 流动
 * - 防止回流
 */
public class OneWayPipeBlock extends Block implements BlockEntityProvider {
    
    public static final Settings SETTINGS = AdvancedFactorPipeBlock.SETTINGS;
    
    public OneWayPipeBlock() {
        super(SETTINGS);
        setDefaultState(getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }
    
    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OneWayPipeBlockEntity(pos, state);
    }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        if (type != LogisticsPipes.ONE_WAY_PIPE_ENTITY) return null;
        return (w, p, s, be) -> OneWayPipeBlockEntity.tick(w, p, s, (OneWayPipeBlockEntity)be);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof OneWayPipeBlockEntity pipe) {
                // 循环切换输出方向
                Direction current = pipe.getOutputSide();
                Direction next = switch (current) {
                    case UP -> Direction.DOWN;
                    case DOWN -> Direction.NORTH;
                    case NORTH -> Direction.SOUTH;
                    case SOUTH -> Direction.EAST;
                    case EAST -> Direction.WEST;
                    case WEST -> Direction.UP;
                };
                pipe.setOutputSide(next);
                player.sendMessage(net.minecraft.text.Text.literal("输出方向：" + next.getName()), true);
            }
        }
        return ActionResult.SUCCESS;
    }
}
