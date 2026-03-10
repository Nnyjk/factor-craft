package com.factorcraft.module.combat;

import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;

/**
 * T4-T5 工具材料数据类
 * 不实现 ToolMaterial 接口，仅作为数据容器
 */
public class ModToolMaterial {
    
    public final int durability;
    public final float miningSpeed;
    public final float attackDamage;
    public final int miningLevel;
    public final Ingredient repairIngredient;
    
    public ModToolMaterial(int durability, float miningSpeed, float attackDamage, int miningLevel, Ingredient repairIngredient) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.repairIngredient = repairIngredient;
    }
    
    // T4 材料 (下界合金级)
    public static final ModToolMaterial T4 = new ModToolMaterial(
        2031, 9.0f, 4.0f, 4, Ingredient.ofItems(Items.NETHERITE_INGOT)
    );
    
    // T5 材料 (Factor 晶体级)
    public static final ModToolMaterial T5 = new ModToolMaterial(
        4096, 12.0f, 5.0f, 4, Ingredient.ofItems(Items.NETHERITE_INGOT)
    );
}
