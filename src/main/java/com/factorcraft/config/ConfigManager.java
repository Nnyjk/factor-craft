package com.factorcraft.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.factorcraft.config.ConfigValidator.ValidationResult;
import com.factorcraft.config.ConfigVersionChecker.CompatibilityResult;
import com.factorcraft.config.ConfigVersionChecker.CompatibilityStatus;

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
 * 支持热重载、验证、版本控制和默认值回退
 */
public class ConfigManager {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, JsonObject> CONFIGS = new HashMap<>();
    private static Path configDir;
    private static final ConfigValidator VALIDATOR = new ConfigValidator();
    private static final ConfigVersionChecker VERSION_CHECKER = new ConfigVersionChecker();
    
    /**
     * 初始化配置系统
     */
    public static void initialize() {
        configDir = Paths.get("config", FactorCraftMod.MOD_ID);
        
        try {
            Files.createDirectories(configDir);
            FactorCraftMod.LOGGER.info("[FactorCraft:Config] 配置目录：{}", configDir.toAbsolutePath());
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Config] 无法创建配置目录", e);
        }
        
        // 加载内嵌默认值
        ConfigDefaults.loadEmbeddedDefaults();
        
        // 加载默认配置
        loadDefaultConfigs();
        
        // 加载外部配置
        loadExternalConfigs();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Config] 配置系统初始化完成，已加载 {} 个配置", CONFIGS.size());
        
        // 初始化平衡配置
        BalanceConfig.initialize();
    }
    
    /**
     * 加载默认配置（从 resources）
     */
    private static void loadDefaultConfigs() {
        String[] defaultConfigs = {
            "weapons.json",
            "materials.json",
            "dimensions.json",
            "traits.json",
            "biome_concentrations.json",
            "resonance_rules.json",
            "cultivation.json",
            "structure_unlocks.json",
            "extraction.json",
            "material_production.json"
        };
        
        for (String name : defaultConfigs) {
            try {
                loadInternalConfig(name);
            } catch (Exception e) {
                FactorCraftMod.LOGGER.debug("[FactorCraft:Config] 无默认配置：{}", name);
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
            String configName = name.replace(".json", "");
            
            // 验证和版本检查
            if (validateAndProcessConfig(json, configName, "internal")) {
                CONFIGS.put(configName, json);
                FactorCraftMod.LOGGER.debug("[FactorCraft:Config] 加载内部配置：{}", name);
            }
        }
    }
    
    /**
     * 加载外部配置（从 config 目录）
     */
    private static void loadExternalConfigs() {
        if (configDir == null || !Files.exists(configDir)) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Config] 外部配置目录不存在，跳过加载");
            return;
        }
        
        try {
            Files.walk(configDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .forEach(ConfigManager::loadConfigFile);
            
            FactorCraftMod.LOGGER.info("[FactorCraft:Config] 外部配置加载完成");
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Config] 无法遍历配置目录", e);
        }
    }
    
    /**
     * 加载配置文件
     */
    private static void loadConfigFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            String name = path.getFileName().toString().replace(".json", "");
            
            // 验证和版本检查
            if (validateAndProcessConfig(json, name, "external")) {
                // 合并默认值
                JsonObject merged = ConfigDefaults.mergeWithDefaults(json, name);
                CONFIGS.put(name, merged);
                FactorCraftMod.LOGGER.info("[FactorCraft:Config] 加载外部配置：{}", name);
            }
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Config] 无法加载配置：{}", path, e);
        }
    }
    
    /**
     * 验证和处理配置
     * 
     * @return true 如果配置有效可加载
     */
    private static boolean validateAndProcessConfig(JsonObject config, String configName, String source) {
        // 版本检查
        String configVersion = VERSION_CHECKER.getVersion(config);
        String requiredVersion = "1.0.0"; // 默认需要 1.0.0
        
        CompatibilityResult versionResult = VERSION_CHECKER.checkCompatibility(configVersion, requiredVersion);
        VERSION_CHECKER.logCheckResult(configName + " (" + source + ")", versionResult);
        
        if (!versionResult.isCompatible()) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Config] 配置 {} 版本不兼容，跳过加载", configName);
            return false;
        }
        
        // 基础验证（版本号必填）
        ValidationResult validation = VALIDATOR.validateVersion(config, configName);
        if (!validation.isValid()) {
            FactorCraftMod.LOGGER.error("[FactorCraft:Config] 配置 {} 验证失败：{}", configName, 
                                       validation.getErrors());
            return false;
        }
        
        // 记录警告
        if (!validation.getWarnings().isEmpty()) {
            for (String warning : validation.getWarnings()) {
                FactorCraftMod.LOGGER.warn("[FactorCraft:Config] 配置 {} 警告：{}", configName, warning);
            }
        }
        
        return true;
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
        return ConfigDefaults.getDouble(config, key, defaultValue);
    }
    
    /**
     * 获取配置值
     */
    public static int getInt(String configName, String key, int defaultValue) {
        JsonObject config = CONFIGS.get(configName);
        return ConfigDefaults.getInt(config, key, defaultValue);
    }
    
    /**
     * 获取配置值
     */
    public static String getString(String configName, String key, String defaultValue) {
        JsonObject config = CONFIGS.get(configName);
        return ConfigDefaults.getString(config, key, defaultValue);
    }
    
    /**
     * 获取配置值
     */
    public static boolean getBoolean(String configName, String key, boolean defaultValue) {
        JsonObject config = CONFIGS.get(configName);
        return ConfigDefaults.getBoolean(config, key, defaultValue);
    }
    
    /**
     * 获取机器配置
     */
    public static JsonObject getMachineConfig(String machineType, String machineId) {
        JsonObject config = CONFIGS.get(machineType);
        if (config == null || !config.has("machines")) {
            return null;
        }
        
        JsonObject machines = config.getAsJsonObject("machines");
        if (machines.has(machineId)) {
            return machines.getAsJsonObject(machineId);
        }
        
        // 回退到默认值
        JsonObject defaults = ConfigDefaults.getDefaults(machineType);
        if (defaults != null && defaults.has("machines")) {
            JsonObject defaultMachines = defaults.getAsJsonObject("machines");
            if (defaultMachines.has(machineId)) {
                return defaultMachines.getAsJsonObject(machineId);
            }
        }
        
        return null;
    }
    
    /**
     * 热重载配置
     */
    public static void reload() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Config] 重载配置...");
        CONFIGS.clear();
        loadDefaultConfigs();
        loadExternalConfigs();
        BalanceConfig.reload();
        FactorCraftMod.LOGGER.info("[FactorCraft:Config] 配置重载完成，已加载 {} 个配置", CONFIGS.size());
    }
    
    /**
     * 获取所有配置名称
     */
    public static java.util.Set<String> getConfigNames() {
        return CONFIGS.keySet();
    }
    
    /**
     * 检查配置是否已加载
     */
    public static boolean hasConfig(String name) {
        return CONFIGS.containsKey(name);
    }
}
