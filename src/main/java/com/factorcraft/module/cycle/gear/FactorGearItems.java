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
    
    // Registry Keys - 量子工具
    public static final RegistryKey<Item> QUANTUM_PICKAXE_KEY = createKey("quantum_pickaxe");
    public static final RegistryKey<Item> QUANTUM_AXE_KEY = createKey("quantum_axe");
    public static final RegistryKey<Item> QUANTUM_SHOVEL_KEY = createKey("quantum_shovel");
    public static final RegistryKey<Item> QUANTUM_HOE_KEY = createKey("quantum_hoe");
    public static final RegistryKey<Item> QUANTUM_SWORD_KEY = createKey("quantum_sword");
    
    // Registry Keys - Factor 盔甲
    public static final RegistryKey<Item> FACTOR_HELMET_KEY = createKey("factor_helmet");
    public static final RegistryKey<Item> FACTOR_CHESTPLATE_KEY = createKey("factor_chestplate");
    public static final RegistryKey<Item> FACTOR_LEGGINGS_KEY = createKey("factor_leggings");
    public static final RegistryKey<Item> FACTOR_BOOTS_KEY = createKey("factor_boots");
    
    // 量子工具
    public static final Item QUANTUM_PICKAXE = new QuantumPickaxeItem(QUANTUM_PICKAXE_KEY);
    public static final Item QUANTUM_AXE = new QuantumAxeItem(QUANTUM_AXE_KEY);
    public static final Item QUANTUM_SHOVEL = new QuantumShovelItem(QUANTUM_SHOVEL_KEY);
    public static final Item QUANTUM_HOE = new QuantumHoeItem(QUANTUM_HOE_KEY);
    public static final Item QUANTUM_SWORD = new QuantumSwordItem(QUANTUM_SWORD_KEY);
    
    // Factor 盔甲
    public static final Item FACTOR_HELMET = new FactorHelmetItem(FACTOR_HELMET_KEY);
    public static final Item FACTOR_CHESTPLATE = new FactorChestplateItem(FACTOR_CHESTPLATE_KEY);
    public static final Item FACTOR_LEGGINGS = new FactorLeggingsItem(FACTOR_LEGGINGS_KEY);
    public static final Item FACTOR_BOOTS = new FactorBootsItem(FACTOR_BOOTS_KEY);
    
    /**
     * 注册所有物品
     */
    public static void register() {
        Registry.register(Registries.ITEM, QUANTUM_PICKAXE_KEY, QUANTUM_PICKAXE);
        Registry.register(Registries.ITEM, QUANTUM_AXE_KEY, QUANTUM_AXE);
        Registry.register(Registries.ITEM, QUANTUM_SHOVEL_KEY, QUANTUM_SHOVEL);
        Registry.register(Registries.ITEM, QUANTUM_HOE_KEY, QUANTUM_HOE);
        Registry.register(Registries.ITEM, QUANTUM_SWORD_KEY, QUANTUM_SWORD);
        Registry.register(Registries.ITEM, FACTOR_HELMET_KEY, FACTOR_HELMET);
        Registry.register(Registries.ITEM, FACTOR_CHESTPLATE_KEY, FACTOR_CHESTPLATE);
        Registry.register(Registries.ITEM, FACTOR_LEGGINGS_KEY, FACTOR_LEGGINGS);
        Registry.register(Registries.ITEM, FACTOR_BOOTS_KEY, FACTOR_BOOTS);
    }
    
    private static RegistryKey<Item> createKey(String name) {
        return RegistryKey.of(Registries.ITEM.getKey(), Identifier.of("factorcraft", name));
    }
}
