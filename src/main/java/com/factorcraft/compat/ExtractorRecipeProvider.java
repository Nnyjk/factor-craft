package com.factorcraft.compat;

import com.factorcraft.FactorCraftMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 提取器配方提供者
 */
public class ExtractorRecipeProvider implements RecipeProvider {
    
    private static final Identifier TYPE_ID = Identifier.of(FactorCraftMod.MOD_ID, "extractor");
    private static final String DISPLAY_NAME_KEY = "factorcraft.rei.category.extractor";
    
    private final List<RecipeInfo> recipes = new ArrayList<>();
    
    public ExtractorRecipeProvider() {
        loadRecipes();
    }
    
    private void loadRecipes() {
        try (InputStream stream = RecipeProvider.class.getClassLoader()
                .getResourceAsStream("config/recipe_display.json")) {
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();
                    JsonObject extractorRecipes = config.getAsJsonObject("extractor_recipes");
                    
                    for (String key : extractorRecipes.keySet()) {
                        JsonObject recipe = extractorRecipes.getAsJsonObject(key);
                        recipes.add(parseRecipe(key, recipe));
                    }
                    
                    FactorCraftMod.LOGGER.info("[RecipeProvider] 加载 {} 个提取器配方", recipes.size());
                }
            }
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[RecipeProvider] 无法加载提取器配方", e);
        }
    }
    
    private RecipeInfo parseRecipe(String id, JsonObject recipe) {
        return new RecipeInfo(
            id,
            recipe.get("tier").getAsInt(),
            parseItemStacks(recipe.getAsJsonArray("inputs")),
            parseItemStacks(recipe.getAsJsonArray("outputs")),
            recipe.get("processing_time").getAsInt(),
            recipe.get("factor_cost").getAsLong(),
            recipe.get("factor_output").getAsLong()
        );
    }
    
    private List<ItemStack> parseItemStacks(com.google.gson.JsonArray arr) {
        List<ItemStack> stacks = new ArrayList<>();
        for (var element : arr) {
            String itemId = element.getAsString();
            // 实际运行时从注册表获取物品
            // stacks.add(new ItemStack(Registries.ITEM.get(Identifier.of(itemId))));
        }
        return stacks;
    }
    
    @Override
    public Identifier getRecipeTypeId() {
        return TYPE_ID;
    }
    
    @Override
    public String getDisplayNameKey() {
        return DISPLAY_NAME_KEY;
    }
    
    @Override
    public List<RecipeInfo> getRecipes() {
        return recipes;
    }
}