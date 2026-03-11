package com.factorcraft.module.material.config;

import com.factorcraft.module.material.model.TraitCategory;
import com.factorcraft.module.material.model.TraitEffect;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 特性配置解析器
 * 
 * 解析 traits.json 配置文件，返回特性定义列表
 */
public class TraitsConfigParser {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * 特性定义（完整版）
     * 包含所有 traits.json 中的字段
     */
    public static class TraitDefinition {
        private final String id;
        private final String name;
        private final String type;
        private final TraitCategory category;
        private final String description;
        private final List<TraitEffect> effects;
        private final int maxLevel;
        private final double levelScaling;
        private final Map<String, Object> resonance;
        private final Set<String> incompatible;
        private final double weight;
        private final int[] tierRange;
        
        public TraitDefinition(String id, String name, String type, TraitCategory category,
                             String description, List<TraitEffect> effects, int maxLevel,
                             double levelScaling, Map<String, Object> resonance,
                             Set<String> incompatible, double weight, int[] tierRange) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.category = category;
            this.description = description;
            this.effects = effects;
            this.maxLevel = maxLevel;
            this.levelScaling = levelScaling;
            this.resonance = resonance;
            this.incompatible = incompatible;
            this.weight = weight;
            this.tierRange = tierRange;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public TraitCategory getCategory() { return category; }
        public String getDescription() { return description; }
        public List<TraitEffect> getEffects() { return effects; }
        public int getMaxLevel() { return maxLevel; }
        public double getLevelScaling() { return levelScaling; }
        public Map<String, Object> getResonance() { return resonance; }
        public Set<String> getIncompatible() { return incompatible; }
        public double getWeight() { return weight; }
        public int[] getTierRange() { return tierRange; }
    }
    
    /**
     * 解析 traits.json 文件
     * 
     * @param configPath 配置文件路径
     * @return 特性定义列表
     * @throws IOException 文件读取错误
     */
    public List<TraitDefinition> parse(Path configPath) throws IOException {
        List<TraitDefinition> traits = new ArrayList<>();
        
        try (FileReader reader = new FileReader(configPath.toFile())) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            JsonArray traitsArray = root.getAsJsonArray("traits");
            
            for (JsonElement element : traitsArray) {
                JsonObject traitObj = element.getAsJsonObject();
                TraitDefinition def = parseTraitDefinition(traitObj);
                traits.add(def);
            }
        }
        
        return traits;
    }
    
    private TraitDefinition parseTraitDefinition(JsonObject obj) {
        String id = obj.get("id").getAsString();
        String name = obj.get("name").getAsString();
        String type = obj.get("type").getAsString();
        String categoryStr = obj.get("category").getAsString();
        String description = obj.get("description").getAsString();
        
        // Convert category string to enum
        TraitCategory category = parseCategory(categoryStr);
        
        // Parse effects
        List<TraitEffect> effects = new ArrayList<>();
        if (obj.has("effects")) {
            for (JsonElement effectElem : obj.getAsJsonArray("effects")) {
                effects.add(GSON.fromJson(effectElem, TraitEffect.class));
            }
        }
        
        // Parse other fields
        int maxLevel = obj.has("max_level") ? obj.get("max_level").getAsInt() : 1;
        double levelScaling = obj.has("level_scaling") ? obj.get("level_scaling").getAsDouble() : 0.0;
        double weight = obj.has("weight") ? obj.get("weight").getAsDouble() : 100.0;
        
        // Parse resonance
        Map<String, Object> resonance = new HashMap<>();
        if (obj.has("resonance")) {
            JsonObject resonanceObj = obj.getAsJsonObject("resonance");
            for (String key : resonanceObj.keySet()) {
                resonance.put(key, GSON.fromJson(resonanceObj.get(key), Object.class));
            }
        }
        
        // Parse incompatible
        Set<String> incompatible = new HashSet<>();
        if (obj.has("incompatible")) {
            for (JsonElement incElem : obj.getAsJsonArray("incompatible")) {
                incompatible.add(incElem.getAsString());
            }
        }
        
        // Parse tier_range
        int[] tierRange = {1, 5};
        if (obj.has("tier_range")) {
            JsonArray rangeArr = obj.getAsJsonArray("tier_range");
            tierRange[0] = rangeArr.get(0).getAsInt();
            tierRange[1] = rangeArr.get(1).getAsInt();
        }
        
        return new TraitDefinition(
            id, name, type, category, description, effects, maxLevel,
            levelScaling, resonance, incompatible, weight, tierRange
        );
    }
    
    private TraitCategory parseCategory(String categoryStr) {
        return switch (categoryStr.toLowerCase()) {
            case "extraction" -> TraitCategory.EXTRACTION;
            case "transfer" -> TraitCategory.TRANSFER;
            case "production" -> TraitCategory.PRODUCTION;
            case "environment" -> TraitCategory.ENVIRONMENT;
            case "negative" -> TraitCategory.NEGATIVE;
            default -> TraitCategory.EXTRACTION; // 默认归入提取类
        };
    }
}