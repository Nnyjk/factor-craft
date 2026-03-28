package com.factorcraft.module.cycle.gear;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * Factor 装备物品注册表
 * 
 * 注册所有 R2.2 终极装备物品
 */
public class FactorGearItems {
    
    // 量子工具
    public static final Item QUANTUM_PICKAXE = new QuantumPickaxeItem();
    public static final Item QUANTUM_AXE = new QuantumAxeItem();
    public static final Item QUANTUM_SHOVEL = new QuantumShovelItem();
    public static final Item QUANTUM_HOE = new QuantumHoeItem();
    public static final Item QUANTUM_SWORD = new QuantumSwordItem();
    
    // Factor 盔甲
    public static final Item FACTOR_HELMET = new FactorHelmetItem();
    public static final Item FACTOR_CHESTPLATE = new FactorChestplateItem();
    public static final Item FACTOR_LEGGINGS = new FactorLeggingsItem();
    public static final Item FACTOR_BOOTS = new FactorBootsItem();
    
    /**
     * 注册所有物品
     */
    public static void register() {
        // 量子工具
        register("quantum_pickaxe", QUANTUM_PICKAXE);
        register("quantum_axe", QUANTUM_AXE);
        register("quantum_shovel", QUANTUM_SHOVEL);
        register("quantum_hoe", QUANTUM_HOE);
        register("quantum_sword", QUANTUM_SWORD);
        
        // Factor 盔甲
        register("factor_helmet", FACTOR_HELMET);
        register("factor_chestplate", FACTOR_CHESTPLATE);
        register("factor_leggings", FACTOR_LEGGINGS);
        register("factor_boots", FACTOR_BOOTS);
    }
    
    /**
     * 注册单个物品
     */
    private static void register(String name, Item item) {
        RegistryKey<Item> key = RegistryKey.of(Registries.ITEM.getKey(), Identifier.of("factorcraft", name));
        Registry.register(Registries.ITEM, key, item);
    }
}
