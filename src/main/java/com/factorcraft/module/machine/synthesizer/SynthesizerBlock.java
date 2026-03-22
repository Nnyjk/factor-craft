package com.factorcraft.module.machine.synthesizer;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Factor 合成器方块
 * 
 * 将低级 Factor 合成为高级 Factor
 * 使用已有的 FactorSynthesisRecipe 配方系统
 */
public class SynthesizerBlock extends BlockWithEntity {
    
    public static final BooleanProperty ACTIVE = Properties.LIT;
    
    // 使用 createCodec（1.21+ 要求）
    public static final MapCodec<SynthesizerBlock> CODEC = createCodec(SynthesizerBlock::new);
    
    /**
     * 构造器
     * @param settings 方块设置
     */
    public SynthesizerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ACTIVE, false));
    }
    
    // ========== BlockWithEntity 实现 ==========
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SynthesizerBlockEntity(pos, state);
    }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        
        return (w, pos, s, be) -> {
            if (be instanceof SynthesizerBlockEntity synthesizer) {
                SynthesizerBlockEntity.tick(w, pos, s, synthesizer);
            }
        };
    }
    
    // ========== 状态管理 ==========
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    
    // ========== 交互 ==========
    
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, 
                                  PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SynthesizerBlockEntity synthesizer) {
                player.openHandledScreen(synthesizer);
            }
        }
        return ActionResult.SUCCESS;
    }
    
    // ========== 物品掉落 ==========
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, 
                                BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SynthesizerBlockEntity synthesizer) {
                // 掉落物品槽位中的物品
                ItemScatterer.spawn(world, pos, synthesizer);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}