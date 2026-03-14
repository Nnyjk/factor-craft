package com.factorcraft.module.factor;

/**
 * 潮汐状态枚举
 * 
 * 表示当前 Factor 相对于基准值的偏离程度
 * 
 * TODO: 后续可添加具体游戏效果
 */
public enum TideStatus {
    /** 稳定：偏离 ±10% 以内 */
    STABLE(0.0, "stable"),
    
    /** 偏离：偏离 ±10-30% */
    DEVIATED(0.05, "deviated"),
    
    /** 波动：偏离 ±30-50% */
    FLUCTUATING(0.15, "fluctuating"),
    
    /** 剧烈：偏离 ±50% 以上 */
    VOLATILE(0.30, "volatile");

    private final double baseEffectChance;
    private final String name;

    TideStatus(double baseEffectChance, String name) {
        this.baseEffectChance = baseEffectChance;
        this.name = name;
    }

    /**
     * 基础效果触发概率
     */
    public double baseEffectChance() {
        return baseEffectChance;
    }
    
    /**
     * 状态名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 是否为稳定状态
     */
    public boolean isStable() {
        return this == STABLE;
    }
    
    /**
     * 是否需要触发效果
     */
    public boolean shouldTriggerEffects() {
        return this != STABLE;
    }
}