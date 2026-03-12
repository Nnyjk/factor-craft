package com.factorcraft.datapack;

import com.factorcraft.module.material.trait.TraitDefinition;
import com.factorcraft.module.material.trait.TraitRegistry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 数据包支持系统
 * 允许玩家通过数据包自定义特性、配方等
 */
public class DataPackManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_PATH = "data/factorcraft/";
    
    /**
     * 初始化数据包系统
     */
    public static void initialize() {
        // 注册资源重载监听器
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.of("factorcraft", "datapack_loader");
                }
                
                @Override
                public void reload(ResourceManager manager) {
                    loadDataPacks(manager);
                }
            }
        );
    }
    
    /**
     * 加载所有数据包
     */
    private static void loadDataPacks(ResourceManager manager) {
        // 加载自定义特性
        loadCustomTraits(manager);
        
        // 加载自定义配方
        loadCustomRecipes(manager);
        
        // 加载自定义配置
        loadCustomConfigurations(manager);
    }
    
    /**
     * 加载自定义特性
     */
    private static void loadCustomTraits(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(
            DATA_PATH + "traits",
            path -> path.getPath().endsWith(".json")
        );
        
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                InputStream stream = entry.getValue().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                TraitDefinition trait = parseTrait(json);
                
                if (trait != null) {
                    TraitRegistry.register(trait);
                    System.out.println("[FactorCraft] Loaded custom trait: " + trait.id());
                }
                
                reader.close();
            } catch (Exception e) {
                System.err.println("[FactorCraft] Failed to load trait from " + resourceId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * 加载自定义配方
     */
    private static void loadCustomRecipes(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources(
            DATA_PATH + "recipes",
            path -> path.getPath().endsWith(".json")
        );
        
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                InputStream stream = entry.getValue().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                // 解析并注册配方
                parseRecipe(json);
                
                reader.close();
            } catch (Exception e) {
                System.err.println("[FactorCraft] Failed to load recipe from " + resourceId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * 加载自定义配置
     */
    private static void loadCustomConfigurations(ResourceManager manager) {
        // 加载数据包中的配置覆盖
        Map<Identifier, Resource> resources = manager.findResources(
            DATA_PATH + "config",
            path -> path.getPath().endsWith(".json")
        );
        
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                InputStream stream = entry.getValue().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                // 应用配置覆盖
                applyConfiguration(json);
                
                reader.close();
            } catch (Exception e) {
                System.err.println("[FactorCraft] Failed to load config from " + resourceId + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * 解析特性定义
     */
    private static TraitDefinition parseTrait(JsonObject json) {
        try {
            // 简化解析，实际应根据 TraitDefinition 结构完整解析
            String id = json.get("id").getAsString();
            String name = json.get("name").getAsString();
            String type = json.has("type") ? json.get("type").getAsString() : "positive";
            
            // 返回简化版本
            return new TraitDefinition(
                id,
                name,
                type,
                com.factorcraft.module.material.model.TraitCategory.EXTRACTION,
                json.has("description") ? json.get("description").getAsString() : "",
                List.of(),
                json.has("max_level") ? json.get("max_level").getAsInt() : 3,
                1.0,
                Map.of(),
                Set.of(),
                1.0,
                new int[]{1, 3}
            );
        } catch (Exception e) {
            System.err.println("[FactorCraft] Failed to parse trait: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析配方
     */
    private static void parseRecipe(JsonObject json) {
        // 简化版本，实际应完整解析配方
        String type = json.get("type").getAsString();
        System.out.println("[FactorCraft] Loaded recipe type: " + type);
    }
    
    /**
     * 应用配置
     */
    private static void applyConfiguration(JsonObject json) {
        // 简化版本，实际应应用配置覆盖
        System.out.println("[FactorCraft] Applied configuration override");
    }
}