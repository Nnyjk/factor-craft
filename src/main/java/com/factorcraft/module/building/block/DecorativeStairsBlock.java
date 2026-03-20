package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * 装饰楼梯方块
 */
public class DecorativeStairsBlock extends StairsBlock {
    
    public DecorativeStairsBlock(Identifier id, Supplier<BlockState> baseBlockState) {
        super(baseBlockState.get(), AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(3.0f, 6.0f)
            .requiresTool());
    }
}