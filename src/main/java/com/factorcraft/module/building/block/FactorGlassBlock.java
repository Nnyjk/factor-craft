package com.factorcraft.module.building.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * Factor 玻璃 - 显示内部 Factor 流动
 */
public class FactorGlassBlock extends TranslucentBlock {
    
    // T1-T5 等级，影响颜色和透明度
    public static final IntProperty TIER = IntProperty.of("tier", 1, 5);
    
    public FactorGlassBlock() {
        super(Settings.create()
            .strength(0.3f)
            .luminance(state -> state.get(TIER) * 2)
            .nonOpaque());
        setDefaultState(getDefaultState().with(TIER, 1));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // 根据放置位置浓度决定等级
        return getDefaultState().with(TIER, 1);
    }
    
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }
}