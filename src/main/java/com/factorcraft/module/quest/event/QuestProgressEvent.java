package com.factorcraft.module.quest.event;

import com.factorcraft.module.quest.model.QuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 任务进度更新事件
 */
public class QuestProgressEvent extends QuestEvent {
    
    private final int conditionIndex;
    private final float oldProgress;
    private final float newProgress;
    
    public QuestProgressEvent(QuestData questData, PlayerEntity player, 
                              int conditionIndex, float oldProgress, float newProgress) {
        super(questData, player);
        this.conditionIndex = conditionIndex;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
    }
    
    public int getConditionIndex() {
        return conditionIndex;
    }
    
    public float getOldProgress() {
        return oldProgress;
    }
    
    public float getNewProgress() {
        return newProgress;
    }
    
    /**
     * 检查条件是否刚完成
     */
    public boolean isJustCompleted() {
        return oldProgress < 1.0f && newProgress >= 1.0f;
    }
}