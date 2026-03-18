package com.factorcraft.module.technology.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 科技模块物品注册 - Fabric 1.21.4
 * 
 * 命名规范: factor_item_{name}_{tier}
 */
public class ModItems {
    
    private static final String MOD_ID = "factorcraft";
    
    // ========== 特性水晶 ==========
    
    public static final Item CRYSTAL_SHARP = register("factor_item_crystal_sharp");
    public static final Item CRYSTAL_STURDY = register("factor_item_crystal_sturdy");
    public static final Item CRYSTAL_PROTECTIVE = register("factor_item_crystal_protective");
    public static final Item CRYSTAL_ENERGETIC = register("factor_item_crystal_energetic");
    public static final Item CRYSTAL_CATALYTIC = register("factor_item_crystal_catalytic");
    
    // ========== 线圈 (T1-T5) ==========
    
    public static final Item COIL_T1 = register("factor_item_coil_t1");
    public static final Item COIL_T2 = register("factor_item_coil_t2");
    public static final Item COIL_T3 = register("factor_item_coil_t3");
    public static final Item COIL_T4 = register("factor_item_coil_t4");
    public static final Item COIL_T5 = register("factor_item_coil_t5");
    
    // ========== 电路 ==========
    
    public static final Item CIRCUIT_BASIC = register("factor_item_circuit_basic");
    public static final Item CIRCUIT_ADVANCED = register("factor_item_circuit_advanced");
    public static final Item CIRCUIT_ELITE = register("factor_item_circuit_elite");
    
    // ========== Factor 电池 ==========
    
    public static final Item BATTERY_T1 = registerBattery(
        "factor_item_battery_t1",
        FactorBatteryItem.BatteryTier.T1
    );
    public static final Item BATTERY_T2 = registerBattery(
        "factor_item_battery_t2",
        FactorBatteryItem.BatteryTier.T2
    );
    public static final Item BATTERY_T3 = registerBattery(
        "factor_item_battery_t3",
        FactorBatteryItem.BatteryTier.T3
    );
    public static final Item BATTERY_T4 = registerBattery(
        "factor_item_battery_t4",
        FactorBatteryItem.BatteryTier.T4
    );
    public static final Item BATTERY_T5 = registerBattery(
        "factor_item_battery_t5",
        FactorBatteryItem.BatteryTier.T5
    );
    
    /**
     * 注册物品（基础版本）
     */
    private static Item register(String name) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        
        Item item = new Item(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, id, item);
    }
    
    /**
     * 注册电池物品
     */
    private static Item registerBattery(String name, FactorBatteryItem.BatteryTier tier) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        
        Item item = new FactorBatteryItem(tier, new Item.Settings().registryKey(key).maxCount(1));
        return Registry.register(Registries.ITEM, id, item);
    }
    
    /**
     * 注册物品（自定义 Item 子类）- 需要传入 ItemFactory
     */
    @FunctionalInterface
    public interface ItemFactory {
        Item create(Item.Settings settings);
    }
    
    private static Item register(String name, ItemFactory factory) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        
        Item item = factory.create(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, id, item);
    }
    
    public static void register() {
        // 静态初始化时已完成注册
    }
}