package com.factorcraft.module.cycle.energy.block;

import com.factorcraft.module.cycle.energy.block.entity.FactorCompressorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 压缩机方块
 * 
 * 将 1000mB 普通 Factor 压缩为 10mB 高密度 Factor (压缩比 100:1)
 * 多方块结构：3x3x3
 */
public class FactorCompressorBlock extends BlockWithEntity {
    
    public static final MapCodec<FactorCompressorBlock> CODEC = createCodec(FactorCompressorBlock::new);
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    public FactorCompressorBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorCompressorBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world,
        BlockState state,
        BlockEntityType<T> type
    ) {
        return validateTicker(type, FactorCompressorBlockEntity.TYPE, FactorCompressorBlockEntity::tick);
    }
    
    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        BlockHitResult hit
    ) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FactorCompressorBlockEntity compressor) {
                NamedScreenHandlerFactory screenHandlerFactory = compressor;
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }
        return ActionResult.SUCCESS;
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
