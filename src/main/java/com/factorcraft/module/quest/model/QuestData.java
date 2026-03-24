package com.factorcraft.module.quest.model;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * 任务数据模型
 * 
 * 存储玩家的任务状态和进度信息
 */
public class QuestData {
    
    private final Identifier questId;
    private final UUID playerId;
    private QuestState state;
    private long acceptTime;
    private long completeTime;
    private float[] conditionProgress;
    private boolean[] conditionsCompleted;
    
    public enum QuestState {
        AVAILABLE("可接取"),
        ACTIVE("进行中"),
        COMPLETED("已完成"),
        TURNED_IN("已领取奖励"),
        FAILED("失败"),
        LOCKED("锁定");
        
        private final String displayName;
        
        QuestState(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public QuestData(Identifier questId, UUID playerId) {
        this.questId = questId;
        this.playerId = playerId;
        this.state = QuestState.AVAILABLE;
        this.acceptTime = 0;
        this.completeTime = 0;
        this.conditionProgress = new float[0];
        this.conditionsCompleted = new boolean[0];
    }
    
    /**
     * 接取任务
     */
    public void accept(int conditionCount) {
        this.state = QuestState.ACTIVE;
        this.acceptTime = System.currentTimeMillis();
        this.conditionProgress = new float[conditionCount];
        this.conditionsCompleted = new boolean[conditionCount];
    }
    
    /**
     * 完成任务
     */
    public void complete() {
        this.state = QuestState.COMPLETED;
        this.completeTime = System.currentTimeMillis();
    }
    
    /**
     * 领取奖励
     */
    public void turnIn() {
        this.state = QuestState.TURNED_IN;
    }
    
    /**
     * 更新条件进度
     */
    public void updateProgress(int index, float progress) {
        if (index >= 0 && index < conditionProgress.length) {
            conditionProgress[index] = Math.min(1.0f, Math.max(0.0f, progress));
            if (conditionProgress[index] >= 1.0f) {
                conditionsCompleted[index] = true;
            }
        }
    }
    
    /**
     * 检查所有条件是否完成
     */
    public boolean isAllConditionsCompleted() {
        for (boolean completed : conditionsCompleted) {
            if (!completed) {
                return false;
            }
        }
        return conditionsCompleted.length > 0;
    }
    
    /**
     * 获取总体进度 (0.0 - 1.0)
     */
    public float getOverallProgress() {
        if (conditionProgress.length == 0) {
            return 0.0f;
        }
        float total = 0.0f;
        for (float progress : conditionProgress) {
            total += progress;
        }
        return total / conditionProgress.length;
    }
    
    // ==================== NBT 序列化 ====================
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("questId", questId.toString());
        nbt.putString("playerId", playerId.toString());
        nbt.putString("state", state.name());
        nbt.putLong("acceptTime", acceptTime);
        nbt.putLong("completeTime", completeTime);
        
        // 保存条件进度
        float[] progress = conditionProgress;
        boolean[] completed = conditionsCompleted;
        for (int i = 0; i < progress.length; i++) {
            nbt.putFloat("progress_" + i, progress[i]);
            nbt.putBoolean("completed_" + i, completed[i]);
        }
        nbt.putInt("conditionCount", progress.length);
        
        return nbt;
    }
    
    public static QuestData fromNbt(NbtCompound nbt) {
        Identifier questId = Identifier.of(nbt.getString("questId"));
        UUID playerId = UUID.fromString(nbt.getString("playerId"));
        
        QuestData data = new QuestData(questId, playerId);
        data.state = QuestState.valueOf(nbt.getString("state"));
        data.acceptTime = nbt.getLong("acceptTime");
        data.completeTime = nbt.getLong("completeTime");
        
        // 加载条件进度
        int count = nbt.getInt("conditionCount");
        data.conditionProgress = new float[count];
        data.conditionsCompleted = new boolean[count];
        for (int i = 0; i < count; i++) {
            data.conditionProgress[i] = nbt.getFloat("progress_" + i);
            data.conditionsCompleted[i] = nbt.getBoolean("completed_" + i);
        }
        
        return data;
    }
    
    // ==================== Getters ====================
    
    public Identifier getQuestId() {
        return questId;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public QuestState getState() {
        return state;
    }
    
    public long getAcceptTime() {
        return acceptTime;
    }
    
    public long getCompleteTime() {
        return completeTime;
    }
    
    public float[] getConditionProgress() {
        return conditionProgress;
    }
    
    public boolean[] getConditionsCompleted() {
        return conditionsCompleted;
    }
}