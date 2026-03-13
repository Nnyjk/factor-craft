package com.factorcraft.module.quest.reward;

/**
 * 任务奖励类型枚举
 */
public enum QuestRewardType {
    
    ITEM("物品奖励", "item"),
    EXPERIENCE("经验奖励", "exp"),
    FACTOR("Factor 奖励", "factor"),
    TECHNOLOGY("科技解锁", "tech"),
    ACHIEVEMENT("成就解锁", "achievement");
    
    private final String displayName;
    private final String serializedName;
    
    QuestRewardType(String displayName, String serializedName) {
        this.displayName = displayName;
        this.serializedName = serializedName;
    }
    
    public String getDisplayName() { return displayName; }
    public String getSerializedName() { return serializedName; }
    
    public static QuestRewardType fromSerializedName(String name) {
        for (QuestRewardType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
