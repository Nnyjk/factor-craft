package com.factorcraft.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    
    // Factor 晶体
    public static final Item FACTOR_CRYSTAL = new Item(
        new Item.Settings()
            .maxCount(64)
    );
    
    // 特性催化剂
    public static final Item TRAIT_CATALYST = new Item(
        new Item.Settings()
            .maxCount(16)
    );
    
    // 共振碎片
    public static final Item RESONANCE_SHARD = new Item(
        new Item.Settings()
            .maxCount(32)
    );
    
    // 纯净 Factor
    public static final Item PURE_FACTOR = new Item(
        new Item.Settings()
            .maxCount(1)
            .maxDamage(100)
    );
    
    // 特性提取器
    public static final Item TRAIT_EXTRACTOR = new Item(
        new Item.Settings()
            .maxCount(1)
            .maxDamage(50)
    );
    
    public static void register() {
        registerItem("factor_crystal", FACTOR_CRYSTAL);
        registerItem("trait_catalyst", TRAIT_CATALYST);
        registerItem("resonance_shard", RESONANCE_SHARD);
        registerItem("pure_factor", PURE_FACTOR);
        registerItem("trait_extractor", TRAIT_EXTRACTOR);
    }
    
    private static void registerItem(String name, Item item) {
        Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), item);
    }
}