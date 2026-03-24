package com.factorcraft.module.quest.event;

import com.factorcraft.module.quest.model.QuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 任务完成事件
 */
public class QuestCompleteEvent extends QuestEvent {
    
    private final Identifier questId;
    private final long completionTime;
    
    public QuestCompleteEvent(QuestData questData, PlayerEntity player) {
        super(questData, player);
        this.questId = questData.getQuestId();
        this.completionTime = System.currentTimeMillis();
    }
    
    public Identifier getQuestId() {
        return questId;
    }
    
    public long getCompletionTime() {
        return completionTime;
    }
    
    /**
     * 任务完成前置事件
     */
    public static class Pre extends QuestCompleteEvent {
        public Pre(QuestData questData, PlayerEntity player) {
            super(questData, player);
        }
    }
    
    /**
     * 任务完成后置事件
     */
    public static class Post extends QuestCompleteEvent {
        public Post(QuestData questData, PlayerEntity player) {
            super(questData, player);
        }
    }
}