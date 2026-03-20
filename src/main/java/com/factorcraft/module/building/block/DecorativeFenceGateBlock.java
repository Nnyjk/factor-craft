package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WoodType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 装饰栅栏门方块
 */
public class DecorativeFenceGateBlock extends FenceGateBlock {
    
    public DecorativeFenceGateBlock(Identifier id) {
        super(WoodType.OAK, AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(3.0f, 6.0f)
            .requiresTool());
    }
}