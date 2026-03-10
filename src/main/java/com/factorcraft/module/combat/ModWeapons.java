package com.factorcraft.module.combat;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;

/**
 * T4-T5 武器扩展
 */
public class ModWeapons {
    
    // T4 武器 (下界合金级)
    public static final ToolMaterial T4_MATERIAL = new ModToolMaterial(
        2031, 9.0f, 4.0f, 25, RegistryKey.of(net.minecraft.registry.RegistryKeys.ITEM, net.minecraft.util.Identifier.of("factorcraft:t4_ingot"))
    );
    public static final SwordItem T4_SWORD = new SwordItem(T4_MATERIAL, 4, -2.4f, new Item.Settings());
    public static final SwordItem T4_HAMMER = new SwordItem(T4_MATERIAL, 6, -3.0f, new Item.Settings());
    
    // T5 武器 (Factor 晶体级)
    public static final ToolMaterial T5_MATERIAL = new ModToolMaterial(
        4096, 12.0f, 5.0f, 30, RegistryKey.of(net.minecraft.registry.RegistryKeys.ITEM, net.minecraft.util.Identifier.of("factorcraft:t5_crystal"))
    );
    public static final SwordItem T5_SWORD = new SwordItem(T5_MATERIAL, 5, -2.4f, new Item.Settings());
    public static final SwordItem T5_HAMMER = new SwordItem(T5_MATERIAL, 8, -3.0f, new Item.Settings());
    
    public static void register() {
        // TODO: 注册物品
    }
}
