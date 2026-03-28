package com.factorcraft.module.cycle.energy.block;

import com.factorcraft.module.cycle.energy.block.entity.FactorCrystalBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 晶体方块
 * 
 * 用于存储 Factor 浓度的储能方块
 * 可以自然吸收环境中的 Factor 浓度
 * 也可以为机器提供 Factor 能源
 */
public class FactorCrystalBlock extends BlockWithEntity {
    
    public FactorCrystalBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(FactorCrystalBlock::new);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorCrystalBlockEntity(pos, state);
    }
    
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, 
                               PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof FactorCrystalBlockEntity crystal) {
            // 显示当前存储量
            double stored = crystal.getStoredFactor();
            double capacity = crystal.getCapacity();
            double ratio = crystal.getFillRatio();
            
            player.sendMessage(Text.literal(String.format(
                "Factor Crystal: %.2f / %.2f (%.1f%%)",
                stored, capacity, ratio * 100
            )), true);
            
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, 
                                                                   BlockEntityType<T> type) {
        return validateTicker(type, FactorCrystalBlockEntity.TYPE, FactorCrystalBlockEntity::tick);
    }
}
