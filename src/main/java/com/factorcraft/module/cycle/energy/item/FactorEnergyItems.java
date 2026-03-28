package com.factorcraft.module.cycle.energy.item;

import com.factorcraft.FactorCraftMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * Factor 能源模块物品注册
 */
public class FactorEnergyItems {
    
    public static Item DENSE_FACTOR;
    
    /**
     * 初始化并注册所有物品
     */
    public static void init() {
        DENSE_FACTOR = Registry.register(
            Registries.ITEM,
            DenseFactor.DENSE_FACTOR_KEY.getValue(),
            new DenseFactor(new Item.Settings()
                .registryKey(DenseFactor.DENSE_FACTOR_KEY)
                .maxCount(64))
        );
        
        FactorCraftMod.LOGGER.info("Factor Energy Items registered");
    }
}
