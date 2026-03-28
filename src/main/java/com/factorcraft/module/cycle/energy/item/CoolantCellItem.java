package com.factorcraft.module.cycle.energy.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 冷却剂单元
 * 
 * 用于 Factor 反应堆冷却，防止过热爆炸
 * 每个冷却单元提供 10,000 点冷却值
 */
public class CoolantCellItem extends Item {
    
    public static final RegistryKey<Item> KEY = RegistryKey.of(
        RegistryKeys.ITEM,
        Identifier.of("factorcraft", "coolant_cell")
    );
    
    public static final int COOLING_CAPACITY = 10000;
    
    public CoolantCellItem() {
        super(new Item.Settings().registryKey(KEY).maxCount(64));
    }
    
    /**
     * 获取剩余冷却值
     */
    public int getRemainingCooling() {
        // TODO: 使用 Data Components 存储冷却值
        return COOLING_CAPACITY;
    }
}
