package com.factorcraft.module.quest.config;

import com.factorcraft.module.quest.model.QuestType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务配置数据结构
 * 
 * 用于从 JSON 加载任务模板配置
 */
public class QuestConfig {
    
    private String id;
    private String type = "side";
    private String title;
    private String description;
    private List<String> prerequisites = new ArrayList<>();
    private List<ConditionConfig> conditions = new ArrayList<>();
    private List<RewardConfig> rewards = new ArrayList<>();
    private boolean repeatable = false;
    private boolean hidden = false;
    private int sortOrder = 100;
    
    /**
     * 条件配置
     */
    public static class ConditionConfig {
        private String type;
        private String target;
        private int amount = 1;
        private String description;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 奖励配置
     */
    public static class RewardConfig {
        private String type;
        private String item;
        private int amount = 1;
        private int experience = 0;
        private String technology;
        private String factor;
        private int factorAmount = 1;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getItem() { return item; }
        public void setItem(String item) { this.item = item; }
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
        public int getExperience() { return experience; }
        public void setExperience(int experience) { this.experience = experience; }
        public String getTechnology() { return technology; }
        public void setTechnology(String technology) { this.technology = technology; }
        public String getFactor() { return factor; }
        public void setFactor(String factor) { this.factor = factor; }
        public int getFactorAmount() { return factorAmount; }
        public void setFactorAmount(int factorAmount) { this.factorAmount = factorAmount; }
    }
    
    // ==================== Getters & Setters ====================
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public QuestType getType() { return QuestType.fromId(type); }
    public void setType(String type) { this.type = type; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }
    
    public List<ConditionConfig> getConditions() { return conditions; }
    public void setConditions(List<ConditionConfig> conditions) { this.conditions = conditions; }
    
    public List<RewardConfig> getRewards() { return rewards; }
    public void setRewards(List<RewardConfig> rewards) { this.rewards = rewards; }
    
    public boolean isRepeatable() { return repeatable; }
    public void setRepeatable(boolean repeatable) { this.repeatable = repeatable; }
    
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    
    /**
     * 获取任务标识符
     */
    public Identifier getIdentifier() {
        return Identifier.of(id);
    }
}