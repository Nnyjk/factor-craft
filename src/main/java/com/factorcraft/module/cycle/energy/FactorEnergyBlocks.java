package com.factorcraft.module.cycle.energy;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.energy.block.FactorCrystalBlock;
import com.factorcraft.module.cycle.energy.block.FactorPumpBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Factor 能源模块方块注册
 * 
 * Fabric 1.21.4 最佳实践：
 * - 使用 RegistryKey 注册方块和物品
 * - 使用 Block.Settings.create() 创建方块设置
 * - 使用 .registryKey(key) 设置 registryKey
 */
public class FactorEnergyBlocks {
    
    // Registry Keys
    public static final RegistryKey<Block> FACTOR_CRYSTAL_KEY = createKey("factor_crystal");
    public static final RegistryKey<Block> FACTOR_PUMP_KEY = createKey("factor_pump");
    
    // Blocks
    private static FactorCrystalBlock factorCrystalBlock;
    private static FactorPumpBlock factorPumpBlock;
    
    /**
     * 创建 RegistryKey
     */
    private static RegistryKey<Block> createKey(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(FactorCraftMod.MOD_ID, name));
    }
    
    /**
     * 初始化并注册所有方块
     */
    public static void init() {
        factorCrystalBlock = register(
            FACTOR_CRYSTAL_KEY,
            new FactorCrystalBlock(AbstractBlock.Settings.create()
                .registryKey(FACTOR_CRYSTAL_KEY)
                .strength(3.0f, 6.0f)
                .nonOpaque()
                .luminance(state -> 8))
        );
        
        factorPumpBlock = register(
            FACTOR_PUMP_KEY,
            new FactorPumpBlock(AbstractBlock.Settings.create()
                .registryKey(FACTOR_PUMP_KEY)
                .strength(2.5f, 5.0f)
                .nonOpaque())
        );
    }
    
    /**
     * 注册方块
     */
    private static <T extends Block> T register(RegistryKey<Block> key, T block) {
        Registry.register(Registries.BLOCK, key, block);
        
        // 注册对应的 BlockItem
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, key.getValue());
        Registry.register(Registries.ITEM, itemKey, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        
        return block;
    }
    
    // Getters
    public static FactorCrystalBlock getFactorCrystal() {
        return factorCrystalBlock;
    }
    
    public static FactorPumpBlock getFactorPump() {
        return factorPumpBlock;
    }
}
