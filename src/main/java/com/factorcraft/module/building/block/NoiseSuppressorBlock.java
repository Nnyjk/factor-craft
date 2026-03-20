package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 消音方块 - 屏蔽半径8格内的机器运行音效
 */
public class NoiseSuppressorBlock extends Block {
    
    public static final int SUPPRESSION_RADIUS = 8;
    
    public NoiseSuppressorBlock(Identifier id) {
        super(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(3.0f, 6.0f)
            .requiresTool());
    }
}