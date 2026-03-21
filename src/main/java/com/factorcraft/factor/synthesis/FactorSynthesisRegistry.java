package com.factorcraft.factor.synthesis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factor 合成配方注册器
 * 
 * 管理所有合成配方，支持数据驱动加载
 */
public class FactorSynthesisRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorSynthesisRegistry");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String DATA_TYPE = "factor_synthesis";
    
    // 单例实例
    private static final FactorSynthesisRegistry INSTANCE = new FactorSynthesisRegistry();
    
    // 配方存储
    private final Map<Identifier, FactorSynthesisRecipe> recipes = new ConcurrentHashMap<>();
    private final Map<Identifier, List<Identifier>> outputToRecipes = new ConcurrentHashMap<>();
    
    // 私有构造器
    private FactorSynthesisRegistry() {}
    
    // ========== 实例访问 ==========
    
    public static FactorSynthesisRegistry getInstance() {
        return INSTANCE;
    }
    
    // ========== 配方注册 ==========
    
    /**
     * 注册合成配方
     */
    public void register(FactorSynthesisRecipe recipe) {
        Objects.requireNonNull(recipe, "Recipe cannot be null");
        Identifier id = recipe.getId();
        
        if (recipes.containsKey(id)) {
            LOGGER.warn("Overwriting existing synthesis recipe: {}", id);
        }
        
        recipes.put(id, recipe);
        
        // 建立输出到配方的索引
        Identifier outputId = recipe.getOutputFactorId();
        outputToRecipes.computeIfAbsent(outputId, k -> new ArrayList<>()).add(id);
        
        LOGGER.debug("Registered synthesis recipe: {} -> {} ({} inputs, {}% success)", 
            id, outputId, recipe.getInputs().size(), (int)(recipe.getSuccessRate() * 100));
    }
    
    /**
     * 批量注册配方
     */
    public void registerAll(Collection<FactorSynthesisRecipe> recipes) {
        recipes.forEach(this::register);
    }
    
    /**
     * 注销配方
     */
    public void unregister(Identifier id) {
        FactorSynthesisRecipe removed = recipes.remove(id);
        if (removed != null) {
            // 从输出索引中移除
            Identifier outputId = removed.getOutputFactorId();
            List<Identifier> recipesForOutput = outputToRecipes.get(outputId);
            if (recipesForOutput != null) {
                recipesForOutput.remove(id);
                if (recipesForOutput.isEmpty()) {
                    outputToRecipes.remove(outputId);
                }
            }
            LOGGER.debug("Unregistered synthesis recipe: {}", id);
        }
    }
    
    /**
     * 清空所有配方
     */
    public void clear() {
        recipes.clear();
        outputToRecipes.clear();
        LOGGER.info("Cleared all synthesis recipes");
    }
    
    // ========== 配方查询 ==========
    
    /**
     * 根据 ID 获取配方
     */
    public Optional<FactorSynthesisRecipe> getRecipe(Identifier id) {
        return Optional.ofNullable(recipes.get(id));
    }
    
    /**
     * 获取所有配方
     */
    public Collection<FactorSynthesisRecipe> getAllRecipes() {
        return Collections.unmodifiableCollection(recipes.values());
    }
    
    /**
     * 获取所有配方 ID
     */
    public Set<Identifier> getAllRecipeIds() {
        return Collections.unmodifiableSet(recipes.keySet());
    }
    
    /**
     * 根据输出 Factor ID 获取相关配方
     */
    public List<FactorSynthesisRecipe> getRecipesForOutput(Identifier outputFactorId) {
        List<Identifier> recipeIds = outputToRecipes.get(outputFactorId);
        if (recipeIds == null || recipeIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<FactorSynthesisRecipe> result = new ArrayList<>();
        for (Identifier id : recipeIds) {
            FactorSynthesisRecipe recipe = recipes.get(id);
            if (recipe != null) {
                result.add(recipe);
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    /**
     * 检查配方是否存在
     */
    public boolean hasRecipe(Identifier id) {
        return recipes.containsKey(id);
    }
    
    /**
     * 获取配方数量
     */
    public int getRecipeCount() {
        return recipes.size();
    }
    
    // ========== 数据加载 ==========
    
    /**
     * 从资源管理器加载配方
     * 用于数据包支持
     */
    public void loadFromResources(ResourceManager resourceManager) {
        ResourceFinder finder = new ResourceFinder(DATA_TYPE, ".json");
        
        int loaded = 0;
        int failed = 0;
        
        for (Map.Entry<Identifier, Resource> entry : finder.findResources(resourceManager).entrySet()) {
            Identifier resourceId = entry.getKey();
            Identifier recipeId = finder.toResourceId(resourceId);
            Resource resource = entry.getValue();
            
            try (BufferedReader reader = new BufferedReader(
                     new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                FactorSynthesisRecipe recipe = FactorSynthesisRecipe.CODEC
                    .parse(JsonOps.INSTANCE, json)
                    .getOrThrow(JsonParseException::new);
                
                register(recipe);
                loaded++;
                
            } catch (IOException | JsonParseException e) {
                LOGGER.error("Failed to load synthesis recipe {} from {}: {}", 
                    recipeId, resourceId, e.getMessage());
                failed++;
            }
        }
        
        LOGGER.info("Loaded {} synthesis recipes ({} failed)", loaded, failed);
    }
    
    // ========== 调试工具 ==========
    
    /**
     * 打印所有配方（调试用）
     */
    public void dumpRecipes() {
        LOGGER.info("=== Factor Synthesis Recipes ===");
        for (FactorSynthesisRecipe recipe : recipes.values()) {
            LOGGER.info("  {} -> {} ({} inputs)", 
                recipe.getId(), 
                recipe.getOutputFactorId(),
                recipe.getInputs().size());
        }
        LOGGER.info("Total: {} recipes", recipes.size());
    }
}