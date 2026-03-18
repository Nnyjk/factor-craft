package com.factorcraft.module.gear;

import com.factorcraft.FactorCraftMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Factor 装备注册
 * 
 * 注册所有 Factor 工具和护甲
 * 
 * 命名规范:
 * - factor_{tool}_t{tier} (如 factor_pickaxe_t1)
 * - factor_{armor}_t{tier} (如 factor_chestplate_t3)
 */
public class GearRegistry {
    
    private static final String MOD_ID = FactorCraftMod.MOD_ID;
    
    // ========== 工具 ==========
    
    // T1 工具 (铜级)
    public static final Item FACTOR_PICKAXE_T1 = registerTool("factor_pickaxe_t1", 
        ToolMaterial.STONE, 1);
    public static final Item FACTOR_AXE_T1 = registerAxe("factor_axe_t1", 
        ToolMaterial.STONE, 1);
    public static final Item FACTOR_SHOVEL_T1 = registerShovel("factor_shovel_t1", 
        ToolMaterial.STONE, 1);
    public static final Item FACTOR_SWORD_T1 = registerSword("factor_sword_t1", 
        ToolMaterial.STONE, 1);
    
    // T2 工具 (铁级)
    public static final Item FACTOR_PICKAXE_T2 = registerTool("factor_pickaxe_t2", 
        ToolMaterial.IRON, 2);
    public static final Item FACTOR_AXE_T2 = registerAxe("factor_axe_t2", 
        ToolMaterial.IRON, 2);
    public static final Item FACTOR_SHOVEL_T2 = registerShovel("factor_shovel_t2", 
        ToolMaterial.IRON, 2);
    public static final Item FACTOR_SWORD_T2 = registerSword("factor_sword_t2", 
        ToolMaterial.IRON, 2);
    
    // T3 工具 (金级)
    public static final Item FACTOR_PICKAXE_T3 = registerTool("factor_pickaxe_t3", 
        ToolMaterial.GOLD, 3);
    public static final Item FACTOR_AXE_T3 = registerAxe("factor_axe_t3", 
        ToolMaterial.GOLD, 3);
    public static final Item FACTOR_SHOVEL_T3 = registerShovel("factor_shovel_t3", 
        ToolMaterial.GOLD, 3);
    public static final Item FACTOR_SWORD_T3 = registerSword("factor_sword_t3", 
        ToolMaterial.GOLD, 3);
    
    // T4 工具 (钻石级)
    public static final Item FACTOR_PICKAXE_T4 = registerTool("factor_pickaxe_t4", 
        ToolMaterial.DIAMOND, 4);
    public static final Item FACTOR_AXE_T4 = registerAxe("factor_axe_t4", 
        ToolMaterial.DIAMOND, 4);
    public static final Item FACTOR_SHOVEL_T4 = registerShovel("factor_shovel_t4", 
        ToolMaterial.DIAMOND, 4);
    public static final Item FACTOR_SWORD_T4 = registerSword("factor_sword_t4", 
        ToolMaterial.DIAMOND, 4);
    
    // T5 工具 (下界合金级)
    public static final Item FACTOR_PICKAXE_T5 = registerTool("factor_pickaxe_t5", 
        ToolMaterial.NETHERITE, 5);
    public static final Item FACTOR_AXE_T5 = registerAxe("factor_axe_t5", 
        ToolMaterial.NETHERITE, 5);
    public static final Item FACTOR_SHOVEL_T5 = registerShovel("factor_shovel_t5", 
        ToolMaterial.NETHERITE, 5);
    public static final Item FACTOR_SWORD_T5 = registerSword("factor_sword_t5", 
        ToolMaterial.NETHERITE, 5);
    
    // ========== 护甲 ==========
    
    // T1 护甲
    public static final Item FACTOR_HELMET_T1 = registerArmor("factor_helmet_t1", 
        ArmorMaterials.CHAIN, EquipmentType.HELMET, 1);
    public static final Item FACTOR_CHESTPLATE_T1 = registerArmor("factor_chestplate_t1", 
        ArmorMaterials.CHAIN, EquipmentType.CHESTPLATE, 1);
    public static final Item FACTOR_LEGGINGS_T1 = registerArmor("factor_leggings_t1", 
        ArmorMaterials.CHAIN, EquipmentType.LEGGINGS, 1);
    public static final Item FACTOR_BOOTS_T1 = registerArmor("factor_boots_t1", 
        ArmorMaterials.CHAIN, EquipmentType.BOOTS, 1);
    
    // T2 护甲
    public static final Item FACTOR_HELMET_T2 = registerArmor("factor_helmet_t2", 
        ArmorMaterials.IRON, EquipmentType.HELMET, 2);
    public static final Item FACTOR_CHESTPLATE_T2 = registerArmor("factor_chestplate_t2", 
        ArmorMaterials.IRON, EquipmentType.CHESTPLATE, 2);
    public static final Item FACTOR_LEGGINGS_T2 = registerArmor("factor_leggings_t2", 
        ArmorMaterials.IRON, EquipmentType.LEGGINGS, 2);
    public static final Item FACTOR_BOOTS_T2 = registerArmor("factor_boots_t2", 
        ArmorMaterials.IRON, EquipmentType.BOOTS, 2);
    
    // T3 护甲
    public static final Item FACTOR_HELMET_T3 = registerArmor("factor_helmet_t3", 
        ArmorMaterials.GOLD, EquipmentType.HELMET, 3);
    public static final Item FACTOR_CHESTPLATE_T3 = registerArmor("factor_chestplate_t3", 
        ArmorMaterials.GOLD, EquipmentType.CHESTPLATE, 3);
    public static final Item FACTOR_LEGGINGS_T3 = registerArmor("factor_leggings_t3", 
        ArmorMaterials.GOLD, EquipmentType.LEGGINGS, 3);
    public static final Item FACTOR_BOOTS_T3 = registerArmor("factor_boots_t3", 
        ArmorMaterials.GOLD, EquipmentType.BOOTS, 3);
    
    // T4 护甲
    public static final Item FACTOR_HELMET_T4 = registerArmor("factor_helmet_t4", 
        ArmorMaterials.DIAMOND, EquipmentType.HELMET, 4);
    public static final Item FACTOR_CHESTPLATE_T4 = registerArmor("factor_chestplate_t4", 
        ArmorMaterials.DIAMOND, EquipmentType.CHESTPLATE, 4);
    public static final Item FACTOR_LEGGINGS_T4 = registerArmor("factor_leggings_t4", 
        ArmorMaterials.DIAMOND, EquipmentType.LEGGINGS, 4);
    public static final Item FACTOR_BOOTS_T4 = registerArmor("factor_boots_t4", 
        ArmorMaterials.DIAMOND, EquipmentType.BOOTS, 4);
    
    // T5 护甲
    public static final Item FACTOR_HELMET_T5 = registerArmor("factor_helmet_t5", 
        ArmorMaterials.NETHERITE, EquipmentType.HELMET, 5);
    public static final Item FACTOR_CHESTPLATE_T5 = registerArmor("factor_chestplate_t5", 
        ArmorMaterials.NETHERITE, EquipmentType.CHESTPLATE, 5);
    public static final Item FACTOR_LEGGINGS_T5 = registerArmor("factor_leggings_t5", 
        ArmorMaterials.NETHERITE, EquipmentType.LEGGINGS, 5);
    public static final Item FACTOR_BOOTS_T5 = registerArmor("factor_boots_t5", 
        ArmorMaterials.NETHERITE, EquipmentType.BOOTS, 5);
    
    // ========== 注册辅助方法 ==========
    
    private static Item registerTool(String name, ToolMaterial material, int tier) {
        return register(name, settings -> new FactorPickaxeItem(material, tier, settings));
    }
    
    private static Item registerAxe(String name, ToolMaterial material, int tier) {
        return register(name, settings -> new FactorAxeItem(material, tier, settings));
    }
    
    private static Item registerShovel(String name, ToolMaterial material, int tier) {
        return register(name, settings -> new FactorShovelItem(material, tier, settings));
    }
    
    private static Item registerSword(String name, ToolMaterial material, int tier) {
        return register(name, settings -> new FactorSwordItem(material, tier, settings));
    }
    
    private static Item registerArmor(String name, ArmorMaterial material, EquipmentType type, int tier) {
        return register(name, settings -> new FactorArmorItem(material, type, tier, settings));
    }
    
    private interface ItemFactory {
        Item create(Item.Settings settings);
    }
    
    private static Item register(String name, ItemFactory factory) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        
        Item item = factory.create(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, id, item);
    }
    
    /**
     * 注册所有装备
     */
    public static void register() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Gear] 已注册 {} 个工具 + {} 个护甲", 
            20, 20); // 5工具×5等级 = 25, 4护甲×5等级 = 20
    }
}