package com.factorcraft.recipe;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 配方加载器 - 从 JSON 文件加载配方数据
 */
public class RecipeLoader {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * 初始化配方加载器
     */
    public static void initialize() {
        // 注册资源重载监听器
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.of(FactorCraftMod.MOD_ID, "recipe_loader");
                }
                
                @Override
                public void reload(ResourceManager manager) {
                    loadAllRecipes(manager);
                }
            }
        );
        
        FactorCraftMod.LOGGER.info("[FactorCraft] 配方加载器初始化完成");
    }
    
    /**
     * 加载所有配方
     */
    private static void loadAllRecipes(ResourceManager manager) {
        // 清除旧配方
        RecipeRegistry.clearAll();
        
        // 加载 Factor 融合配方
        loadFactorFusionRecipes(manager);
        
        // 加载特性注入配方
        loadTraitInfusionRecipes(manager);
        
        FactorCraftMod.LOGGER.info("[FactorCraft] 配方加载完成");
    }
    
    /**
     * 加载 Factor 融合配方
     */
    private static void loadFactorFusionRecipes(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(
            "data/factorcraft/recipes",
            path -> path.getPath().contains("factor_fusion") || 
                    path.getPath().contains("material_upgrade")
        );
        
        int loaded = 0;
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                InputStream stream = entry.getValue().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                FactorFusionRecipeData recipe = parseFactorFusionRecipe(json, resourceId.toString());
                
                if (recipe != null) {
                    RecipeRegistry.registerFactorFusion(recipe);
                    loaded++;
                }
                
                reader.close();
            } catch (Exception e) {
                FactorCraftMod.LOGGER.error("[FactorCraft] 加载配方失败 {}: {}", resourceId, e.getMessage());
            }
        }
        
        FactorCraftMod.LOGGER.info("[FactorCraft] 加载了 {} 个 Factor 融合配方", loaded);
    }
    
    /**
     * 加载特性注入配方
     */
    private static void loadTraitInfusionRecipes(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(
            "data/factorcraft/recipes",
            path -> path.getPath().contains("trait_infusion")
        );
        
        int loaded = 0;
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                InputStream stream = entry.getValue().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                TraitInfusionRecipeData recipe = parseTraitInfusionRecipe(json, resourceId.toString());
                
                if (recipe != null) {
                    RecipeRegistry.registerTraitInfusion(recipe);
                    loaded++;
                }
                
                reader.close();
            } catch (Exception e) {
                FactorCraftMod.LOGGER.error("[FactorCraft] 加载配方失败 {}: {}", resourceId, e.getMessage());
            }
        }
        
        FactorCraftMod.LOGGER.info("[FactorCraft] 加载了 {} 个特性注入配方", loaded);
    }
    
    /**
     * 解析 Factor 融合配方
     */
    private static FactorFusionRecipeData parseFactorFusionRecipe(JsonObject json, String source) {
        try {
            String type = json.get("type").getAsString();
            if (!type.equals("factorcraft:factor_fusion")) {
                return null;
            }
            
            String id = extractRecipeId(source);
            String group = json.has("group") ? json.get("group").getAsString() : "";
            
            // 解析输入
            JsonObject inputJson = json.getAsJsonObject("input");
            String inputItem = inputJson.has("item") ? inputJson.get("item").getAsString() : null;
            if (inputJson.has("tag")) {
                FactorCraftMod.LOGGER.warn("[FactorCraft] 配方 {} 使用标签输入，暂不支持", id);
            }
            int inputCount = json.has("input_count") ? json.get("input_count").getAsInt() : 1;
            
            // 解析输出
            JsonObject outputJson = json.getAsJsonObject("output");
            String outputItemId = outputJson.get("item").getAsString();
            int outputCount = outputJson.has("count") ? outputJson.get("count").getAsInt() : 1;
            ItemStack output = createItemStack(outputItemId, outputCount);
            
            // 解析配方参数
            double factorCost = json.get("factor_cost").getAsDouble();
            int craftTime = json.has("craft_time") ? json.get("craft_time").getAsInt() : 200;
            String category = json.has("category") ? json.get("category").getAsString() : "misc";
            
            return new FactorFusionRecipeData(
                id,
                group,
                inputItem,
                inputCount,
                output,
                factorCost,
                craftTime,
                category
            );
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[FactorCraft] 解析 Factor 融合配方失败 {}: {}", source, e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析特性注入配方
     */
    private static TraitInfusionRecipeData parseTraitInfusionRecipe(JsonObject json, String source) {
        try {
            String type = json.get("type").getAsString();
            if (!type.equals("factorcraft:trait_infusion")) {
                return null;
            }
            
            String id = extractRecipeId(source);
            String group = json.has("group") ? json.get("group").getAsString() : "";
            
            // 解析输入
            JsonObject inputJson = json.getAsJsonObject("input");
            String inputItem = inputJson.get("item").getAsString();
            int inputCount = json.has("input_count") ? json.get("input_count").getAsInt() : 1;
            
            // 解析特性物品
            JsonObject traitJson = json.getAsJsonObject("trait_item");
            String traitItem = traitJson.get("item").getAsString();
            
            // 解析输出
            JsonObject outputJson = json.getAsJsonObject("output");
            String outputItemId = outputJson.get("item").getAsString();
            int outputCount = outputJson.has("count") ? outputJson.get("count").getAsInt() : 1;
            ItemStack output = createItemStack(outputItemId, outputCount);
            
            // 解析配方参数
            double factorCost = json.get("factor_cost").getAsDouble();
            int craftTime = json.has("craft_time") ? json.get("craft_time").getAsInt() : 200;
            double successRate = json.has("success_rate") ? json.get("success_rate").getAsDouble() : 0.3;
            String category = json.has("category") ? json.get("category").getAsString() : "misc";
            
            return new TraitInfusionRecipeData(
                id,
                group,
                inputItem,
                traitItem,
                inputCount,
                output,
                factorCost,
                craftTime,
                successRate,
                category
            );
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[FactorCraft] 解析特性注入配方失败 {}: {}", source, e.getMessage());
            return null;
        }
    }
    
    /**
     * 从资源路径提取配方 ID
     */
    private static String extractRecipeId(String resourcePath) {
        // 例如：data/factorcraft/recipes/material_upgrade_t1.json -> material_upgrade_t1
        if (resourcePath.contains("/recipes/")) {
            String afterRecipes = resourcePath.substring(resourcePath.indexOf("/recipes/") + 9);
            if (afterRecipes.endsWith(".json")) {
                return afterRecipes.substring(0, afterRecipes.length() - 5);
            }
        }
        return "unknown";
    }
    
    /**
     * 创建物品堆
     */
    private static ItemStack createItemStack(String itemId, int count) {
        Item item = Registries.ITEM.get(Identifier.tryParse(itemId));
        if (item == null) {
            FactorCraftMod.LOGGER.warn("[FactorCraft] 未知物品：{}", itemId);
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, Math.min(count, item.getMaxCount()));
    }
}
