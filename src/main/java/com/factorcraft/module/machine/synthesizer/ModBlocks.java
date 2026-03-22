package com.factorcraft.module.machine.synthesizer;

import com.factorcraft.FactorCraftMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Factor 合成器方块注册
 */
public class ModBlocks {
    
    // ========== Factor 合成器方块 ==========
    
    public static final Block FACTOR_SYNTHESIZER = registerSynthesizer("factor_synthesizer");
    
    // ========== 注册方法 ==========
    
    private static Block registerSynthesizer(String name) {
        // 创建 RegistryKey
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, name));
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(FactorCraftMod.MOD_ID, name));
        
        // 创建方块
        Block block = new SynthesizerBlock(
            AbstractBlock.Settings.create()
                .strength(3.0f, 6.0f)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .registryKey(blockKey)
                .luminance(state -> state.get(SynthesizerBlock.ACTIVE) ? 10 : 0)
        );
        
        // 注册方块
        Registry.register(Registries.BLOCK, blockKey, block);
        
        // 注册方块物品
        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, blockItem);
        
        return block;
    }
    
    // ========== 初始化 ==========
    
    public static void init() {
        // 静态初始化已自动注册
    }
}