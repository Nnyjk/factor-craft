package com.factorcraft.module.cycle.energy.block;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.energy.block.entity.FactorStabilizerBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Factor 稳定器方块
 * 
 * 用于稳定高浓度 Factor 环境，防止 Factor 溢出和浓度波动
 * 
 * 功能：
 * - 降低周围区域的 Factor 浓度波动
 * - 当 Factor 浓度超过阈值时自动吸收多余 Factor
 * - 影响周围 16 格范围内的 Factor 环境
 */
public class FactorStabilizerBlock extends BlockWithEntity implements BlockEntityProvider {
    
    public static final MapCodec<FactorStabilizerBlock> CODEC = createCodec(FactorStabilizerBlock::new);
    
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
    
    public FactorStabilizerBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FactorStabilizerBlockEntity(pos, state);
    }
    
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, FactorStabilizerBlockEntity.TYPE, FactorStabilizerBlockEntity::tick);
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
            if (blockEntity instanceof NamedScreenHandlerFactory) {
                player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
            }
        }
        
        return ActionResult.CONSUME;
    }
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FactorStabilizerBlockEntity stabilizer) {
                // TODO: 如果有物品栏，在这里掉落物品
                // 目前稳定器没有物品栏，只需更新比较器
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FactorStabilizerBlockEntity stabilizer) {
            int factorAmount = stabilizer.getFactorAmount();
            int maxFactor = stabilizer.getMaxFactor();
            return (factorAmount * 15) / maxFactor;
        }
        return 0;
    }
}
