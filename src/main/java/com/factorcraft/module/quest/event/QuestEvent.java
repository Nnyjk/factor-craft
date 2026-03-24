package com.factorcraft.module.quest.event;

import com.factorcraft.module.quest.model.QuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 任务事件基类
 */
public abstract class QuestEvent {
    
    private final QuestData questData;
    private final PlayerEntity player;
    
    protected QuestEvent(QuestData questData, PlayerEntity player) {
        this.questData = questData;
        this.player = player;
    }
    
    public QuestData getQuestData() {
        return questData;
    }
    
    public PlayerEntity getPlayer() {
        return player;
    }
    
    public Identifier getQuestId() {
        return questData.getQuestId();
    }
}