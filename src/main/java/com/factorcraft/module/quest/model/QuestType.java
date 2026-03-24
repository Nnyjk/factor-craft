package com.factorcraft.module.quest.model;

/**
 * 任务类型枚举
 * 
 * 定义任务的分类，影响任务的行为和显示方式
 */
public enum QuestType {
    
    /**
     * 主线任务 - 推进游戏核心进度
     */
    MAIN("主线", "main", true, false),
    
    /**
     * 支线任务 - 可选的额外内容
     */
    SIDE("支线", "side", false, false),
    
    /**
     * 日常任务 - 每日重置
     */
    DAILY("日常", "daily", false, true),
    
    /**
     * 周常任务 - 每周重置
     */
    WEEKLY("周常", "weekly", false, true),
    
    /**
     * 成就任务 - 特殊里程碑
     */
    ACHIEVEMENT("成就", "achievement", true, false),
    
    /**
     * 隐藏任务 - 特殊触发条件
     */
    HIDDEN("隐藏", "hidden", false, false);
    
    private final String displayName;
    private final String id;
    private final boolean required;
    private final boolean resettable;
    
    QuestType(String displayName, String id, boolean required, boolean resettable) {
        this.displayName = displayName;
        this.id = id;
        this.required = required;
        this.resettable = resettable;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getId() {
        return id;
    }
    
    /**
     * 是否为必须完成的主线任务
     */
    public boolean isRequired() {
        return required;
    }
    
    /**
     * 是否可以重置（日常/周常）
     */
    public boolean isResettable() {
        return resettable;
    }
    
    /**
     * 根据 ID 获取任务类型
     */
    public static QuestType fromId(String id) {
        for (QuestType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return SIDE; // 默认为支线任务
    }
}