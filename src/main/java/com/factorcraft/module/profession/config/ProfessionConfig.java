package com.factorcraft.module.profession.config;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * 职业配置数据类
 * 支持JSON数据驱动
 */
public class ProfessionConfig {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("display_name")
    private String displayName;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("factor_type")
    private String factorType;
    
    @SerializedName("base_attributes")
    private Map<String, Double> baseAttributes;
    
    @SerializedName("unlock_conditions")
    private UnlockConditions unlockConditions;
    
    public static class UnlockConditions {
        @SerializedName("min_level")
        private int minLevel = 0;
        
        @SerializedName("required_items")
        private Map<String, Integer> requiredItems;
        
        @SerializedName("required_factor_count")
        private int requiredFactorCount = 0;
        
        public int getMinLevel() { return minLevel; }
        public Map<String, Integer> getRequiredItems() { return requiredItems; }
        public int getRequiredFactorCount() { return requiredFactorCount; }
    }
    
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getFactorType() { return factorType; }
    public Map<String, Double> getBaseAttributes() { return baseAttributes; }
    public UnlockConditions getUnlockConditions() { return unlockConditions; }
}