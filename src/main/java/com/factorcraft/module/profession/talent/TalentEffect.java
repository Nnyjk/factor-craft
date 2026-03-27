package com.factorcraft.module.profession.talent;

/**
 * 天赋效果
 * 
 * 表示天赋节点的具体效果
 */
public class TalentEffect {
    
    private final TalentEffectType type;
    private final float baseValue; // 基础效果值
    private final float perLevelValue; // 每级增加的效果值
    
    /**
     * 创建天赋效果
     * 
     * @param type 效果类型
     * @param baseValue 基础值
     * @param perLevelValue 每级增加值
     */
    public TalentEffect(TalentEffectType type, float baseValue, float perLevelValue) {
        this.type = type;
        this.baseValue = baseValue;
        this.perLevelValue = perLevelValue;
    }
    
    /**
     * 创建天赋效果（显式指定是否为百分比）
     * 
     * 注意：isPercentage 参数仅用于辅助显示，实际百分比逻辑由 TalentEffectType 决定
     * 
     * @param type 效果类型
     * @param baseValue 基础值
     * @param perLevelValue 每级增加值
     * @param isPercentage 是否为百分比效果（用于显示格式化）
     */
    public TalentEffect(TalentEffectType type, float baseValue, float perLevelValue, boolean isPercentage) {
        // isPercentage 参数会被忽略，因为我们使用 TalentEffectType.isPercentage() 来判断
        this.type = type;
        this.baseValue = baseValue;
        this.perLevelValue = perLevelValue;
    }
    
    /**
     * 创建百分比效果 (便捷方法)
     */
    public static TalentEffect percentage(TalentEffectType type, float basePercent, float perLevelPercent) {
        return new TalentEffect(type, basePercent, perLevelPercent);
    }
    
    /**
     * 创建绝对值效果 (便捷方法)
     */
    public static TalentEffect absolute(TalentEffectType type, float baseValue, float perLevelValue) {
        return new TalentEffect(type, baseValue, perLevelValue);
    }
    
    /**
     * 获取指定等级的效果值
     * 
     * @param level 天赋等级 (1-based)
     * @return 效果值
     */
    public float getValueForLevel(int level) {
        if (level <= 0) return 0;
        return baseValue + (perLevelValue * (level - 1));
    }
    
    /**
     * 获取最大等级的效果值
     * 
     * @param maxLevel 最大天赋等级
     * @return 最大效果值
     */
    public float getMaxValue(int maxLevel) {
        return getValueForLevel(maxLevel);
    }
    
    public TalentEffectType getType() {
        return type;
    }
    
    public float getBaseValue() {
        return baseValue;
    }
    
    public float getPerLevelValue() {
        return perLevelValue;
    }
    
    /**
     * 格式化效果描述
     */
    public String formatValue(int level) {
        float value = getValueForLevel(level);
        if (type.isPercentage()) {
            return String.format("%.1f%%", value);
        } else {
            // 对于整数值，不显示小数
            if (value == Math.floor(value)) {
                return String.format("%.0f", value);
            }
            return String.format("%.1f", value);
        }
    }
    
    @Override
    public String toString() {
        return String.format("TalentEffect[%s: base=%.1f, perLevel=%.1f]", 
            type.getId(), baseValue, perLevelValue);
    }
}