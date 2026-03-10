package com.factorcraft.module.combat;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModToolMaterial implements ToolMaterial {
    
    private final int durability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int miningLevel;
    private final RegistryKey<Item> repairIngredientKey;
    
    public ModToolMaterial(int durability, float miningSpeed, float attackDamage, int miningLevel, RegistryKey<Item> repairIngredientKey) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.miningLevel = miningLevel;
        this.repairIngredientKey = repairIngredientKey;
    }
    
    @Override
    public int getDurability() {
        return durability;
    }
    
    @Override
    public float getMiningSpeedMultiplier() {
        return miningSpeed;
    }
    
    @Override
    public float getAttackDamage() {
        return attackDamage;
    }
    
    @Override
    public int getMiningLevel() {
        return miningLevel;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItem(repairIngredientKey.getValue());
    }
}
