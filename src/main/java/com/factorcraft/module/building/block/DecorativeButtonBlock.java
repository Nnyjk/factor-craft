package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.ButtonBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 装饰按钮方块
 */
public class DecorativeButtonBlock extends ButtonBlock {
    
    public DecorativeButtonBlock(Identifier id) {
        super(BlockSetType.OAK, 10, AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(3.0f, 6.0f)
            .requiresTool()
            .noCollision());
    }
}