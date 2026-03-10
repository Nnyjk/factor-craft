package com.factorcraft.module.technology.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * Factor 合成配方
 */
public class FactorCraftingRecipe implements Recipe<Inventory> {
    
    private final Identifier id;
    private final String group;
    private final ItemStack input;
    private final ItemStack output;
    private final double factorCost;
    
    public FactorCraftingRecipe(Identifier id, String group, ItemStack input, ItemStack output, double factorCost) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.factorCost = factorCost;
    }
    
    @Override
    public boolean matches(Inventory inv, World world) {
        // TODO: 检查输入是否匹配
        return true;
    }
    
    @Override
    public ItemStack craft(Inventory inv, net.minecraft.registry.RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }
    
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResult(net.minecraft.registry.RegistryWrapper.WrapperLookup registries) {
        return output.copy();
    }
    
    @Override
    public Identifier getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return null; // TODO: 实现
    }
    
    @Override
    public RecipeType<?> getType() {
        return null; // TODO: 实现
    }
    
    public double getFactorCost() {
        return factorCost;
    }
}
