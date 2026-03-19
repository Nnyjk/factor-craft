package com.factorcraft.module.integration.config;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 非核心内容集成配置。
 * 从 JSON 文件加载，定义第三方模组集成规则。
 */
public record IntegrationConfig(
    @SerializedName("enabled")
    boolean enabled,
    
    @SerializedName("auto_detect")
    boolean autoDetect,
    
    @SerializedName("check_interval_ticks")
    int checkIntervalTicks,
    
    @SerializedName("whitelist")
    List<String> whitelist,
    
    @SerializedName("blacklist")
    List<String> blacklist,
    
    @SerializedName("content_definitions")
    List<ContentDefinition> contentDefinitions,
    
    @SerializedName("compat_rules")
    List<CompatRule> compatRules
) {
    public static final IntegrationConfig DEFAULT = new IntegrationConfig(
        true,
        true,
        6000, // 5 minutes (20 ticks/sec)
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    );
    
    /**
     * 内容定义：描述如何将 Factor 特性应用 to 外部物品。
     */
    public record ContentDefinition(
        @SerializedName("item_id")
        String itemId,
        
        @SerializedName("category")
        String category,
        
        @SerializedName("tier_window")
        TierWindowConfig tierWindow,
        
        @SerializedName("conductivity_cost")
        int conductivityCost,
        
        @SerializedName("tags")
        List<String> tags,
        
        // Tool-specific
        @SerializedName("harvest_speed")
        Float harvestSpeed,
        
        @SerializedName("durability")
        Integer durability,
        
        @SerializedName("harvest_tier")
        Integer harvestTier,
        
        // Weapon-specific
        @SerializedName("attack_damage")
        Float attackDamage,
        
        @SerializedName("attack_speed")
        Float attackSpeed,
        
        @SerializedName("armor_pierce")
        Float armorPierce,
        
        // Armor-specific
        @SerializedName("helmet_armor")
        Integer helmetArmor,
        
        @SerializedName("chest_armor")
        Integer chestArmor,
        
        @SerializedName("leggings_armor")
        Integer leggingsArmor,
        
        @SerializedName("boots_armor")
        Integer bootsArmor,
        
        @SerializedName("knockback_resistance")
        Float knockbackResistance,
        
        // Furniture-specific
        @SerializedName("zone_tag")
        String zoneTag,
        
        @SerializedName("comfort_bonus")
        Integer comfortBonus,
        
        @SerializedName("utility_bonus")
        Integer utilityBonus
    ) {}
    
    public record TierWindowConfig(
        @SerializedName("min_tier")
        int minTier,
        
        @SerializedName("max_tier")
        int maxTier
    ) {}
    
    /**
     * 兼容性规则：定义与特定模组的交互方式。
     */
    public record CompatRule(
        @SerializedName("mod_id")
        String modId,
        
        @SerializedName("action")
        String action, // "allow", "deny", "modify"
        
        @SerializedName("modifications")
        List<Modification> modifications
    ) {}
    
    public record Modification(
        @SerializedName("item_id")
        String itemId,
        
        @SerializedName("property")
        String property,
        
        @SerializedName("value")
        Object value
    ) {}
}