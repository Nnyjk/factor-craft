package com.factorcraft.module.profession.talent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 天赋节点
 * 
 * 天赋树中的单个天赋点，包含效果列表
 */
public class TalentNode {
    
    private final String id;
    private final String displayName;
    private final String description;
    private final TalentBranch branch;
    private final int tier; // 天赋层级 (1-5)
    private final int maxLevel;
    private final List<TalentEffect> effects;
    private int currentLevel;
    
    public TalentNode(String id, String displayName, String description, 
                      TalentBranch branch, int tier, int maxLevel) {
        this(id, displayName, description, branch, tier, maxLevel, Collections.emptyList());
    }
    
    public TalentNode(String id, String displayName, String description, 
                      TalentBranch branch, int tier, int maxLevel, List<TalentEffect> effects) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.branch = branch;
        this.tier = tier;
        this.maxLevel = maxLevel;
        this.effects = new ArrayList<>(effects);
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
    
    public List<TalentEffect> getEffects() {
        return Collections.unmodifiableList(effects);
    }
    
    public boolean hasEffects() {
        return !effects.isEmpty();
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
     * 设置当前等级
     */
    public void setCurrentLevel(int level) {
        this.currentLevel = Math.max(0, Math.min(level, maxLevel));
    }
    
    /**
     * 重置等级
     */
    public void reset() {
        this.currentLevel = 0;
    }
    
    /**
     * 获取指定效果类型的效果值
     * 
     * @param effectType 效果类型
     * @return 当前等级的效果值，如果效果不存在返回0
     */
    public float getEffectValue(TalentEffectType effectType) {
        for (TalentEffect effect : effects) {
            if (effect.getType() == effectType) {
                return effect.getValueForLevel(currentLevel);
            }
        }
        return 0;
    }
    
    /**
     * 获取所有效果的当前值
     * 
     * @return 效果类型 -> 效果值的映射
     */
    public java.util.Map<TalentEffectType, Float> getAllEffectValues() {
        java.util.Map<TalentEffectType, Float> values = new java.util.EnumMap<>(TalentEffectType.class);
        for (TalentEffect effect : effects) {
            values.put(effect.getType(), effect.getValueForLevel(currentLevel));
        }
        return values;
    }
    
    /**
     * 格式化效果描述
     */
    public String formatEffects() {
        if (effects.isEmpty()) {
            return "无效果";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < effects.size(); i++) {
            if (i > 0) sb.append(", ");
            TalentEffect effect = effects.get(i);
            sb.append(effect.getType().getDisplayName())
              .append(": ")
              .append(effect.formatValue(currentLevel > 0 ? currentLevel : 1));
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("TalentNode[%s: %s, tier=%d, level=%d/%d]", 
            id, displayName, tier, currentLevel, maxLevel);
    }
}