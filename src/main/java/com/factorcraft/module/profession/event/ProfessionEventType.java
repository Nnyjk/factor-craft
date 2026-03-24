package com.factorcraft.module.profession.event;

/**
 * 职业事件类型枚举
 */
public enum ProfessionEventType {
    /**
     * 职业选择事件
     */
    PROFESSION_SELECT,
    
    /**
     * 职业升级事件
     */
    LEVEL_UP,
    
    /**
     * 经验获取事件
     */
    EXPERIENCE_GAIN,
    
    /**
     * 属性生效事件
     */
    ATTRIBUTE_APPLY,
    
    /**
     * 技能使用事件
     */
    SKILL_USE
}