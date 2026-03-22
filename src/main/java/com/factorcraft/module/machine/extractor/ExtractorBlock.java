package com.factorcraft.module.machine.extractor;

import com.factorcraft.FactorCraftMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * 提取器方块
 * 
 * 从物品中提取 Factor 的机器方块
 * 支持 T1-T3 三个等级
 */
public class ExtractorBlock extends Block implements BlockEntityProvider {
    
    // 方块状态属性
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
    
    private final int tier;
    
    public ExtractorBlock(int tier, Settings settings) {
        super(settings);
        this.tier = tier;
        
        // 设置默认方块状态
        setDefaultState(getStateManager().getDefaultState()
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(ACTIVE, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, ACTIVE);
    }
    
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExtractorBlockEntity(pos, state, tier);
    }
    
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ExtractorBlockEntity::tick);
    }
    
    /**
     * 验证并返回 ticker
     */
    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> 
            BlockEntityTicker<A> validateTicker(BlockEntityType<A> type, BlockEntityTicker<E> ticker) {
        // 返回通用 ticker，让 ExtractorBlockEntity 自己处理
        return (BlockEntityTicker<A>) ticker;
    }
    
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }
    
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, 
                                  PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof NamedScreenHandlerFactory factory) {
            player.openHandledScreen(factory);
            return ActionResult.CONSUME;
        }
        
        return ActionResult.PASS;
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, 
                                BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtractorBlockEntity extractor) {
                extractor.onBreak();
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ExtractorBlockEntity extractor) {
            // 返还能量级别作为比较器输出
            return extractor.getComparatorOutput();
        }
        return 0;
    }
    
    /**
     * 获取方块等级
     */
    public int getTier() {
        return tier;
    }
    
    /**
     * BlockEntity 类型检查辅助方法
     */
    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> 
            BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, 
                                           BlockEntityType<E> expectedType,
                                           BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}