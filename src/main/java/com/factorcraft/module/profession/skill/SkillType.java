package com.factorcraft.module.profession.skill;

/**
 * 技能类型
 */
public enum SkillType {
    ACTIVE("active", "主动技能"),
    PASSIVE("passive", "被动技能");
    
    private final String id;
    private final String displayName;
    
    SkillType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}