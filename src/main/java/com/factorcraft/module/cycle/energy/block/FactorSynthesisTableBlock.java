package com.factorcraft.module.cycle.energy.block;

import com.factorcraft.module.cycle.energy.block.entity.FactorSynthesisTableBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Factor 合成台方块
 * 
 * 3x3x3 多方块结构，用于高级 Factor 物品合成
 */
public class FactorSynthesisTableBlock extends BlockWithEntity implements BlockEntityProvider {
    
    public static final MapCodec<FactorSynthesisTableBlock> CODEC = createCodec(FactorSynthesisTableBlock::new);
    
    @Override
    public MapCodec<FactorSynthesisTableBlock> getCodec() {
        return CODEC;
    }
    
    public FactorSynthesisTableBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState());
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // 无额外属性
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorSynthesisTableBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        return validateTicker(type, FactorSynthesisTableBlockEntity.TYPE, FactorSynthesisTableBlockEntity::tick);
    }
    
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FactorSynthesisTableBlockEntity synthesisTable) {
                player.openHandledScreen(synthesisTable);
            }
        }
        return ActionResult.SUCCESS;
    }
}
