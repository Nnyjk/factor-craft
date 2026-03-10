package com.factorcraft.module.combat;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;

/**
 * Factor Craft 自定义 ToolMaterial 包装器
 * 
 * Minecraft 1.21.4 ToolMaterial 是 record，不能直接实现
 * 使用 ToolMaterials 的预定义实例
 */
public class ModToolMaterial {
    
    // T1-T5 材料映射到原版 ToolMaterial
    public static final ToolMaterial T1_STONE = ToolMaterial.STONE;
    public static final ToolMaterial T2_IRON = ToolMaterial.IRON;
    public static final ToolMaterial T3_GOLD = ToolMaterial.GOLD;
    public static final ToolMaterial T4_NETHERITE = ToolMaterial.NETHERITE;
    
    // T5 使用 NETHERITE 作为基础（无法创建自定义 ToolMaterial record）
    public static final ToolMaterial T5_FACTOR = ToolMaterial.NETHERITE;
    
    /**
     * 获取 T1-T5 武器耐久度 (自定义覆盖)
     */
    public static int getDurability(int tier) {
        return switch (tier) {
            case 1 -> 1500;
            case 2 -> 2000;
            case 3 -> 2500;
            case 4 -> 3000;
            case 5 -> 3500;
            default -> 100;
        };
    }
    
    /**
     * 获取 T1-T5 武器附魔能力 (自定义覆盖)
     */
    public static int getEnchantability(int tier) {
        return switch (tier) {
            case 1 -> 10;
            case 2 -> 12;
            case 3 -> 14;
            case 4 -> 16;
            case 5 -> 18;
            default -> 10;
        };
    }
}
