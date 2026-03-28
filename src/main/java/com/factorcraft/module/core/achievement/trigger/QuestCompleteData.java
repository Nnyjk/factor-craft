package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 任务完成触发器数据
 */
public class QuestCompleteData {
    private final String questId;
    private final String questCategory;
    private final boolean isMainQuest;
    
    public QuestCompleteData(String questId, String questCategory, boolean isMainQuest) {
        this.questId = questId;
        this.questCategory = questCategory;
        this.isMainQuest = isMainQuest;
    }
    
    public String getQuestId() {
        return questId;
    }
    
    public String getQuestCategory() {
        return questCategory;
    }
    
    public boolean isMainQuest() {
        return isMainQuest;
    }
}
