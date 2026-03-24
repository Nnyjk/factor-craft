package com.factorcraft.module.quest.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务配置加载器
 * 
 * 从数据包加载任务配置 JSON 文件
 */
public class QuestConfigLoader implements SimpleSynchronousResourceReloadListener {
    
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
    
    private static final String QUESTS_PATH = "quests";
    private static final String FILE_EXTENSION = ".json";
    
    private final Map<Identifier, QuestConfig> loadedConfigs = new HashMap<>();
    private ConfigLoadCallback callback;
    
    /**
     * 配置加载回调接口
     */
    @FunctionalInterface
    public interface ConfigLoadCallback {
        void onConfigsLoaded(Map<Identifier, QuestConfig> configs);
    }
    
    @Override
    public Identifier getFabricId() {
        return Identifier.of(FactorCraftMod.MOD_ID, "quest_config_loader");
    }
    
    @Override
    public void reload(ResourceManager manager) {
        loadedConfigs.clear();
        
        Map<Identifier, Resource> resources = manager.findResources(QUESTS_PATH, path -> 
            path.toString().endsWith(FILE_EXTENSION)
        );
        
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            try {
                Resource resource = entry.getValue();
                if (resource == null) {
                    continue;
                }
                
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
                )) {
                    QuestConfig config = GSON.fromJson(reader, QuestConfig.class);
                    
                    if (config != null && config.getId() != null) {
                        Identifier questId = config.getIdentifier();
                        loadedConfigs.put(questId, config);
                        FactorCraftMod.LOGGER.info("Loaded quest config: {}", questId);
                    }
                }
            } catch (IOException e) {
                FactorCraftMod.LOGGER.error("Failed to load quest config: {}", resourceId, e);
            } catch (JsonParseException e) {
                FactorCraftMod.LOGGER.error("Failed to parse quest config: {}", resourceId, e);
            }
        }
        
        // 回调通知配置已加载
        if (callback != null) {
            callback.onConfigsLoaded(loadedConfigs);
        }
        
        FactorCraftMod.LOGGER.info("Loaded {} quest configs", loadedConfigs.size());
    }
    
    /**
     * 设置加载回调
     */
    public void setCallback(ConfigLoadCallback callback) {
        this.callback = callback;
    }
    
    /**
     * 获取所有加载的配置
     */
    public Map<Identifier, QuestConfig> getLoadedConfigs() {
        return loadedConfigs;
    }
    
    /**
     * 获取特定配置
     */
    public QuestConfig getConfig(Identifier questId) {
        return loadedConfigs.get(questId);
    }
    
    /**
     * 检查配置是否存在
     */
    public boolean hasConfig(Identifier questId) {
        return loadedConfigs.containsKey(questId);
    }
    
    /**
     * 获取配置数量
     */
    public int getConfigCount() {
        return loadedConfigs.size();
    }
}