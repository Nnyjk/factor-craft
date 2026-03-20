package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 装饰发光灯方块 - 支持右键调整亮度（0~15级）
 */
public class DecorativeLampBlock extends Block {
    
    public static final IntProperty LIGHT_LEVEL = IntProperty.of("light_level", 0, 15);
    
    private final int tier;
    
    public DecorativeLampBlock(Identifier id, int tier) {
        super(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(3.0f, 6.0f)
            .requiresTool()
            .luminance(state -> state.get(LIGHT_LEVEL)));
        this.tier = tier;
        setDefaultState(getDefaultState().with(LIGHT_LEVEL, 15));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_LEVEL);
    }
    
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            int currentLevel = state.get(LIGHT_LEVEL);
            int newLevel = (currentLevel + 1) % 16;
            world.setBlockState(pos, state.with(LIGHT_LEVEL, newLevel));
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }
    
    public int getTier() {
        return tier;
    }
}