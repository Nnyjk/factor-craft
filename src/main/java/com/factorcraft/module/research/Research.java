package com.factorcraft.module.research;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 研究节点定义
 * 
 * 代表科技树中的一个研究节点
 */
public class Research {
    
    public enum Type {
        TECHNOLOGY,   // 技术研究 - 解锁新机器
        EFFICIENCY,   // 效率研究 - 提升机器效率
        CAPACITY,     // 容量研究 - 增加存储上限
        APPLICATION,  // 应用研究 - 解锁新用途
        ULTIMATE      // 终极研究 - 顶级科技
    }
    
    public enum State {
        LOCKED,       // 已锁定 - 条件未满足
        AVAILABLE,    // 可研究 - 条件已满足
        IN_PROGRESS,  // 研究中
        COMPLETED     // 已完成
    }
    
    private final String id;
    private final String name;
    private final String description;
    private final Type type;
    private final int researchTime;  // 游戏内时间（ticks）
    
    // 前置研究
    private final List<String> prerequisites;
    
    // 解锁条件
    private final Map<String, Integer> factorCosts;  // factor_type -> amount
    private final Map<Item, Integer> itemRequirements;  // item -> count
    private final List<String> requiredQuests;
    
    // 效果
    private final Map<String, Object> effects;
    
    // UI 位置
    private final int treeX;
    private final int treeY;
    private final String category;
    
    private Research(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.researchTime = builder.researchTime;
        this.prerequisites = new ArrayList<>(builder.prerequisites);
        this.factorCosts = new HashMap<>(builder.factorCosts);
        this.itemRequirements = new HashMap<>(builder.itemRequirements);
        this.requiredQuests = new ArrayList<>(builder.requiredQuests);
        this.effects = new HashMap<>(builder.effects);
        this.treeX = builder.treeX;
        this.treeY = builder.treeY;
        this.category = builder.category;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Type getType() { return type; }
    public int getResearchTime() { return researchTime; }
    public List<String> getPrerequisites() { return prerequisites; }
    public Map<String, Integer> getFactorCosts() { return factorCosts; }
    public Map<Item, Integer> getItemRequirements() { return itemRequirements; }
    public List<String> getRequiredQuests() { return requiredQuests; }
    public Map<String, Object> getEffects() { return effects; }
    public int getTreeX() { return treeX; }
    public int getTreeY() { return treeY; }
    public String getCategory() { return category; }
    
    /**
     * 从 JSON 解析研究定义
     */
    public static Research fromJson(JsonObject json) {
        Builder builder = new Builder()
            .id(json.get("id").getAsString())
            .name(json.get("name").getAsString())
            .description(json.get("description").getAsString())
            .type(Type.valueOf(json.get("type").getAsString().toUpperCase()))
            .researchTime(json.has("research_time") ? json.get("research_time").getAsInt() : 6000);
        
        // 前置研究
        if (json.has("prerequisites")) {
            for (var elem : json.getAsJsonArray("prerequisites")) {
                builder.addPrerequisite(elem.getAsString());
            }
        }
        
        // Factor 消耗
        if (json.has("factor_costs")) {
            JsonObject costs = json.getAsJsonObject("factor_costs");
            for (var entry : costs.entrySet()) {
                builder.addFactorCost(entry.getKey(), entry.getValue().getAsInt());
            }
        }
        
        // 物品要求
        if (json.has("item_requirements")) {
            JsonObject items = json.getAsJsonObject("item_requirements");
            for (var entry : items.entrySet()) {
                Identifier itemId = Identifier.of(entry.getKey());
                Item item = Registries.ITEM.get(itemId);
                builder.addItemRequirement(item, entry.getValue().getAsInt());
            }
        }
        
        // 任务要求
        if (json.has("required_quests")) {
            for (var elem : json.getAsJsonArray("required_quests")) {
                builder.addRequiredQuest(elem.getAsString());
            }
        }
        
        // 效果
        if (json.has("effects")) {
            JsonObject effects = json.getAsJsonObject("effects");
            for (var entry : effects.entrySet()) {
                builder.addEffect(entry.getKey(), entry.getValue().getAsString());
            }
        }
        
        // UI 位置
        if (json.has("ui")) {
            JsonObject ui = json.getAsJsonObject("ui");
            builder.treeX(ui.get("x").getAsInt());
            builder.treeY(ui.get("y").getAsInt());
            builder.category(ui.has("category") ? ui.get("category").getAsString() : "general");
        }
        
        return builder.build();
    }
    
    public static class Builder {
        private String id;
        private String name;
        private String description;
        private Type type = Type.TECHNOLOGY;
        private int researchTime = 6000;  // 5分钟
        private List<String> prerequisites = new ArrayList<>();
        private Map<String, Integer> factorCosts = new HashMap<>();
        private Map<Item, Integer> itemRequirements = new HashMap<>();
        private List<String> requiredQuests = new ArrayList<>();
        private Map<String, Object> effects = new HashMap<>();
        private int treeX = 0;
        private int treeY = 0;
        private String category = "general";
        
        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(Type type) { this.type = type; return this; }
        public Builder researchTime(int ticks) { this.researchTime = ticks; return this; }
        public Builder addPrerequisite(String researchId) { prerequisites.add(researchId); return this; }
        public Builder addFactorCost(String factorType, int amount) { factorCosts.put(factorType, amount); return this; }
        public Builder addItemRequirement(Item item, int count) { itemRequirements.put(item, count); return this; }
        public Builder addRequiredQuest(String questId) { requiredQuests.add(questId); return this; }
        public Builder addEffect(String key, Object value) { effects.put(key, value); return this; }
        public Builder treeX(int x) { this.treeX = x; return this; }
        public Builder treeY(int y) { this.treeY = y; return this; }
        public Builder category(String category) { this.category = category; return this; }
        
        public Research build() {
            return new Research(this);
        }
    }
}