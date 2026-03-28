package com.factorcraft.module.cycle.energy;

import com.factorcraft.module.cycle.energy.item.CoolantCellItem;
import com.factorcraft.module.cycle.energy.item.DenseFactor;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Factor 能源模块物品注册
 */
public class FactorEnergyItems {
    
    // Registry Keys
    public static final RegistryKey<Item> DENSE_FACTOR_KEY = createKey("dense_factor");
    public static final RegistryKey<Item> COOLANT_CELL_KEY = createKey("coolant_cell");
    
    // Items
    private static DenseFactor denseFactor;
    private static CoolantCellItem coolantCell;
    
    /**
     * 初始化并注册所有物品
     */
    public static void init() {
        denseFactor = register(DENSE_FACTOR_KEY, new DenseFactor(new Item.Settings().maxCount(64)));
        coolantCell = register(COOLANT_CELL_KEY, new CoolantCellItem());
    }
    
    /**
     * 创建 RegistryKey
     */
    private static RegistryKey<Item> createKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of("factorcraft", name));
    }
    
    /**
     * 注册物品
     */
    private static <T extends Item> T register(RegistryKey<Item> key, T item) {
        return Registry.register(Registries.ITEM, key, item);
    }
    
    // Getters
    public static DenseFactor getDenseFactor() {
        return denseFactor;
    }
    
    public static CoolantCellItem getCoolantCell() {
        return coolantCell;
    }
}
