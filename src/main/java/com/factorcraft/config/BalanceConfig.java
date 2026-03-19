package com.factorcraft.config;

import com.factorcraft.FactorCraftMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 游戏平衡配置。
 * 管理所有游戏数值，支持难度预设。
 */
public final class BalanceConfig {
    
    private static JsonObject config;
    private static String currentDifficulty = "normal";
    
    // 缓存的难度乘数
    private static double factorExtractionRate = 1.0;
    private static double factorConsumptionRate = 1.0;
    private static double questRewardMultiplier = 1.0;
    private static double machineEfficiency = 1.0;
    private static double tideNegativeEffect = 1.0;
    
    /**
     * 初始化平衡配置
     */
    public static void initialize() {
        loadConfig();
        FactorCraftMod.LOGGER.info("[BalanceConfig] 初始化完成，难度: {}", currentDifficulty);
    }
    
    /**
     * 加载配置文件
     */
    private static void loadConfig() {
        Path externalConfig = Paths.get("config", "factorcraft", "balance.json");
        
        // 优先加载外部配置
        if (Files.exists(externalConfig)) {
            try (BufferedReader reader = Files.newBufferedReader(externalConfig)) {
                config = JsonParser.parseReader(reader).getAsJsonObject();
                FactorCraftMod.LOGGER.info("[BalanceConfig] 加载外部配置: {}", externalConfig);
            } catch (Exception e) {
                FactorCraftMod.LOGGER.error("[BalanceConfig] 无法加载外部配置，使用默认值", e);
                loadDefaultConfig();
            }
        } else {
            loadDefaultConfig();
        }
        
        // 加载当前难度
        if (config.has("difficulty")) {
            JsonObject difficulty = config.getAsJsonObject("difficulty");
            if (difficulty.has("current")) {
                currentDifficulty = difficulty.get("current").getAsString();
            }
        }
        
        // 缓存难度乘数
        cacheDifficultyMultipliers();
    }
    
    /**
     * 加载默认配置
     */
    private static void loadDefaultConfig() {
        try (InputStream stream = BalanceConfig.class.getClassLoader()
                .getResourceAsStream("config/balance.json")) {
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    config = JsonParser.parseReader(reader).getAsJsonObject();
                    FactorCraftMod.LOGGER.info("[BalanceConfig] 加载默认配置");
                }
            }
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[BalanceConfig] 无法加载默认配置", e);
            config = new JsonObject();
        }
    }
    
    /**
     * 缓存难度乘数
     */
    private static void cacheDifficultyMultipliers() {
        if (!config.has("difficulty")) return;
        
        JsonObject difficulty = config.getAsJsonObject("difficulty");
        if (!difficulty.has("presets")) return;
        
        JsonObject presets = difficulty.getAsJsonObject("presets");
        if (!presets.has(currentDifficulty)) return;
        
        JsonObject preset = presets.getAsJsonObject(currentDifficulty);
        
        factorExtractionRate = getDouble(preset, "factor_extraction_rate", 1.0);
        factorConsumptionRate = getDouble(preset, "factor_consumption_rate", 1.0);
        questRewardMultiplier = getDouble(preset, "quest_reward_multiplier", 1.0);
        machineEfficiency = getDouble(preset, "machine_efficiency", 1.0);
        tideNegativeEffect = getDouble(preset, "tide_negative_effect", 1.0);
        
        FactorCraftMod.LOGGER.info("[BalanceConfig] 难度乘数已更新: extraction={}, consumption={}, quest={}, machine={}, tide={}",
                factorExtractionRate, factorConsumptionRate, questRewardMultiplier, machineEfficiency, tideNegativeEffect);
    }
    
    // === 难度相关 ===
    
    public static String getCurrentDifficulty() {
        return currentDifficulty;
    }
    
    public static void setDifficulty(String difficulty) {
        if (!difficulty.equals("easy") && !difficulty.equals("normal") && !difficulty.equals("hard")) {
            FactorCraftMod.LOGGER.warn("[BalanceConfig] 无效难度: {}, 使用 normal", difficulty);
            difficulty = "normal";
        }
        currentDifficulty = difficulty;
        cacheDifficultyMultipliers();
        FactorCraftMod.LOGGER.info("[BalanceConfig] 难度已切换为: {}", difficulty);
    }
    
    // === 难度乘数 ===
    
    public static double getFactorExtractionRate() {
        return factorExtractionRate;
    }
    
    public static double getFactorConsumptionRate() {
        return factorConsumptionRate;
    }
    
    public static double getQuestRewardMultiplier() {
        return questRewardMultiplier;
    }
    
    public static double getMachineEfficiency() {
        return machineEfficiency;
    }
    
    public static double getTideNegativeEffect() {
        return tideNegativeEffect;
    }
    
    // === Factor 系统 ===
    
    public static double getBaseExtractionRate() {
        return getDouble("factor", "base_extraction_rate", 10.0);
    }
    
    public static double getBaseDiffusionCoefficient() {
        return getDouble("factor", "base_diffusion_coefficient", 0.1);
    }
    
    public static double getMaxConcentration() {
        return getDouble("factor", "max_concentration", 100.0);
    }
    
    public static double getDecayRate() {
        return getDouble("factor", "decay_rate", 0.001);
    }
    
    public static int getSpreadRadius() {
        return getInt("factor", "spread_radius", 3);
    }
    
    // === 维度 ===
    
    public static double getDimensionBaseFactor(String dimension) {
        String key = dimension.replace("minecraft:", "");
        if (config.has("dimensions") && config.getAsJsonObject("dimensions").has(key)) {
            JsonObject dim = config.getAsJsonObject("dimensions").getAsJsonObject(key);
            return getDouble(dim, "base_factor", 0.5);
        }
        return 0.5;
    }
    
    public static int getDimensionTideCycleDays(String dimension) {
        String key = dimension.replace("minecraft:", "");
        if (config.has("dimensions") && config.getAsJsonObject("dimensions").has(key)) {
            JsonObject dim = config.getAsJsonObject("dimensions").getAsJsonObject(key);
            return getInt(dim, "tide_cycle_days", 8);
        }
        return 8;
    }
    
    // === 机器 ===
    
    public static int getMachineFactorCapacity(String machineType, int tier) {
        return getMachineArrayValue(machineType, "factor_capacity", tier, 100);
    }
    
    public static int getMachineProcessingTime(String machineType, int tier) {
        return getMachineArrayValue(machineType, "processing_time_ticks", tier, 100);
    }
    
    public static int getExtractorMinConcentration() {
        if (config.has("machines") && config.getAsJsonObject("machines").has("extractor")) {
            JsonObject extractor = config.getAsJsonObject("machines").getAsJsonObject("extractor");
            return getInt(extractor, "min_concentration_required", 20);
        }
        return 20;
    }
    
    public static int getTransmitterMaxDistance(int tier) {
        return getMachineArrayValue("transmitter", "max_distance", tier, 16);
    }
    
    public static double getTransmitterEfficiency(int tier) {
        if (tier < 1 || tier > 4) tier = 1;
        if (config.has("machines") && config.getAsJsonObject("machines").has("transmitter")) {
            JsonObject transmitter = config.getAsJsonObject("machines").getAsJsonObject("transmitter");
            if (transmitter.has("efficiency")) {
                var arr = transmitter.getAsJsonArray("efficiency");
                if (arr.size() >= tier) {
                    return arr.get(tier - 1).getAsDouble();
                }
            }
        }
        return 0.75;
    }
    
    // === 任务 ===
    
    public static int getBaseExperienceReward() {
        return getInt("quests", "base_experience_reward", 100);
    }
    
    public static int getBaseFactorReward() {
        return getInt("quests", "base_factor_reward", 50);
    }
    
    public static int getDailyQuestCount() {
        return getInt("quests", "daily_quest_count", 3);
    }
    
    // === 武器 ===
    
    public static double getWeaponDamageBonus(int tier) {
        return getArrayValue("weapons", "factor_damage_bonus", tier, 0.2);
    }
    
    public static int getWeaponBaseDamage(int tier) {
        return (int) getArrayValue("weapons", "base_damage", tier, 6);
    }
    
    public static double getWeaponAttackSpeed(int tier) {
        return getArrayValue("weapons", "attack_speed", tier, -2.4);
    }
    
    // === 材料 ===
    
    public static int getMaterialFactorValue(int tier) {
        return (int) getArrayValue("materials", "factor_value", tier, 10);
    }
    
    public static int getMaterialTraitSlots(int tier) {
        return (int) getArrayValue("materials", "trait_slots", tier, 1);
    }
    
    // === 辅助方法 ===
    
    private static double getDouble(String section, String key, double defaultValue) {
        if (config.has(section) && config.getAsJsonObject(section).has(key)) {
            return config.getAsJsonObject(section).get(key).getAsDouble();
        }
        return defaultValue;
    }
    
    private static double getDouble(JsonObject obj, String key, double defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsDouble();
        }
        return defaultValue;
    }
    
    private static int getInt(String section, String key, int defaultValue) {
        if (config.has(section) && config.getAsJsonObject(section).has(key)) {
            return config.getAsJsonObject(section).get(key).getAsInt();
        }
        return defaultValue;
    }
    
    private static int getInt(JsonObject obj, String key, int defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsInt();
        }
        return defaultValue;
    }
    
    private static double getArrayValue(String section, String key, int tier, double defaultValue) {
        if (tier < 1 || tier > 5) tier = 1;
        if (config.has(section) && config.getAsJsonObject(section).has(key)) {
            var arr = config.getAsJsonObject(section).getAsJsonArray(key);
            if (arr.size() >= tier) {
                return arr.get(tier - 1).getAsDouble();
            }
        }
        return defaultValue;
    }
    
    private static int getMachineArrayValue(String machineType, String key, int tier, int defaultValue) {
        if (tier < 1 || tier > 5) tier = 1;
        if (config.has("machines") && config.getAsJsonObject("machines").has(machineType)) {
            JsonObject machine = config.getAsJsonObject("machines").getAsJsonObject(machineType);
            if (machine.has(key)) {
                var arr = machine.getAsJsonArray(key);
                if (arr.size() >= tier) {
                    return arr.get(tier - 1).getAsInt();
                }
            }
        }
        return defaultValue;
    }
    
    /**
     * 热重载配置
     */
    public static void reload() {
        FactorCraftMod.LOGGER.info("[BalanceConfig] 重载配置...");
        loadConfig();
        FactorCraftMod.LOGGER.info("[BalanceConfig] 配置重载完成");
    }
    
    /**
     * 获取原始配置（用于调试）
     */
    public static JsonObject getRawConfig() {
        return config;
    }
}