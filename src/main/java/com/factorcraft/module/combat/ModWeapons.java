package com.factorcraft.module.combat;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * T4-T5 武器扩展 - Fabric 1.21.4
 * 使用简单 Item 而非 SwordItem，避免 ToolMaterial 依赖
 */
public class ModWeapons {
    
    // T4 武器 (下界合金级)
    public static final Item T4_SWORD = registerItem(
        "t4_sword",
        new Item(new Item.Settings().maxCount(1).maxDamage(2031))
    );
    
    public static final Item T4_HAMMER = registerItem(
        "t4_hammer",
        new Item(new Item.Settings().maxCount(1).maxDamage(2031))
    );
    
    // T5 武器 (Factor 晶体级)
    public static final Item T5_SWORD = registerItem(
        "t5_sword",
        new Item(new Item.Settings().maxCount(1).maxDamage(4096))
    );
    
    public static final Item T5_HAMMER = registerItem(
        "t5_hammer",
        new Item(new Item.Settings().maxCount(1).maxDamage(4096))
    );
    
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("factorcraft", name), item);
    }
    
    public static void register() {
        // 静态初始化时已注册
    }
}
