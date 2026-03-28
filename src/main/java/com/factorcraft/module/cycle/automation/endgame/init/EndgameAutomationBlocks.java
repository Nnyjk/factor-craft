package com.factorcraft.module.cycle.automation.endgame.init;

import com.factorcraft.module.cycle.automation.endgame.block.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 终局自动化方块注册表
 */
public class EndgameAutomationBlocks {
    
    public static final String MOD_ID = "factorcraft";
    
    // 自动提取器 MK-II
    public static final Block AUTO_EXTRACTOR_MK2 = registerBlock(
        "auto_extractor_mk2",
        new AutoExtractorMK2Block(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "auto_extractor_mk2"))).strength(5.0f, 6.0f).requiresTool())
    );
    
    // Factor 泵 MK-II
    public static final Block FACTOR_PUMP_MK2 = registerBlock(
        "factor_pump_mk2",
        new FactorPumpMK2Block(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "factor_pump_mk2"))).strength(5.0f, 6.0f).requiresTool())
    );
    
    // 高级合成器
    public static final Block ADVANCED_CRAFTER = registerBlock(
        "advanced_crafter",
        new AdvancedCrafterBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "advanced_crafter"))).strength(5.0f, 6.0f).requiresTool())
    );
    
    // 量子仓储单元
    public static final Block QUANTUM_STORAGE = registerBlock(
        "quantum_storage",
        new QuantumStorageBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "quantum_storage"))).strength(5.0f, 6.0f).requiresTool())
    );
    
    private static Block registerBlock(String name, Block block) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        // 注册方块
        Block registeredBlock = Registry.register(Registries.BLOCK, key, block);
        
        // 注册 BlockItem
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = Registry.register(Registries.ITEM, itemKey, new BlockItem(registeredBlock, new Item.Settings().registryKey(itemKey)));
        
        // 添加到物品组
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(item));
        
        return registeredBlock;
    }
    
    public static void init() {
        // 初始化方法，触发静态字段注册
    }
}
