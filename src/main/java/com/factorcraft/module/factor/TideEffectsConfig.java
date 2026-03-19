package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.config.ConfigManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * 潮汐效果配置
 * 
 * 从 tide_effects.json 加载效果配置，支持热重载
 * 提供效果值查询和启用/禁用控制
 */
public class TideEffectsConfig {
    
    private static final String CONFIG_NAME = "tide_effects";
    private static TideEffectsConfig INSTANCE;
    
    // 全局开关
    private boolean enabled = true;
    
    // 各状态效果配置
    private final Map<TideStatus, StatusConfig> statusConfigs = new EnumMap<>(TideStatus.class);
    
    // 视觉配置
    private boolean showHudStatus = true;
    private String hudPosition = "top_left";
    private boolean particleEffects = true;
    
    /**
     * 状态效果配置
     */
    public static class StatusConfig {
        public final double machineEfficiency;
        public final double extractionEfficiency;
        public final double creatureSpawnModifier;
        public final List<EffectEntry> playerEffects;
        public final double effectChance;
        public final boolean overloadRisk;
        
        public StatusConfig(double machineEfficiency, double extractionEfficiency, 
                           double creatureSpawnModifier, List<EffectEntry> playerEffects,
                           double effectChance, boolean overloadRisk) {
            this.machineEfficiency = machineEfficiency;
            this.extractionEfficiency = extractionEfficiency;
            this.creatureSpawnModifier = creatureSpawnModifier;
            this.playerEffects = playerEffects;
            this.effectChance = effectChance;
            this.overloadRisk = overloadRisk;
        }
    }
    
    /**
     * 玩家效果条目
     */
    public record EffectEntry(String effectId, int amplifier, int duration) {
        public static EffectEntry parse(String str) {
            String[] parts = str.split(":");
            if (parts.length < 3) return null;
            try {
                return new EffectEntry(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
    
    private TideEffectsConfig() {
        // 初始化默认值
        initDefaults();
        load();
    }
    
    /**
     * 初始化默认配置
     */
    private void initDefaults() {
        statusConfigs.put(TideStatus.DEPLETED, new StatusConfig(
            -0.50, -0.75, -0.25,
            List.of(new EffectEntry("slowness", 1, 200), new EffectEntry("weakness", 1, 200)),
            0.1, false
        ));
        
        statusConfigs.put(TideStatus.LOW_ENERGY, new StatusConfig(
            -0.25, -0.50, -0.20,
            List.of(),
            0.2, false
        ));
        
        statusConfigs.put(TideStatus.STABLE, new StatusConfig(
            0.0, 0.0, 0.0,
            List.of(),
            0.0, false
        ));
        
        statusConfigs.put(TideStatus.HIGH_ENERGY, new StatusConfig(
            0.25, 0.50, 0.20,
            List.of(new EffectEntry("speed", 0, 200), new EffectEntry("haste", 0, 200)),
            0.3, false
        ));
        
        statusConfigs.put(TideStatus.OVERLOAD, new StatusConfig(
            0.50, 1.00, 0.30,
            List.of(new EffectEntry("speed", 1, 200), new EffectEntry("haste", 1, 200), new EffectEntry("nausea", 0, 100)),
            0.5, true
        ));
    }
    
    /**
     * 从配置文件加载
     */
    public void load() {
        try {
            JsonObject root = ConfigManager.getConfig(CONFIG_NAME);
            if (root == null) {
                FactorCraftMod.LOGGER.warn("[TideEffects] 配置文件未找到，使用默认值");
                return;
            }
            
            // 全局开关
            if (root.has("enabled")) {
                this.enabled = root.get("enabled").getAsBoolean();
            }
            
            // 加载各状态配置
            if (root.has("effects")) {
                JsonObject effects = root.getAsJsonObject("effects");
                loadStatusConfig(effects, "depleted", TideStatus.DEPLETED);
                loadStatusConfig(effects, "low_energy", TideStatus.LOW_ENERGY);
                loadStatusConfig(effects, "stable", TideStatus.STABLE);
                loadStatusConfig(effects, "high_energy", TideStatus.HIGH_ENERGY);
                loadStatusConfig(effects, "overload", TideStatus.OVERLOAD);
            }
            
            // 视觉配置
            if (root.has("visual")) {
                JsonObject visual = root.getAsJsonObject("visual");
                if (visual.has("show_hud_status")) {
                    this.showHudStatus = visual.get("show_hud_status").getAsBoolean();
                }
                if (visual.has("hud_position")) {
                    this.hudPosition = visual.get("hud_position").getAsString();
                }
                if (visual.has("particle_effects")) {
                    this.particleEffects = visual.get("particle_effects").getAsBoolean();
                }
            }
            
            FactorCraftMod.LOGGER.info("[TideEffects] 配置加载完成，启用状态: {}", enabled);
            
        } catch (Exception e) {
            FactorCraftMod.LOGGER.error("[TideEffects] 加载配置失败", e);
        }
    }
    
    private void loadStatusConfig(JsonObject effects, String key, TideStatus status) {
        if (!effects.has(key)) return;
        
        JsonObject obj = effects.getAsJsonObject(key);
        
        double machineEff = getDouble(obj, "machine_efficiency", 
            statusConfigs.get(status).machineEfficiency);
        double extractEff = getDouble(obj, "extraction_efficiency", 
            statusConfigs.get(status).extractionEfficiency);
        double spawnMod = getDouble(obj, "creature_spawn_modifier", 
            statusConfigs.get(status).creatureSpawnModifier);
        double chance = getDouble(obj, "effect_chance", 
            statusConfigs.get(status).effectChance);
        boolean overload = getBoolean(obj, "overload_risk", 
            statusConfigs.get(status).overloadRisk);
        
        List<EffectEntry> playerEffects = new ArrayList<>();
        if (obj.has("player_effects")) {
            JsonArray arr = obj.getAsJsonArray("player_effects");
            for (JsonElement el : arr) {
                EffectEntry entry = EffectEntry.parse(el.getAsString());
                if (entry != null) {
                    playerEffects.add(entry);
                }
            }
        }
        
        statusConfigs.put(status, new StatusConfig(
            machineEff, extractEff, spawnMod, playerEffects, chance, overload
        ));
    }
    
    private double getDouble(JsonObject obj, String key, double defaultValue) {
        return obj.has(key) ? obj.get(key).getAsDouble() : defaultValue;
    }
    
    private boolean getBoolean(JsonObject obj, String key, boolean defaultValue) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : defaultValue;
    }
    
    // ==================== 静态访问方法 ====================
    
    public static TideEffectsConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TideEffectsConfig();
        }
        return INSTANCE;
    }
    
    /**
     * 热重载配置
     */
    public static void reload() {
        if (INSTANCE != null) {
            INSTANCE.load();
        } else {
            getInstance();
        }
    }
    
    /**
     * 检查潮汐效果是否启用
     */
    public static boolean isEnabled() {
        return getInstance().enabled;
    }
    
    /**
     * 获取状态配置
     */
    public static StatusConfig getConfig(TideStatus status) {
        return getInstance().statusConfigs.get(status);
    }
    
    /**
     * 获取机器效率修正值
     */
    public static double getMachineEfficiency(TideStatus status) {
        if (!isEnabled()) return 0.0;
        return getConfig(status).machineEfficiency;
    }
    
    /**
     * 获取提取效率修正值
     */
    public static double getExtractionEfficiency(TideStatus status) {
        if (!isEnabled()) return 0.0;
        return getConfig(status).extractionEfficiency;
    }
    
    /**
     * 获取生物生成修正值
     */
    public static double getCreatureSpawnModifier(TideStatus status) {
        if (!isEnabled()) return 0.0;
        return getConfig(status).creatureSpawnModifier;
    }
    
    /**
     * 获取玩家效果列表
     */
    public static List<EffectEntry> getPlayerEffects(TideStatus status) {
        if (!isEnabled()) return List.of();
        return getConfig(status).playerEffects;
    }
    
    /**
     * 获取效果触发概率
     */
    public static double getEffectChance(TideStatus status) {
        if (!isEnabled()) return 0.0;
        return getConfig(status).effectChance;
    }
    
    /**
     * 是否有过载风险
     */
    public static boolean hasOverloadRisk(TideStatus status) {
        if (!isEnabled()) return false;
        return getConfig(status).overloadRisk;
    }
    
    // ==================== 视觉配置 ====================
    
    public static boolean showHudStatus() {
        return getInstance().showHudStatus;
    }
    
    public static String getHudPosition() {
        return getInstance().hudPosition;
    }
    
    public static boolean hasParticleEffects() {
        return getInstance().particleEffects;
    }
}