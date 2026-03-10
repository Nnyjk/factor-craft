package com.factorcraft.module.technology.item;

import net.minecraft.item.Item;

/**
 * 科技模块物品注册
 */
public class ModItems {
    
    // 特性物品
    public static final Item SHARP_CRYSTAL = new Item(new Item.Settings());
    public static final Item STURDY_CRYSTAL = new Item(new Item.Settings());
    public static final Item PROTECTIVE_CRYSTAL = new Item(new Item.Settings());
    public static final Item ENERGETIC_CRYSTAL = new Item(new Item.Settings());
    public static final Item CATALYTIC_CRYSTAL = new Item(new Item.Settings());
    
    // 升级组件
    public static final Item EXTRACTION_COIL_T1 = new Item(new Item.Settings());
    public static final Item EXTRACTION_COIL_T2 = new Item(new Item.Settings());
    public static final Item EXTRACTION_COIL_T3 = new Item(new Item.Settings());
    public static final Item EXTRACTION_COIL_T4 = new Item(new Item.Settings());
    public static final Item EXTRACTION_COIL_T5 = new Item(new Item.Settings());
    
    // 电路
    public static final Item BASIC_CIRCUIT = new Item(new Item.Settings());
    public static final Item ADVANCED_CIRCUIT = new Item(new Item.Settings());
    public static final Item ELITE_CIRCUIT = new Item(new Item.Settings());
    
    public static void register() {
        // TODO: 注册物品
    }
}
