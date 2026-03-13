package com.factorcraft.module.technology.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 科技模块物品注册 - Fabric 1.21.4
 */
public class ModItems {
    
    private static final String MOD_ID = "factorcraft";
    
    // 特性水晶
    public static final Item SHARP_CRYSTAL = register("sharp_crystal");
    public static final Item STURDY_CRYSTAL = register("sturdy_crystal");
    public static final Item PROTECTIVE_CRYSTAL = register("protective_crystal");
    public static final Item ENERGETIC_CRYSTAL = register("energetic_crystal");
    public static final Item CATALYTIC_CRYSTAL = register("catalytic_crystal");
    
    // 升级组件
    public static final Item EXTRACTION_COIL_T1 = register("extraction_coil_t1");
    public static final Item EXTRACTION_COIL_T2 = register("extraction_coil_t2");
    public static final Item EXTRACTION_COIL_T3 = register("extraction_coil_t3");
    public static final Item EXTRACTION_COIL_T4 = register("extraction_coil_t4");
    public static final Item EXTRACTION_COIL_T5 = register("extraction_coil_t5");
    
    // 电路
    public static final Item BASIC_CIRCUIT = register("basic_circuit");
    public static final Item ADVANCED_CIRCUIT = register("advanced_circuit");
    public static final Item ELITE_CIRCUIT = register("elite_circuit");
    
    /**
     * 注册物品（使用 RegistryKey 模式，符合 Fabric 1.21.4 要求）
     */
    private static Item register(String name) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        
        Item item = new Item(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, id, item);
    }
    
    public static void register() {
        // 静态初始化时已完成注册
    }
}