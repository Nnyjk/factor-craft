package com.factorcraft.module.loot;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.config.ConfigManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * 怪物掉落配置
 * 
 * 从 mob_drops.json 加载掉落配置，支持热重载
 */
public class MobDropsConfig {
    
    private static final String CONFIG_NAME = "mob_drops";
    private static MobDropsConfig INSTANCE;
    
    // 全局开关
    private boolean enabled = true;
    private double globalMultiplier = 1.0;
    
    // 默认掉落配置
    private DropConfig defaultDrop = new DropConfig(0.1, 1, 2, 1, 2);
    
    // 生物掉落配置: mobId -> dropId -> config
    private final Map<String, Map<String, DropConfig>> mobDrops = new HashMap<>();
    
    // Boss 掉落配置: bossId -> dropId -> config
    private final Map<String, Map<String, DropConfig>> bossDrops = new HashMap<>();
    
    // Factor 浓度加成
    private ConcentrationBonus concentrationBonus = new ConcentrationBonus();
    
    /**
     * 掉落配置
     */
    public record DropConfig(double chance, int min, int max, int tier, int tierRange) {
        public static DropConfig fromJson(JsonObject obj, DropConfig defaults) {
            double chance = obj.has("chance") ? obj.get("chance").getAsDouble() : defaults.chance;
            int min = obj.has("min") ? obj.get("min").getAsInt() : defaults.min;
            int max = obj.has("max") ? obj.get("max").getAsInt() : defaults.max;
            int tier = obj.has("tier") ? obj.get("tier").getAsInt() : defaults.tier;
            int tierRange = obj.has("tier_range") ? obj.get("tier_range").getAsInt() : 0;
            return new DropConfig(chance, min, max, tier, tierRange);
        }
    }
    
    /**
     * Factor 浓度加成配置
     */
    public static class ConcentrationBonus {
        public boolean enabled = true;
        public int lowThreshold = 20;
        public int mediumThreshold = 40;
        public int highThreshold = 60;
        public int veryHighThreshold = 80;
        public double lowBonus = 0.1;
        public double mediumBonus = 0.2;
        public double highBonus = 0.35;
        public double veryHighBonus = 0.5;
        
        public double getBonus(double concentration) {
            if (!enabled) return 0;
            if (concentration >= veryHighThreshold) return veryHighBonus;
            if (concentration >= highThreshold) return highBonus;
            if (concentration >= mediumThreshold) return mediumBonus;
            if (concentration >= lowThreshold) return lowBonus;
            return 0;
        }
    }
    
    private MobDropsConfig() {
        load();
    }
    
    /**
     * 从配置文件加载
     */
    public void load() {
        try {
            JsonObject root = ConfigManager.getConfig(CONFIG_NAME);
            if (root == null) {
                FactorCraftMod.LOGGER.warn("[MobDrops] 配置文件未找到，使用默认值");
                initDefaults();
                return;
            }
            
            // 全局设置
            if (root.has("enabled")) {
                this.enabled = root.get("enabled").getAsBoolean();
            }
            if (root.has("global_multiplier")) {
                this.globalMultiplier = root.get("global_multiplier").getAsDouble();
            }
            
            // 默认掉落
            if (root.has("default_drops")) {
                JsonObject defaults = root.getAsJsonObject("default_drops");
                this.defaultDrop = parseDefaultDrop(defaults);
            }
            
            // 生物掉落
            if (root.has("mob_drops")) {
                JsonObject mobs = root.getAsJsonObject("mob_drops");
                for (String mobId : mobs.keySet()) {
                    mobDrops.put(mobId, parseDrops(mobs.getAsJsonObject(mobId)));
                }
            }
            
            // Boss 掉落
            if (root.has("boss_drops")) {
                JsonObject bosses = root.getAsJsonObject("boss_drops");
                for (String bossId : bosses.keySet()) {
                    bossDrops.put(bossId, parseDrops(bosses.getAsJsonObject(bossId)));
                }
            }
            
            // 浓度加成
            if (root.has("factor_concentration_bonus")) {
                JsonObject bonus = root.getAsJsonObject("factor_concentration_bonus");
                parseConcentrationBonus(bonus);
            }
            
            FactorCraftMod.LOGGER.info("[MobDrops] 配置加载完成，启用状态: {}, {} 种生物, {} 种Boss", 
                enabled, mobDrops.size(), bossDrops.size());
            
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[MobDrops] 加载配置失败", e);
            initDefaults();
        }
    }
    
    private void initDefaults() {
        // 默认僵尸掉落
        Map<String, DropConfig> zombieDrops = new HashMap<>();
        zombieDrops.put("factor_shard", new DropConfig(0.08, 1, 2, 1, 0));
        mobDrops.put("minecraft:zombie", zombieDrops);
    }
    
    private DropConfig parseDefaultDrop(JsonObject defaults) {
        if (defaults.has("factor_shard")) {
            return DropConfig.fromJson(defaults.getAsJsonObject("factor_shard"), 
                new DropConfig(0.1, 1, 2, 1, 2));
        }
        return new DropConfig(0.1, 1, 2, 1, 2);
    }
    
    private Map<String, DropConfig> parseDrops(JsonObject drops) {
        Map<String, DropConfig> result = new HashMap<>();
        for (String dropId : drops.keySet()) {
            JsonObject dropConfig = drops.getAsJsonObject(dropId);
            result.put(dropId, DropConfig.fromJson(dropConfig, defaultDrop));
        }
        return result;
    }
    
    private void parseConcentrationBonus(JsonObject bonus) {
        concentrationBonus.enabled = getBool(bonus, "enabled", true);
        concentrationBonus.lowThreshold = getInt(bonus, "low_threshold", 20);
        concentrationBonus.mediumThreshold = getInt(bonus, "medium_threshold", 40);
        concentrationBonus.highThreshold = getInt(bonus, "high_threshold", 60);
        concentrationBonus.veryHighThreshold = getInt(bonus, "very_high_threshold", 80);
        concentrationBonus.lowBonus = getDouble(bonus, "low_bonus", 0.1);
        concentrationBonus.mediumBonus = getDouble(bonus, "medium_bonus", 0.2);
        concentrationBonus.highBonus = getDouble(bonus, "high_bonus", 0.35);
        concentrationBonus.veryHighBonus = getDouble(bonus, "very_high_bonus", 0.5);
    }
    
    private boolean getBool(JsonObject obj, String key, boolean def) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : def;
    }
    
    private int getInt(JsonObject obj, String key, int def) {
        return obj.has(key) ? obj.get(key).getAsInt() : def;
    }
    
    private double getDouble(JsonObject obj, String key, double def) {
        return obj.has(key) ? obj.get(key).getAsDouble() : def;
    }
    
    // ==================== 静态访问方法 ====================
    
    public static MobDropsConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MobDropsConfig();
        }
        return INSTANCE;
    }
    
    public static void reload() {
        if (INSTANCE != null) {
            INSTANCE.load();
        } else {
            getInstance();
        }
    }
    
    public static boolean isEnabled() {
        return getInstance().enabled;
    }
    
    public static double getGlobalMultiplier() {
        return getInstance().globalMultiplier;
    }
    
    /**
     * 获取生物掉落配置
     */
    public static Map<String, DropConfig> getMobDrops(String mobId) {
        // 检查是否为 Boss
        if (getInstance().bossDrops.containsKey(mobId)) {
            return getInstance().bossDrops.get(mobId);
        }
        return getInstance().mobDrops.getOrDefault(mobId, Map.of());
    }
    
    /**
     * 检查是否为 Boss
     */
    public static boolean isBoss(String mobId) {
        return getInstance().bossDrops.containsKey(mobId);
    }
    
    /**
     * 获取浓度加成
     */
    public static double getConcentrationBonus(double concentration) {
        return getInstance().concentrationBonus.getBonus(concentration);
    }
    
    /**
     * 获取所有配置的生物 ID
     */
    public static Set<String> getConfiguredMobs() {
        Set<String> result = new HashSet<>();
        result.addAll(getInstance().mobDrops.keySet());
        result.addAll(getInstance().bossDrops.keySet());
        return result;
    }
}