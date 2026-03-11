package com.factorcraft.module.technology.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 科技模块物品注册
 */
public class ModItems {
    
    // 特性物品
    public static final Item SHARP_CRYSTAL = registerItem(
        "sharp_crystal",
        new Item(new Item.Settings())
    );
    public static final Item STURDY_CRYSTAL = registerItem(
        "sturdy_crystal",
        new Item(new Item.Settings())
    );
    public static final Item PROTECTIVE_CRYSTAL = registerItem(
        "protective_crystal",
        new Item(new Item.Settings())
    );
    public static final Item ENERGETIC_CRYSTAL = registerItem(
        "energetic_crystal",
        new Item(new Item.Settings())
    );
    public static final Item CATALYTIC_CRYSTAL = registerItem(
        "catalytic_crystal",
        new Item(new Item.Settings())
    );
    
    // 升级组件
    public static final Item EXTRACTION_COIL_T1 = registerItem(
        "extraction_coil_t1",
        new Item(new Item.Settings())
    );
    public static final Item EXTRACTION_COIL_T2 = registerItem(
        "extraction_coil_t2",
        new Item(new Item.Settings())
    );
    public static final Item EXTRACTION_COIL_T3 = registerItem(
        "extraction_coil_t3",
        new Item(new Item.Settings())
    );
    public static final Item EXTRACTION_COIL_T4 = registerItem(
        "extraction_coil_t4",
        new Item(new Item.Settings())
    );
    public static final Item EXTRACTION_COIL_T5 = registerItem(
        "extraction_coil_t5",
        new Item(new Item.Settings())
    );
    
    // 电路
    public static final Item BASIC_CIRCUIT = registerItem(
        "basic_circuit",
        new Item(new Item.Settings())
    );
    public static final Item ADVANCED_CIRCUIT = registerItem(
        "advanced_circuit",
        new Item(new Item.Settings())
    );
    public static final Item ELITE_CIRCUIT = registerItem(
        "elite_circuit",
        new Item(new Item.Settings())
    );
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), item);
    }
    
    public static void register() {
        // 静态初始化时已注册
    }
}
