package com.factorcraft.module.gear;

/**
 * 装备强化等级枚举
 * 
 * T1-T5 对应不同材料等级和能力强度
 */
public enum GearUpgradeLevel {
    /** T1: 基础等级 */
    T1(1, "基础", 1.0f),
    
    /** T2: 进阶等级 */
    T2(2, "进阶", 1.5f),
    
    /** T3: 高级等级 */
    T3(3, "高级", 2.0f),
    
    /** T4: 专家等级 */
    T4(4, "专家", 2.5f),
    
    /** T5: 大师等级 */
    T5(5, "大师", 3.0f);
    
    private final int level;
    private final String displayName;
    private final float multiplier;
    
    GearUpgradeLevel(int level, String displayName, float multiplier) {
        this.level = level;
        this.displayName = displayName;
        this.multiplier = multiplier;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public float getMultiplier() {
        return this.multiplier;
    }
    
    /**
     * 根据等级数值获取枚举
     */
    public static GearUpgradeLevel fromLevel(int level) {
        for (GearUpgradeLevel e : values()) {
            if (e.level == level) {
                return e;
            }
        }
        return T1;
    }
    
    /**
     * 根据 tier 数值获取枚举（别名）
     */
    public static GearUpgradeLevel fromTier(int tier) {
        return fromLevel(tier);
    }
}
