package com.factorcraft.module.loot;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 共振核心 - 稀有掉落物
 */
public class ResonanceCoreItem extends Item {
    
    public ResonanceCoreItem() {
        super(new Settings().maxCount(16));
    }
    
    /**
     * 注册物品
     */
    public static void register() {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", "resonance_core"), new ResonanceCoreItem());
    }
}
