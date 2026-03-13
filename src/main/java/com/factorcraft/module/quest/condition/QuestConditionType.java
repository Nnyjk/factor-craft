package com.factorcraft.module.quest.condition;

/**
 * 任务条件类型枚举
 */
public enum QuestConditionType {
    
    // 基础条件 (PR #36)
    ITEM_PICKUP("获得物品", "pickup"),
    ITEM_CRAFT("合成物品", "craft"),
    ITEM_SUBMIT("提交物品", "submit"),
    ITEM_USE("使用物品", "use"),
    
    // 进阶条件 (PR #37)
    ENTITY_KILL("击杀怪物", "kill"),
    BLOCK_PLACE("放置方块", "place"),
    DIMENSION_TRAVEL("维度传输", "travel"),
    FACTOR_ABSORB("Factor 吸收", "absorb"),
    
    // 复合条件 (PR #37)
    COMPOSITE("复合条件", "composite");
    
    private final String displayName;
    private final String serializedName;
    
    QuestConditionType(String displayName, String serializedName) {
        this.displayName = displayName;
        this.serializedName = serializedName;
    }
    
    public String getDisplayName() { return displayName; }
    public String getSerializedName() { return serializedName; }
    
    public static QuestConditionType fromSerializedName(String name) {
        for (QuestConditionType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
