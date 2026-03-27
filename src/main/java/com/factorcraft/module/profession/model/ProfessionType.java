package com.factorcraft.module.profession.model;

/**
 * 职业类型枚举
 * 
 * 四大职业体系，与Factor系统深度绑定
 * 
 * 基础职业（开局可选）：
 * - ENGINEER: Factor工程师
 * - CULTIVATOR: 能量培育师
 * - EXPLORER: 潮汐探索者
 * 
 * 隐藏职业（特殊条件解锁）：
 * - MASTER: 因子掌控者
 */
public enum ProfessionType {
    
    /**
     * Factor工程师 - 生产建造核心、自动化工厂
     * 核心关键词：效率、自动化、Factor产量
     * 核心属性：机器工作速度、Factor消耗降低、产量提升、自动化范围
     * 天赋分支：效率、自动化、能量
     */
    ENGINEER("engineer", "Factor工程师", "engineer_factor", false),
    
    /**
     * 能量培育师 - 生物养成核心、变异培育
     * 核心关键词：培育、变异、生物产出
     * 核心属性：生物生长速度、变异概率、稀有掉落率、Factor产出效率
     * 天赋分支：培育、养殖、能量
     */
    CULTIVATOR("cultivator", "能量培育师", "cultivator_factor", false),
    
    /**
     * 潮汐探索者 - 冒险战斗核心、遗迹探索
     * 核心关键词：战斗、探索、潮汐
     * 核心属性：攻击力、暴击率、探索幸运值、潮汐抗性
     * 天赋分支：战斗、探索、生存
     */
    EXPLORER("explorer", "潮汐探索者", "explorer_factor", false),
    
    /**
     * 因子掌控者 - 全能型隐藏职业
     * 解锁条件：完成主线任务"因子融合"，3个基础职业均达到10级
     * 核心能力：可融合三个基础职业的能力，自定义天赋组合
     * 天赋分支：全能融合
     */
    MASTER("master", "因子掌控者", "master_factor", true);
    
    private final String id;
    private final String displayName;
    private final String factorType;
    private final boolean hidden;
    
    ProfessionType(String id, String displayName, String factorType, boolean hidden) {
        this.id = id;
        this.displayName = displayName;
        this.factorType = factorType;
        this.hidden = hidden;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getFactorType() {
        return factorType;
    }
    
    /**
     * 是否为隐藏职业
     */
    public boolean isHidden() {
        return hidden;
    }
    
    /**
     * 根据ID获取职业类型
     */
    public static ProfessionType fromId(String id) {
        for (ProfessionType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 获取所有基础职业（非隐藏）
     */
    public static ProfessionType[] getBasicProfessions() {
        return new ProfessionType[] { ENGINEER, CULTIVATOR, EXPLORER };
    }
    
    /**
     * 获取职业描述
     */
    public String getDescription() {
        return switch (this) {
            case ENGINEER -> "专注机器效率提升、Factor产量优化、自动化工厂搭建";
            case CULTIVATOR -> "专注Factor生物培育、变异生物养殖、特殊道具产出";
            case EXPLORER -> "专注Factor能量利用、战斗能力提升、遗迹探索";
            case MASTER -> "可融合三个基础职业的能力，自定义天赋组合";
        };
    }
    
    /**
     * 获取职业核心属性标签
     */
    public String[] getCoreTags() {
        return switch (this) {
            case ENGINEER -> new String[] {"效率", "自动化", "Factor产量"};
            case CULTIVATOR -> new String[] {"培育", "变异", "生物产出"};
            case EXPLORER -> new String[] {"战斗", "探索", "潮汐"};
            case MASTER -> new String[] {"全能", "融合", "自定义"};
        };
    }
}