package com.factorcraft.module.core.achievement.trigger;

/**
 * 成就触发器类型枚举
 * 定义 5 种基础触发器类型
 */
public enum TriggerType {
    /**
     * Factor 生产触发器
     * 监听 Factor 晶体生产、提纯等事件
     */
    FACTOR_PRODUCTION("factor_production"),
    
    /**
     * 机器制作触发器
     * 监听机器设备制作事件
     */
    MACHINE_CRAFT("machine_craft"),
    
    /**
     * 任务完成触发器
     * 监听任务完成事件
     */
    QUEST_COMPLETE("quest_complete"),
    
    /**
     * Boss 击杀触发器
     * 监听 Boss 实体死亡事件
     */
    BOSS_KILL("boss_kill"),
    
    /**
     * 探索发现触发器
     * 监听维度传送、结构发现等事件
     */
    EXPLORATION("exploration");
    
    private final String id;
    
    TriggerType(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
}
