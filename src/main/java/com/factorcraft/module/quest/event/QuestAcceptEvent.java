package com.factorcraft.module.quest.event;

import com.factorcraft.module.quest.model.QuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 任务接取事件
 */
public class QuestAcceptEvent extends QuestEvent {
    
    private final Identifier questId;
    private boolean cancelled = false;
    
    public QuestAcceptEvent(QuestData questData, PlayerEntity player) {
        super(questData, player);
        this.questId = questData.getQuestId();
    }
    
    public Identifier getQuestId() {
        return questId;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    /**
     * 任务接取前置事件 - 可取消
     */
    public static class Pre extends QuestAcceptEvent {
        public Pre(QuestData questData, PlayerEntity player) {
            super(questData, player);
        }
    }
    
    /**
     * 任务接取后置事件 - 不可取消
     */
    public static class Post extends QuestAcceptEvent {
        public Post(QuestData questData, PlayerEntity player) {
            super(questData, player);
        }
    }
}