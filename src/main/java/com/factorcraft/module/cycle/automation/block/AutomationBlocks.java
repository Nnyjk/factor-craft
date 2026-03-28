package com.factorcraft.module.cycle.automation.block;

import com.factorcraft.module.cycle.automation.block.crafter.AutoCrafterBlock;
import com.factorcraft.module.cycle.automation.block.distributor.AutoDistributorBlock;
import com.factorcraft.module.cycle.automation.block.harvester.AutoHarvesterBlock;
import com.factorcraft.module.cycle.automation.block.controller.SystemControllerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 自动化模块方块注册
 */
public class AutomationBlocks {
    
    // 自动合成器
    public static final Block AUTO_CRAFTER = registerBlock(
        "auto_crafter",
        new AutoCrafterBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "auto_crafter"))).strength(3.5f).requiresTool())
    );
    
    // 自动收割机
    public static final Block AUTO_HARVESTER = registerBlock(
        "auto_harvester",
        new AutoHarvesterBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "auto_harvester"))).strength(3.5f).requiresTool())
    );
    
    // 自动分配器
    public static final Block AUTO_DISTRIBUTOR = registerBlock(
        "auto_distributor",
        new AutoDistributorBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "auto_distributor"))).strength(3.5f).requiresTool())
    );
    
    // 系统控制器
    public static final Block SYSTEM_CONTROLLER = registerBlock(
        "system_controller",
        new SystemControllerBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "system_controller"))).strength(3.5f).requiresTool())
    );
    
    /**
     * 注册方块和对应的 BlockItem
     */
    private static Block registerBlock(String name, Block block) {
        Identifier id = Identifier.of("factorcraft", name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        // 注册方块
        Block registeredBlock = Registry.register(Registries.BLOCK, blockKey, block);
        
        // 注册 BlockItem
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, itemKey, new BlockItem(registeredBlock, new Item.Settings().registryKey(itemKey)));
        
        return registeredBlock;
    }
    
    /**
     * 初始化所有方块注册
     */
    public static void init() {
        // 类加载时自动注册
    }
}
