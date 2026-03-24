package com.factorcraft.module.profession.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 职业配置加载器
 * 从数据包加载职业配置
 */
public class ProfessionConfigLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ProfessionConfig");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_PATH = "profession_configs";
    
    private static final Map<String, ProfessionConfig> loadedConfigs = new HashMap<>();
    
    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                @Override
                public Identifier getFabricId() {
                    return Identifier.of("factorcraft", "profession_configs");
                }
                
                @Override
                public void reload(ResourceManager manager) {
                    loadedConfigs.clear();
                    
                    Collection<Identifier> resources = manager.findResources(CONFIG_PATH, 
                        path -> path.getPath().endsWith(".json")).keySet();
                    
                    for (Identifier id : resources) {
                        try {
                            Resource resource = manager.getResource(id).orElse(null);
                            if (resource == null) continue;
                            
                            InputStreamReader reader = new InputStreamReader(
                                resource.getInputStream(), StandardCharsets.UTF_8);
                            ProfessionConfig config = GSON.fromJson(reader, ProfessionConfig.class);
                            
                            if (config != null && config.getId() != null) {
                                loadedConfigs.put(config.getId(), config);
                                LOGGER.info("Loaded profession config: {}", config.getId());
                            }
                        } catch (Exception e) {
                            LOGGER.error("Failed to load profession config: {}", id, e);
                        }
                    }
                    
                    LOGGER.info("Loaded {} profession configs", loadedConfigs.size());
                }
            }
        );
        
        LOGGER.info("Profession config loader initialized");
    }
    
    /**
     * 获取已加载的职业配置
     */
    public static Map<String, ProfessionConfig> getLoadedConfigs() {
        return loadedConfigs;
    }
    
    /**
     * 根据ID获取职业配置
     */
    public static ProfessionConfig getConfig(String id) {
        return loadedConfigs.get(id);
    }
    
    /**
     * 检查配置是否存在
     */
    public static boolean hasConfig(String id) {
        return loadedConfigs.containsKey(id);
    }
}