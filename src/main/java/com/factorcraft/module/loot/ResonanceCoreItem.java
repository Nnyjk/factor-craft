package com.factorcraft.module.loot;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 共振核心 - 稀有掉落物
 */
public class ResonanceCoreItem extends Item {
    
    public ResonanceCoreItem() {
        super(new Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("factorcraft", "resonance_core")))
            .maxCount(16));
    }
    
    /**
     * 注册物品
     */
    public static void register() {
        Identifier id = Identifier.of("factorcraft", "resonance_core");
        Registry.register(Registries.ITEM, id, new ResonanceCoreItem());
    }
}
