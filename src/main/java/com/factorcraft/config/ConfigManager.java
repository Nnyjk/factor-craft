package com.factorcraft.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器
 * 
 * 统一管理所有配置文件
 * 支持热重载
 */
public class ConfigManager {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, JsonObject> CONFIGS = new HashMap<>();
    private static Path configDir;
    
    /**
     * 初始化配置系统
     */
    public static void initialize() {
        configDir = Paths.get("config", FactorCraftMod.MOD_ID);
        
        try {
            Files.createDirectories(configDir);
            FactorCraftMod.LOGGER.info("[ConfigManager] 配置目录: {}", configDir.toAbsolutePath());
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[ConfigManager] 无法创建配置目录", e);
        }
        
        // 加载默认配置
        loadDefaultConfigs();
        
        // 加载外部配置
        loadExternalConfigs();
    }
    
    /**
     * 加载默认配置（从 resources）
     */
    private static void loadDefaultConfigs() {
        String[] defaultConfigs = {
            "weapons.json",
            "materials.json",
            "dimensions.json"
        };
        
        for (String name : defaultConfigs) {
            try {
                loadInternalConfig(name);
            } catch (Exception e) {
                FactorCraftMod.LOGGER.debug("[ConfigManager] 无默认配置: {}", name);
            }
        }
    }
    
    /**
     * 加载内部配置
     */
    private static void loadInternalConfig(String name) throws IOException {
        String path = "config/" + name;
        InputStream stream = ConfigManager.class.getClassLoader().getResourceAsStream(path);
        
        if (stream == null) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            CONFIGS.put(name.replace(".json", ""), json);
            FactorCraftMod.LOGGER.debug("[ConfigManager] 加载内部配置: {}", name);
        }
    }
    
    /**
     * 加载外部配置（从 config 目录）
     */
    private static void loadExternalConfigs() {
        if (configDir == null || !Files.exists(configDir)) {
            return;
        }
        
        try {
            Files.walk(configDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(ConfigManager::loadConfigFile);
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[ConfigManager] 无法遍历配置目录", e);
        }
    }
    
    /**
     * 加载配置文件
     */
    private static void loadConfigFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String name = path.getFileName().toString().replace(".json", "");
            CONFIGS.put(name, json);
            FactorCraftMod.LOGGER.info("[ConfigManager] 加载外部配置: {}", name);
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[ConfigManager] 无法加载配置: {}", path, e);
        }
    }
    
    /**
     * 获取配置
     */
    public static JsonObject getConfig(String name) {
        return CONFIGS.get(name);
    }
    
    /**
     * 获取配置值
     */
    public static double getDouble(String configName, String key, double defaultValue) {
        JsonObject config = CONFIGS.get(configName);
        if (config != null && config.has(key)) {
            return config.get(key).getAsDouble();
        }
        return defaultValue;
    }
    
    /**
     * 获取配置值
     */
    public static int getInt(String configName, String key, int defaultValue) {
        JsonObject config = CONFIGS.get(configName);
        if (config != null && config.has(key)) {
            return config.get(key).getAsInt();
        }
        return defaultValue;
    }
    
    /**
     * 获取配置值
     */
    public static String getString(String configName, String key, String defaultValue) {
        JsonObject config = CONFIGS.get(configName);
        if (config != null && config.has(key)) {
            return config.get(key).getAsString();
        }
        return defaultValue;
    }
    
    /**
     * 热重载配置
     */
    public static void reload() {
        FactorCraftMod.LOGGER.info("[ConfigManager] 重载配置...");
        CONFIGS.clear();
        loadDefaultConfigs();
        loadExternalConfigs();
        FactorCraftMod.LOGGER.info("[ConfigManager] 配置重载完成");
    }
}