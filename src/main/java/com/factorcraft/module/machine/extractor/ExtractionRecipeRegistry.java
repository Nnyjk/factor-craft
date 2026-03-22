package com.factorcraft.module.machine.extractor;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * 提取配方注册器
 * 
 * 数据驱动的提取配方管理
 * 从 data/factorcraft/extraction_recipes/ 加载配方
 */
public class ExtractionRecipeRegistry extends SinglePreparationResourceReloader<Map<Identifier, ExtractionRecipe>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ExtractionRecipeRegistry");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Identifier ID = Identifier.of("factorcraft", "extraction_recipes");
    
    // 配方存储
    private static final Map<Identifier, ExtractionRecipe> RECIPES = new HashMap<>();
    private static final Map<Identifier, List<ExtractionRecipe>> RECIPES_BY_INPUT = new HashMap<>();
    
    // ========== 单例实例 ==========
    
    private static final ExtractionRecipeRegistry INSTANCE = new ExtractionRecipeRegistry();
    
    public static ExtractionRecipeRegistry getInstance() {
        return INSTANCE;
    }
    
    // ========== 查询方法 ==========
    
    /**
     * 获取所有配方
     */
    public static Collection<ExtractionRecipe> getAllRecipes() {
        return Collections.unmodifiableCollection(RECIPES.values());
    }
    
    /**
     * 根据 ID 获取配方
     */
    public static Optional<ExtractionRecipe> getRecipe(Identifier id) {
        return Optional.ofNullable(RECIPES.get(id));
    }
    
    /**
     * 根据输入物品查找匹配的配方
     */
    public static List<ExtractionRecipe> findRecipesForInput(ItemStack input) {
        if (input == null || input.isEmpty()) return List.of();
        
        List<ExtractionRecipe> result = new ArrayList<>();
        
        // 遍历所有配方查找匹配项
        for (ExtractionRecipe recipe : RECIPES.values()) {
            if (recipe.matches(input)) {
                result.add(recipe);
            }
        }
        
        return result;
    }
    
    /**
     * 查找第一个匹配的配方
     */
    public static Optional<ExtractionRecipe> findFirstRecipe(ItemStack input) {
        return findRecipesForInput(input).stream().findFirst();
    }
    
    /**
     * 检查物品是否有对应的提取配方
     */
    public static boolean hasRecipeFor(ItemStack input) {
        return !findRecipesForInput(input).isEmpty();
    }
    
    /**
     * 获取配方数量
     */
    public static int getRecipeCount() {
        return RECIPES.size();
    }
    
    // ========== 资源重载 ==========
    
    @Override
    protected Map<Identifier, ExtractionRecipe> prepare(ResourceManager manager, Profiler profiler) {
        profiler.startTick();
        Map<Identifier, ExtractionRecipe> recipes = new HashMap<>();
        
        // 加载所有 extraction_recipes/*.json 文件
        for (Identifier resourceId : manager.findResources("extraction_recipes", path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                // 读取文件内容
                Optional<Resource> resource = manager.getResource(resourceId);
                if (resource.isEmpty()) continue;
                
                try (BufferedReader reader = resource.get().getReader()) {
                    JsonElement json = GSON.fromJson(reader, JsonElement.class);
                    
                    // 解析配方
                    ExtractionRecipe recipe = parseRecipe(resourceId, json);
                    if (recipe != null) {
                        recipes.put(recipe.getId(), recipe);
                    }
                }
            } catch (IOException | JsonParseException e) {
                LOGGER.error("Failed to load extraction recipe: {}", resourceId, e);
            }
        }
        
        profiler.endTick();
        return recipes;
    }
    
    @Override
    protected void apply(Map<Identifier, ExtractionRecipe> prepared, ResourceManager manager, Profiler profiler) {
        RECIPES.clear();
        RECIPES_BY_INPUT.clear();
        
        RECIPES.putAll(prepared);
        
        // 建立输入物品索引
        for (ExtractionRecipe recipe : RECIPES.values()) {
            Identifier itemId = Identifier.of(recipe.getInput().getItem().toString());
            RECIPES_BY_INPUT.computeIfAbsent(itemId, k -> new ArrayList<>()).add(recipe);
        }
        
        LOGGER.info("Loaded {} extraction recipes", RECIPES.size());
    }
    
    // ========== 配方解析 ==========
    
    private ExtractionRecipe parseRecipe(Identifier fileId, JsonElement json) {
        try {
            // 从文件路径提取配方 ID
            String path = fileId.getPath();
            String recipeName = path.substring("extraction_recipes/".length(), path.length() - ".json".length());
            Identifier recipeId = Identifier.of("factorcraft", recipeName);
            
            // 解析配方内容
            var jsonObj = json.getAsJsonObject();
            
            // 解析输入物品
            ItemStack input = parseItemStack(jsonObj.get("input"));
            
            // 解析输出
            ExtractionRecipe.ExtractionOutput output = parseOutput(jsonObj.get("output"));
            
            // 解析能量消耗和处理时间
            int energyCost = jsonObj.has("energy_cost") ? jsonObj.get("energy_cost").getAsInt() : 1000;
            int processingTime = jsonObj.has("processing_time") ? jsonObj.get("processing_time").getAsInt() : 200;
            
            // 解析可选的结构需求
            Optional<Identifier> requiredStructure = Optional.empty();
            if (jsonObj.has("required_structure")) {
                requiredStructure = Optional.of(Identifier.of(jsonObj.get("required_structure").getAsString()));
            }
            
            return new ExtractionRecipe(recipeId, input, output, energyCost, processingTime, requiredStructure);
        } catch (Exception e) {
            LOGGER.error("Failed to parse extraction recipe from {}: {}", fileId, e.getMessage());
            return null;
        }
    }
    
    private ItemStack parseItemStack(JsonElement json) {
        var obj = json.getAsJsonObject();
        String itemId = obj.get("item").getAsString();
        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        
        return new ItemStack(Registries.ITEM.get(Identifier.of(itemId)), count);
    }
    
    private ExtractionRecipe.ExtractionOutput parseOutput(JsonElement json) {
        var obj = json.getAsJsonObject();
        
        return new ExtractionRecipe.ExtractionOutput(
            obj.get("factor_type").getAsString(),
            obj.has("min_level") ? obj.get("min_level").getAsInt() : 1,
            obj.has("max_level") ? obj.get("max_level").getAsInt() : 10,
            obj.has("min_power") ? obj.get("min_power").getAsDouble() : 10.0,
            obj.has("max_power") ? obj.get("max_power").getAsDouble() : 50.0,
            obj.has("base_chance") ? obj.get("base_chance").getAsDouble() : 1.0,
            obj.has("count") ? obj.get("count").getAsInt() : 1
        );
    }
}