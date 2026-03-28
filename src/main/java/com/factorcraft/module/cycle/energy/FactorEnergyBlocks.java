package com.factorcraft.module.cycle.energy;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.energy.block.FactorCompressorBlock;
import com.factorcraft.module.cycle.energy.block.FactorCrystalBlock;
import com.factorcraft.module.cycle.energy.block.FactorPumpBlock;
import com.factorcraft.module.cycle.energy.block.FactorReactorBlock;
import com.factorcraft.module.cycle.energy.block.FactorStabilizerBlock;
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
    public static final RegistryKey<Block> FACTOR_COMPRESSOR_KEY = createKey("factor_compressor");
    public static final RegistryKey<Block> FACTOR_REACTOR_KEY = createKey("factor_reactor");
    public static final RegistryKey<Block> FACTOR_STABILIZER_KEY = createKey("factor_stabilizer");
    
    // Blocks
    private static FactorCrystalBlock factorCrystalBlock;
    private static FactorPumpBlock factorPumpBlock;
    public static Block FACTOR_COMPRESSOR;
    public static Block FACTOR_REACTOR;
    public static Block FACTOR_STABILIZER;
    
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
        
        FACTOR_COMPRESSOR = register(
            FACTOR_COMPRESSOR_KEY,
            new FactorCompressorBlock(AbstractBlock.Settings.create()
                .registryKey(FACTOR_COMPRESSOR_KEY)
                .strength(3.0f, 6.0f)
                .nonOpaque())
        );
        
        FACTOR_REACTOR = register(
            FACTOR_REACTOR_KEY,
            new FactorReactorBlock(AbstractBlock.Settings.create()
                .registryKey(FACTOR_REACTOR_KEY)
                .strength(4.0f, 8.0f)
                .nonOpaque())
        );
        FactorReactorBlock.FACTOR_REACTOR = FACTOR_REACTOR;
        
        FACTOR_STABILIZER = register(
            FACTOR_STABILIZER_KEY,
            new FactorStabilizerBlock(AbstractBlock.Settings.create()
                .registryKey(FACTOR_STABILIZER_KEY)
                .strength(3.5f, 7.0f)
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
    
    public static Block getFactorReactor() {
        return FACTOR_REACTOR;
    }
    
    public static Block getFactorStabilizer() {
        return FACTOR_STABILIZER;
    }
}
