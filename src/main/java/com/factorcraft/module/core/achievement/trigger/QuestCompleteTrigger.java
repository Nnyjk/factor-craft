package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 任务完成触发器
 * 监听任务完成事件
 */
public class QuestCompleteTrigger implements AchievementTrigger<QuestCompleteData> {
    
    private final String id;
    private final String questId;
    private final String category;
    private final Boolean mainQuestOnly;
    
    public QuestCompleteTrigger(String id, String questId, String category, Boolean mainQuestOnly) {
        this.id = id;
        this.questId = questId;
        this.category = category;
        this.mainQuestOnly = mainQuestOnly;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean matches(QuestCompleteData data) {
        // 检查任务 ID 是否匹配（空表示任意任务）
        if (questId != null && !questId.equals(data.getQuestId())) {
            return false;
        }
        // 检查类别是否匹配（空表示任意类别）
        if (category != null && !category.equals(data.getQuestCategory())) {
            return false;
        }
        // 检查是否只限主线任务
        if (mainQuestOnly != null && mainQuestOnly && !data.isMainQuest()) {
            return false;
        }
        return true;
    }
    
    @Override
    public int trigger(ServerPlayerEntity player, QuestCompleteData data) {
        // 完成任务返回固定进度 1
        return 1;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.QUEST_COMPLETE;
    }
}
