package com.factorcraft.module.cycle.energy.item;

import com.factorcraft.FactorCraftMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 高密度 Factor 物品
 * 
 * 通过 Factor 压缩机将 1000mB 普通 Factor 压缩为 10mB 高密度 Factor
 * 用于高级合成和 TΩ 级制造
 */
public class DenseFactor extends Item {
    
    public static final RegistryKey<Item> DENSE_FACTOR_KEY = RegistryKey.of(
        RegistryKeys.ITEM,
        Identifier.of(FactorCraftMod.MOD_ID, "dense_factor")
    );
    
    public DenseFactor(Settings settings) {
        super(settings.registryKey(DENSE_FACTOR_KEY));
    }
}
