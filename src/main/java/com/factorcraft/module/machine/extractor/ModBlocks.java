package com.factorcraft.module.machine.extractor;

import com.factorcraft.FactorCraftMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * 提取器方块注册
 */
public class ModBlocks {
    
    private static final String MOD_ID = FactorCraftMod.MOD_ID;
    
    // ========== 提取器方块 T1-T3 ==========
    
    /** 提取器 T1 - 基础 */
    public static final Block EXTRACTOR_T1 = registerExtractorBlock("extractor_t1", 1, 3.0f, 4);
    
    /** 提取器 T2 - 中级 */
    public static final Block EXTRACTOR_T2 = registerExtractorBlock("extractor_t2", 2, 4.0f, 7);
    
    /** 提取器 T3 - 高级 */
    public static final Block EXTRACTOR_T3 = registerExtractorBlock("extractor_t3", 3, 5.0f, 10);
    
    /**
     * 注册提取器方块
     */
    private static Block registerExtractorBlock(String name, int tier, float hardness, int luminance) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new ExtractorBlock(
            tier,
            AbstractBlock.Settings.create()
                .registryKey(key)
                .mapColor(MapColor.IRON_GRAY)
                .strength(hardness, hardness * 2)
                .sounds(BlockSoundGroup.METAL)
                .pistonBehavior(PistonBehavior.BLOCK)
                .luminance(state -> state.get(ExtractorBlock.ACTIVE) ? luminance : 0)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册所有方块
     */
    public static void register() {
        // 静态初始化已自动注册
        FactorCraftMod.LOGGER.info("Registering extractor blocks for " + MOD_ID);
    }
}