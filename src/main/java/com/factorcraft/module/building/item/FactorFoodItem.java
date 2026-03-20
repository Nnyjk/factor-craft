package com.factorcraft.module.building.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Factor主题食物基类
 * 
 * 简化版本：仅提供基础营养值
 * 状态效果通过 ConsumableComponent 或其他机制实现
 */
public class FactorFoodItem extends Item {
    
    private final int tier;
    private final String tierName;
    
    public FactorFoodItem(Identifier id, int tier, String tierName, 
                          int nutrition, float saturation) {
        super(createSettings(id, nutrition, saturation));
        this.tier = tier;
        this.tierName = tierName;
    }
    
    private static Settings createSettings(Identifier id, int nutrition, float saturation) {
        FoodComponent food = new FoodComponent(nutrition, saturation, false);
        
        return new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))
            .food(food);
    }
    
    public int getTier() {
        return tier;
    }
    
    public String getTierName() {
        return tierName;
    }
}