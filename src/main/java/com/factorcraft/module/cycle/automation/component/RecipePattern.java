package com.factorcraft.module.cycle.automation.component;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

/**
 * 配方样板 - 用于保存自动合成器的配方
 */
public class RecipePattern {
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;
    private final String id;
    
    public RecipePattern(String id, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
    }
    
    public String getId() {
        return id;
    }
    
    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public ItemStack getOutput() {
        return output;
    }
    
    /**
     * 将配方写入 NBT
     */
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup lookup) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id);
        
        NbtList ingredientsNbt = new NbtList();
        for (Ingredient ingredient : ingredients) {
            NbtCompound ingredientNbt = new NbtCompound();
            // 简化存储：只存储物品 ID
            ingredientNbt.putString("item", "minecraft:air"); // 实际需要序列化 ingredient
            ingredientsNbt.add(ingredientNbt);
        }
        nbt.put("ingredients", ingredientsNbt);
        
        nbt.put("output", output.encode(lookup));
        return nbt;
    }
    
    /**
     * 从 NBT 读取配方
     */
    public static RecipePattern fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        String id = nbt.getString("id");
        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        ItemStack output = ItemStack.fromNbtOrEmpty(lookup, nbt.getCompound("output"));
        return new RecipePattern(id, ingredients, output);
    }
    
    /**
     * 检查配方是否匹配
     */
    public boolean matches(DefaultedList<ItemStack> input) {
        if (input.size() != ingredients.size()) {
            return false;
        }
        for (int i = 0; i < ingredients.size(); i++) {
            if (!ingredients.get(i).test(input.get(i))) {
                return false;
            }
        }
        return true;
    }
}
