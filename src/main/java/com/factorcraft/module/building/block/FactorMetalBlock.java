package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;

/**
 * Factor 金属块 - T1-T5 材料块
 */
public class FactorMetalBlock extends Block {
    
    public static final IntProperty TIER = IntProperty.of("tier", 1, 5);
    
    public FactorMetalBlock(Identifier id) {
        super(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(6.0f, 6.0f));
        setDefaultState(getDefaultState().with(TIER, 1));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TIER);
    }
    
    @Override
    public float getHardness() {
        return 6.0f; // Base hardness
    }
    
    /**
     * 创建指定等级的金属块
     */
    public static BlockState createTierState(int tier) {
        return FACTOR_METAL.getDefaultState().with(TIER, Math.max(1, Math.min(5, tier)));
    }
    
    // 静态实例引用
    public static FactorMetalBlock FACTOR_METAL;
}