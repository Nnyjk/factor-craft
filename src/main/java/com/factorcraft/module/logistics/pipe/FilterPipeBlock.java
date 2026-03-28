package com.factorcraft.module.logistics.pipe;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 过滤管道
 * 
 * 功能：
 * - 配置允许的 Factor 类型
 * - 白名单/黑名单模式
 */
public class FilterPipeBlock extends Block implements BlockEntityProvider {
    
    public static final Settings SETTINGS = AdvancedFactorPipeBlock.SETTINGS;
    
    public FilterPipeBlock() {
        super(SETTINGS);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FilterPipeBlockEntity(pos, state);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != LogisticsPipes.FILTER_PIPE_ENTITY) return null;
        return world.isClient ? null : (w, p, s, be) -> FilterPipeBlockEntity.tick(w, p, s, (FilterPipeBlockEntity)be);
    }
}
