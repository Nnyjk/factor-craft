package com.factorcraft.module.quest.instance;

import com.factorcraft.module.quest.template.QuestTemplate;
import com.factorcraft.module.quest.condition.QuestCondition;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

/**
 * 任务实例 - 玩家的任务进度追踪
 */
public class QuestInstance {
    
    private final QuestTemplate template;
    private final UUID playerId;
    private final long startTime;
    private float[] conditionProgress;
    
    public QuestInstance(QuestTemplate template, UUID playerId) {
        this.template = template;
        this.playerId = playerId;
        this.startTime = System.currentTimeMillis();
        this.conditionProgress = new float[template.getConditions().size()];
        
        // 初始化进度
        for (int i = 0; i < conditionProgress.length; i++) {
            conditionProgress[i] = 0.0f;
        }
    }
    
    /**
     * 更新任务进度
     */
    public void updateProgress(PlayerEntity player, Object context) {
        for (int i = 0; i < template.getConditions().size(); i++) {
            QuestCondition condition = template.getConditions().get(i);
            // 实现条件进度更新逻辑
            conditionProgress[i] = condition.getProgress(player, context);
        }
    }
    
    /**
     * 检查任务是否完成
     */
    public boolean isCompleted() {
        for (int i = 0; i < template.getConditions().size(); i++) {
            if (conditionProgress[i] < 1.0f) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 获取总体进度 (0.0-1.0)
     */
    public float getOverallProgress() {
        if (template.getConditions().isEmpty()) {
            return 1.0f;
        }
        
        float total = 0.0f;
        for (float progress : conditionProgress) {
            total += progress;
        }
        return total / template.getConditions().size();
    }
    
    public QuestTemplate getTemplate() { return template; }
    public UUID getPlayerId() { return playerId; }
    public long getStartTime() { return startTime; }
    public float[] getConditionProgress() { return conditionProgress.clone(); }
}
