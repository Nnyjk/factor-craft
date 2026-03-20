package com.factorcraft.module.profession.model;

/**
 * 职业类型枚举
 * 
 * 三大核心职业体系，与Factor系统深度绑定
 */
public enum ProfessionType {
    
    /**
     * 创生师 - 生产建造核心、团队辅助
     * 核心关键词：创生
     * 核心属性：Factor亲和度、建造速度、作物生长倍率、资源产出倍率
     */
    GENESIS("genesis", "创生师", "genesis_factor"),
    
    /**
     * 湮灭使 - 战斗探索核心、资源采集
     * 核心关键词：湮灭
     * 核心属性：攻击力、暴击率、Factor伤害加成、探索幸运值
     */
    ANNIHILATION("annihilation", "湮灭使", "annihilation_factor"),
    
    /**
     * 锻铸匠 - 加工制造核心、装备强化
     * 核心关键词：锻铸
     * 核心属性：加工效率、装备品质、Factor转化效率、合成成功率
     */
    FORGE("forge", "锻铸匠", "forge_factor");
    
    private final String id;
    private final String displayName;
    private final String factorType;
    
    ProfessionType(String id, String displayName, String factorType) {
        this.id = id;
        this.displayName = displayName;
        this.factorType = factorType;
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
    
    public static ProfessionType fromId(String id) {
        for (ProfessionType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return null;
    }
}