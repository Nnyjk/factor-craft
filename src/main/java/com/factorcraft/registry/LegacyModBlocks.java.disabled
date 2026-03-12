package com.factorcraft.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Mod 方块注册
 */
public class ModBlocks {
    
    // Factor 培育核心
    public static final Block CULTIVATION_CORE = new Block(
        AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .strength(3.0f, 6.0f)
    );
    
    // Factor 锚定器
    public static final Block FACTOR_ANCHOR = new Block(
        AbstractBlock.Settings.copy(Blocks.STONE)
            .strength(2.5f, 4.0f)
    );
    
    // Factor 提取器
    public static final Block FACTOR_EXTRACTOR = new Block(
        AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
            .strength(3.5f, 5.0f)
    );
    
    // BlockEntity Types (后续实现)
    public static BlockEntityType<?> CULTIVATION_CORE_ENTITY;
    
    public static void register() {
        registerBlock("cultivation_core", CULTIVATION_CORE);
        registerBlock("factor_anchor", FACTOR_ANCHOR);
        registerBlock("factor_extractor", FACTOR_EXTRACTOR);
    }
    
    private static void registerBlock(String name, Block block) {
        Registry.register(Registries.BLOCK, Identifier.of("factorcraft", name), block);
        Registry.register(
            Registries.ITEM,
            Identifier.of("factorcraft", name),
            new BlockItem(block, new Item.Settings())
        );
    }
}