package com.factorcraft.module.profession.talent;

/**
 * 天赋效果类型枚举
 * 
 * 定义所有可能的天赋效果类型
 */
public enum TalentEffectType {
    
    // ==================== 工程师效果 ====================
    
    /** 机器工作速度提升 (百分比) */
    MACHINE_SPEED("machine_speed", "机器速度提升", true),
    
    /** Factor消耗降低 (百分比) */
    FACTOR_COST_REDUCTION("factor_cost_reduction", "Factor消耗降低", true),
    
    /** 产量效率提升 (百分比) */
    OUTPUT_EFFICIENCY("output_efficiency", "产量效率", true),
    
    /** 管道传输速度 (百分比) */
    PIPE_SPEED("pipe_speed", "管道速度", true),
    
    /** 自动合成效率 (百分比) */
    AUTO_CRAFT_EFFICIENCY("auto_craft_efficiency", "自动合成效率", true),
    
    /** 远程控制范围 (绝对值) */
    REMOTE_CONTROL_RANGE("remote_control_range", "远程控制范围", false),
    
    /** Factor存储上限提升 (绝对值) */
    FACTOR_STORAGE("factor_storage", "Factor存储提升", false),
    
    /** Factor传输速度提升 (百分比) */
    FACTOR_TRANSFER_RATE("factor_transfer_rate", "Factor传输速度", true),
    
    /** 电池容量提升 (百分比) */
    BATTERY_CAPACITY("battery_capacity", "电池容量提升", true),
    
    // ==================== 培育师效果 ====================
    
    /** 生物生长速度提升 (百分比) */
    GROWTH_SPEED("growth_speed", "生长速度", true),
    
    /** 变异概率提升 (百分比) */
    MUTATION_CHANCE("mutation_chance", "变异概率", true),
    
    /** 稀有掉落率提升 (百分比) */
    RARE_DROP_CHANCE("rare_drop_chance", "稀有掉落率", true),
    
    /** 自动养殖效率 (百分比) */
    AUTO_FARM_EFFICIENCY("auto_farm_efficiency", "自动养殖效率", true),
    
    /** 批量培育数量 (绝对值) */
    BATCH_BREED_COUNT("batch_breed_count", "批量培育数量", false),
    
    /** 监控范围 (百分比) */
    MONITOR_RANGE("monitor_range", "监控范围", true),
    
    /** Factor产出效率 (百分比) */
    FACTOR_PRODUCTION("factor_production", "Factor产出效率", true),
    
    /** Factor转化效率 (百分比) */
    FACTOR_CONVERT_EFFICIENCY("factor_convert_efficiency", "Factor转化效率", true),
    
    /** Factor恢复速度 (百分比) */
    FACTOR_REGEN_RATE("factor_regen_rate", "Factor恢复速度", true),
    
    // ==================== 探险家效果 ====================
    
    /** Factor赋能效果 (百分比) */
    FACTOR_EMPOWERMENT("factor_empowerment", "Factor赋能", true),
    
    /** 攻击力提升 (百分比) */
    ATTACK_DAMAGE("attack_damage", "攻击力", true),
    
    /** 技能伤害提升 (百分比) */
    SKILL_DAMAGE("skill_damage", "技能伤害", true),
    
    /** 暴击率 (百分比) */
    CRITICAL_CHANCE("critical_chance", "暴击率", true),
    
    /** Factor感知范围 (绝对值) */
    FACTOR_SENSE_RANGE("factor_sense_range", "Factor感知范围", false),
    
    /** 遗迹探测范围 (绝对值) */
    RUIN_DETECTION_RANGE("ruin_detection_range", "遗迹探测范围", false),
    
    /** 稀有资源发现概率 (百分比) */
    RARE_RESOURCE_CHANCE("rare_resource_chance", "稀有资源概率", true),
    
    /** 潮汐抗性 (百分比) */
    TIDE_RESISTANCE("tide_resistance", "潮汐抗性", true),
    
    /** 生命恢复速度 (百分比) */
    HEALTH_REGEN("health_regen", "生命恢复速度", true),
    
    /** 环境伤害降低 (百分比) */
    ENVIRONMENT_DAMAGE_REDUCTION("environment_damage_reduction", "环境伤害降低", true),
    
    // ==================== 因子掌控者效果 ====================
    
    /** 跨职业效率加成 (百分比) */
    CROSS_CLASS_EFFICIENCY("cross_class_efficiency", "跨职业效率", true),
    
    /** 跨职业培育加成 (百分比) */
    CROSS_CLASS_BREEDING("cross_class_breeding", "跨职业培育", true),
    
    /** 跨职业战斗加成 (百分比) */
    CROSS_CLASS_COMBAT("cross_class_combat", "跨职业战斗", true),
    
    /** 多职业加成 (百分比) */
    MULTI_CLASS_BONUS("multi_class_bonus", "多职业加成", true),
    
    /** 全属性提升 (百分比) */
    ALL_STATS_BONUS("all_stats_bonus", "全属性提升", true),
    
    /** 技能效果提升 (百分比) */
    SKILL_EFFECTIVENESS("skill_effectiveness", "技能效果", true),
    
    /** 技能冷却降低 (百分比) */
    COOLDOWN_REDUCTION("cooldown_reduction", "技能冷却降低", true),
    
    /** 终极技能威力 (百分比) */
    ULTIMATE_SKILL_POWER("ultimate_skill_power", "终极技能威力", true);
    
    private final String id;
    private final String displayName;
    private final boolean isPercentage; // true=百分比, false=绝对值
    
    TalentEffectType(String id, String displayName, boolean isPercentage) {
        this.id = id;
        this.displayName = displayName;
        this.isPercentage = isPercentage;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isPercentage() {
        return isPercentage;
    }
    
    /**
     * 根据ID获取效果类型
     */
    public static TalentEffectType fromId(String id) {
        for (TalentEffectType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}