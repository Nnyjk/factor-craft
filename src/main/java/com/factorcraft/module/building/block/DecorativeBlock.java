package com.factorcraft.module.building.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 装饰方块基类
 * 
 * 用于 Tier 风格装饰板的统一基类
 */
public class DecorativeBlock extends Block {
    
    private final int tier;
    private final String tierName;
    
    public DecorativeBlock(Identifier id, int tier, String tierName) {
        super(AbstractBlock.Settings.create()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
            .strength(3.0f, 6.0f)
            .requiresTool());
        this.tier = tier;
        this.tierName = tierName;
    }
    
    public int getTier() {
        return tier;
    }
    
    public String getTierName() {
        return tierName;
    }
}