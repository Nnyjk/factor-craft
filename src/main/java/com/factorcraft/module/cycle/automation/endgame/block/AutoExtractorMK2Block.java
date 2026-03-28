package com.factorcraft.module.cycle.automation.endgame.block;

import com.factorcraft.module.cycle.automation.endgame.block.entity.AutoExtractorMK2BlockEntity;
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
 * 自动提取器 MK-II
 * 高速 Factor 提取设备，支持超频功能
 * 处理速度：10 items/tick
 * 支持超频：最高 4x
 */
public class AutoExtractorMK2Block extends BlockWithEntity implements BlockEntityProvider {
    
    public static final MapCodec<AutoExtractorMK2Block> CODEC = createCodec(AutoExtractorMK2Block::new);
    
    public AutoExtractorMK2Block(Settings settings) {
        super(settings);
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AutoExtractorMK2BlockEntity(pos, state);
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
        if (blockEntity instanceof AutoExtractorMK2BlockEntity extractor) {
            player.openHandledScreen((NamedScreenHandlerFactory) extractor);
        }
        
        return ActionResult.CONSUME;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, EndgameAutomationBlockEntities.AUTO_EXTRACTOR_MK2, AutoExtractorMK2BlockEntity::tick);
    }
}
