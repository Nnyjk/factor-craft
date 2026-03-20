package com.factorcraft.module.profession.talent;

/**
 * 天赋节点
 * 
 * 天赋树中的单个天赋点
 */
public class TalentNode {
    
    private final String id;
    private final String displayName;
    private final String description;
    private final TalentBranch branch;
    private final int tier; // 天赋层级 (1-5)
    private final int maxLevel;
    private int currentLevel;
    
    public TalentNode(String id, String displayName, String description, 
                      TalentBranch branch, int tier, int maxLevel) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.branch = branch;
        this.tier = tier;
        this.maxLevel = maxLevel;
        this.currentLevel = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public TalentBranch getBranch() {
        return branch;
    }
    
    public int getTier() {
        return tier;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public boolean isUnlocked() {
        return currentLevel > 0;
    }
    
    public boolean canLevelUp() {
        return currentLevel < maxLevel;
    }
    
    public boolean levelUp() {
        if (canLevelUp()) {
            currentLevel++;
            return true;
        }
        return false;
    }
    
    /**
     * 获取天赋效果值
     * 通常根据当前等级计算
     */
    public float getEffectValue() {
        return currentLevel * 0.1f; // 简单示例：每级10%效果
    }
}