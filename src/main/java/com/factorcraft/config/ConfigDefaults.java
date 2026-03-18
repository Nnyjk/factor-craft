package com.factorcraft.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置默认值管理器
 * 
 * 提供配置文件的默认值回退机制
 */
public class ConfigDefaults {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, JsonObject> DEFAULTS = new HashMap<>();
    
    /**
     * 注册配置默认值
     */
    public static void registerDefaults(String configName, JsonObject defaults) {
        DEFAULTS.put(configName, defaults);
        FactorCraftMod.LOGGER.debug("[ConfigDefaults] 注册默认配置：{}", configName);
    }
    
    /**
     * 获取配置默认值
     */
    public static JsonObject getDefaults(String configName) {
        JsonObject defaults = DEFAULTS.get(configName);
        if (defaults == null) {
            FactorCraftMod.LOGGER.warn("[ConfigDefaults] 未找到默认配置：{}，返回空对象", configName);
            return new JsonObject();
        }
        return defaults.deepCopy(); // 返回深拷贝，防止修改原始默认值
    }
    
    /**
     * 检查是否有默认值
     */
    public static boolean hasDefaults(String configName) {
        return DEFAULTS.containsKey(configName);
    }
    
    /**
     * 加载内嵌默认配置
     */
    public static void loadEmbeddedDefaults() {
        // 机器配置默认值
        loadMachineDefaults();
        
        // 特性配置默认值
        loadTraitDefaults();
        
        // 世界生成配置默认值
        loadWorldGenDefaults();
    }
    
    /**
     * 加载机器配置默认值
     */
    private static void loadMachineDefaults() {
        // 提取器默认配置
        JsonObject extractorDefaults = new JsonObject();
        extractorDefaults.addProperty("version", "1.0.0");
        extractorDefaults.addProperty("schema", "factorcraft:machines/extractor/1.0");
        
        JsonObject extractorMachines = new JsonObject();
        
        // T1 提取器
        JsonObject extractorT1 = new JsonObject();
        extractorT1.addProperty("speed", 1.0);
        extractorT1.addProperty("capacity", 1000);
        extractorT1.addProperty("energy_consumption", 10);
        extractorT1.addProperty("range", 3);
        extractorT1.addProperty("work_interval", 20); // 20 ticks = 1 秒
        extractorMachines.add("extractor_t1", extractorT1);
        
        // T2 提取器
        JsonObject extractorT2 = new JsonObject();
        extractorT2.addProperty("speed", 2.0);
        extractorT2.addProperty("capacity", 5000);
        extractorT2.addProperty("energy_consumption", 25);
        extractorT2.addProperty("range", 5);
        extractorT2.addProperty("work_interval", 15);
        extractorMachines.add("extractor_t2", extractorT2);
        
        // T3 提取器
        JsonObject extractorT3 = new JsonObject();
        extractorT3.addProperty("speed", 4.0);
        extractorT3.addProperty("capacity", 20000);
        extractorT3.addProperty("energy_consumption", 60);
        extractorT3.addProperty("range", 7);
        extractorT3.addProperty("work_interval", 10);
        extractorMachines.add("extractor_t3", extractorT3);
        
        extractorDefaults.add("machines", extractorMachines);
        registerDefaults("extractor", extractorDefaults);
        
        // 合成器默认配置
        JsonObject synthesizerDefaults = new JsonObject();
        synthesizerDefaults.addProperty("version", "1.0.0");
        synthesizerDefaults.addProperty("schema", "factorcraft:machines/synthesizer/1.0");
        
        JsonObject synthesizerMachines = new JsonObject();
        
        JsonObject synthesizerT1 = new JsonObject();
        synthesizerT1.addProperty("speed", 1.0);
        synthesizerT1.addProperty("capacity", 2000);
        synthesizerT1.addProperty("energy_consumption", 20);
        synthesizerT1.addProperty("crafting_time", 100); // 5 秒
        synthesizerMachines.add("synthesizer_t1", synthesizerT1);
        
        JsonObject synthesizerT2 = new JsonObject();
        synthesizerT2.addProperty("speed", 2.0);
        synthesizerT2.addProperty("capacity", 10000);
        synthesizerT2.addProperty("energy_consumption", 50);
        synthesizerT2.addProperty("crafting_time", 60); // 3 秒
        synthesizerMachines.add("synthesizer_t2", synthesizerT2);
        
        synthesizerDefaults.add("machines", synthesizerMachines);
        registerDefaults("synthesizer", synthesizerDefaults);
        
        // 传递器默认配置
        JsonObject transmitterDefaults = new JsonObject();
        transmitterDefaults.addProperty("version", "1.0.0");
        transmitterDefaults.addProperty("schema", "factorcraft:machines/transmitter/1.0");
        
        JsonObject transmitterMachines = new JsonObject();
        
        JsonObject transmitterT1 = new JsonObject();
        transmitterT1.addProperty("transfer_rate", 10); // 每秒传输量
        transmitterT1.addProperty("capacity", 500);
        transmitterT1.addProperty("range", 16); // 方块半径
        transmitterT1.addProperty("dimension_transfer", false);
        transmitterMachines.add("transmitter_t1", transmitterT1);
        
        JsonObject transmitterT2 = new JsonObject();
        transmitterT2.addProperty("transfer_rate", 50);
        transmitterT2.addProperty("capacity", 2500);
        transmitterT2.addProperty("range", 32);
        transmitterT2.addProperty("dimension_transfer", true);
        transmitterMachines.add("transmitter_t2", transmitterT2);
        
        transmitterDefaults.add("machines", transmitterMachines);
        registerDefaults("transmitter", transmitterDefaults);
        
        // 消耗器默认配置
        JsonObject consumerDefaults = new JsonObject();
        consumerDefaults.addProperty("version", "1.0.0");
        consumerDefaults.addProperty("schema", "factorcraft:machines/consumer/1.0");
        
        JsonObject consumerMachines = new JsonObject();
        
        JsonObject consumerT1 = new JsonObject();
        consumerT1.addProperty("consumption_rate", 5); // 每秒消耗量
        consumerT1.addProperty("capacity", 1000);
        consumerT1.addProperty("output_factor", 1.0);
        consumerMachines.add("consumer_t1", consumerT1);
        
        JsonObject consumerT2 = new JsonObject();
        consumerT2.addProperty("consumption_rate", 25);
        consumerT2.addProperty("capacity", 5000);
        consumerT2.addProperty("output_factor", 2.5);
        consumerMachines.add("consumer_t2", consumerT2);
        
        consumerDefaults.add("machines", consumerMachines);
        registerDefaults("consumer", consumerDefaults);
        
        // 培育器默认配置
        JsonObject cultivatorDefaults = new JsonObject();
        cultivatorDefaults.addProperty("version", "1.0.0");
        cultivatorDefaults.addProperty("schema", "factorcraft:machines/cultivator/1.0");
        
        JsonObject cultivatorMachines = new JsonObject();
        
        JsonObject cultivatorT1 = new JsonObject();
        cultivatorT1.addProperty("infusion_speed", 1.0);
        cultivatorT1.addProperty("capacity", 3000);
        cultivatorT1.addProperty("energy_consumption", 30);
        cultivatorT1.addProperty("trait_slots", 3);
        cultivatorMachines.add("cultivator_t1", cultivatorT1);
        
        JsonObject cultivatorT2 = new JsonObject();
        cultivatorT2.addProperty("infusion_speed", 2.5);
        cultivatorT2.addProperty("capacity", 15000);
        cultivatorT2.addProperty("energy_consumption", 75);
        cultivatorT2.addProperty("trait_slots", 5);
        cultivatorMachines.add("cultivator_t2", cultivatorT2);
        
        cultivatorDefaults.add("machines", cultivatorMachines);
        registerDefaults("cultivator", cultivatorDefaults);
        
        FactorCraftMod.LOGGER.info("[ConfigDefaults] 机器配置默认值加载完成（5 个机器类型）");
    }
    
    /**
     * 加载特性配置默认值
     */
    private static void loadTraitDefaults() {
        JsonObject traitDefaults = new JsonObject();
        traitDefaults.addProperty("version", "1.0.0");
        traitDefaults.addProperty("schema", "factorcraft:traits/1.0");
        
        // 默认特性配置参数
        JsonObject defaultTraitConfig = new JsonObject();
        defaultTraitConfig.addProperty("max_traits_per_material", 5);
        defaultTraitConfig.addProperty("trait_rarity_weight", 100.0);
        defaultTraitConfig.addProperty("allow_negative_traits", true);
        defaultTraitConfig.addProperty("negative_trait_chance", 0.2);
        
        traitDefaults.add("config", defaultTraitConfig);
        registerDefaults("traits", traitDefaults);
        
        FactorCraftMod.LOGGER.info("[ConfigDefaults] 特性配置默认值加载完成");
    }
    
    /**
     * 加载世界生成配置默认值
     */
    private static void loadWorldGenDefaults() {
        // 生物群系浓度默认值
        JsonObject biomeDefaults = new JsonObject();
        biomeDefaults.addProperty("version", "1.0.0");
        biomeDefaults.addProperty("schema", "factorcraft:world/biome/1.0");
        
        JsonObject defaultConcentration = new JsonObject();
        defaultConcentration.addProperty("base_concentration", 50.0);
        defaultConcentration.addProperty("variation", 20.0);
        defaultConcentration.addProperty("dimension_multiplier", 1.0);
        
        biomeDefaults.add("default", defaultConcentration);
        registerDefaults("biome_concentrations", biomeDefaults);
        
        // 共振规则默认值
        JsonObject resonanceDefaults = new JsonObject();
        resonanceDefaults.addProperty("version", "1.0.0");
        resonanceDefaults.addProperty("schema", "factorcraft:world/resonance/1.0");
        
        JsonObject defaultResonance = new JsonObject();
        defaultResonance.addProperty("resonance_threshold", 75.0);
        defaultResonance.addProperty("resonance_multiplier", 1.5);
        defaultResonance.addProperty("decay_rate", 0.1);
        
        resonanceDefaults.add("config", defaultResonance);
        registerDefaults("resonance_rules", resonanceDefaults);
        
        FactorCraftMod.LOGGER.info("[ConfigDefaults] 世界生成配置默认值加载完成");
    }
    
    /**
     * 合并配置与默认值
     * 
     * @param config 用户配置
     * @param configName 配置名称
     * @return 合并后的配置
     */
    public static JsonObject mergeWithDefaults(JsonObject config, String configName) {
        JsonObject defaults = getDefaults(configName);
        
        if (defaults == null || defaults.isEmpty()) {
            return config;
        }
        
        // 深拷贝默认配置
        JsonObject merged = defaults.deepCopy();
        
        // 用用户配置覆盖默认值
        for (Map.Entry<String, JsonElement> entry : config.entrySet()) {
            merged.add(entry.getKey(), entry.getValue().deepCopy());
        }
        
        return merged;
    }
    
    /**
     * 获取配置中的数值（带默认值回退）
     */
    public static double getDouble(JsonObject config, String key, double defaultValue) {
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsDouble();
    }
    
    /**
     * 获取配置中的整数（带默认值回退）
     */
    public static int getInt(JsonObject config, String key, int defaultValue) {
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsInt();
    }
    
    /**
     * 获取配置中的布尔值（带默认值回退）
     */
    public static boolean getBoolean(JsonObject config, String key, boolean defaultValue) {
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsBoolean();
    }
    
    /**
     * 获取配置中的字符串（带默认值回退）
     */
    public static String getString(JsonObject config, String key, String defaultValue) {
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsString();
    }
}
