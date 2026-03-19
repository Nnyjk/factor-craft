package com.factorcraft.module.research;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 玩家研究进度追踪
 * 
 * 存储玩家的研究状态和进度
 */
public class ResearchProgress {
    
    private final UUID playerId;
    
    // 已完成的研究
    private final Set<String> completedResearch;
    
    // 正在进行的研究 -> 开始时间（游戏 tick）
    private final Map<String, Long> inProgressResearch;
    
    // 研究总时间累计（用于统计）
    private long totalResearchTime = 0;
    
    // 已解锁的效果缓存
    private final Set<String> unlockedEffects;
    
    public ResearchProgress(UUID playerId) {
        this.playerId = playerId;
        this.completedResearch = new HashSet<>();
        this.inProgressResearch = new HashMap<>();
        this.unlockedEffects = new HashSet<>();
    }
    
    // Getters
    public UUID getPlayerId() { return playerId; }
    public Set<String> getCompletedResearch() { return completedResearch; }
    public Map<String, Long> getInProgressResearch() { return inProgressResearch; }
    public long getTotalResearchTime() { return totalResearchTime; }
    
    /**
     * 检查研究是否已完成
     */
    public boolean isCompleted(String researchId) {
        return completedResearch.contains(researchId);
    }
    
    /**
     * 检查研究是否正在进行
     */
    public boolean isInProgress(String researchId) {
        return inProgressResearch.containsKey(researchId);
    }
    
    /**
     * 开始研究
     */
    public void startResearch(String researchId, long startTick) {
        inProgressResearch.put(researchId, startTick);
    }
    
    /**
     * 完成研究
     */
    public void completeResearch(String researchId, Research research) {
        inProgressResearch.remove(researchId);
        completedResearch.add(researchId);
        totalResearchTime += research.getResearchTime();
        
        // 缓存解锁效果
        for (var effect : research.getEffects().keySet()) {
            unlockedEffects.add(effect);
        }
    }
    
    /**
     * 取消研究
     */
    public void cancelResearch(String researchId) {
        inProgressResearch.remove(researchId);
    }
    
    /**
     * 获取研究进度（0.0 - 1.0）
     */
    public float getProgress(String researchId, long currentTick, Research research) {
        if (!inProgressResearch.containsKey(researchId)) {
            return isCompleted(researchId) ? 1.0f : 0.0f;
        }
        
        long startTick = inProgressResearch.get(researchId);
        long elapsed = currentTick - startTick;
        int totalTime = research.getResearchTime();
        
        return Math.min(1.0f, (float) elapsed / totalTime);
    }
    
    /**
     * 检查效果是否已解锁
     */
    public boolean hasEffectUnlocked(String effectId) {
        return unlockedEffects.contains(effectId);
    }
    
    /**
     * 获取已完成研究数量
     */
    public int getCompletedCount() {
        return completedResearch.size();
    }
    
    /**
     * 重置所有研究进度
     */
    public void reset() {
        completedResearch.clear();
        inProgressResearch.clear();
        unlockedEffects.clear();
        totalResearchTime = 0;
    }
    
    // ==================== NBT 序列化 ====================
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("player_id", playerId);
        
        // 已完成研究
        NbtList completed = new NbtList();
        for (String id : completedResearch) {
            completed.add(NbtString.of(id));
        }
        nbt.put("completed", completed);
        
        // 进行中研究
        NbtCompound inProgress = new NbtCompound();
        for (var entry : inProgressResearch.entrySet()) {
            inProgress.putLong(entry.getKey(), entry.getValue());
        }
        nbt.put("in_progress", inProgress);
        
        nbt.putLong("total_time", totalResearchTime);
        
        // 解锁效果
        NbtList effects = new NbtList();
        for (String id : unlockedEffects) {
            effects.add(NbtString.of(id));
        }
        nbt.put("unlocked_effects", effects);
        
        return nbt;
    }
    
    public static ResearchProgress fromNbt(NbtCompound nbt) {
        UUID playerId = nbt.getUuid("player_id");
        ResearchProgress progress = new ResearchProgress(playerId);
        
        // 已完成研究
        NbtList completed = nbt.getList("completed", NbtList.STRING_TYPE);
        for (int i = 0; i < completed.size(); i++) {
            progress.completedResearch.add(completed.getString(i));
        }
        
        // 进行中研究
        NbtCompound inProgress = nbt.getCompound("in_progress");
        for (String key : inProgress.getKeys()) {
            progress.inProgressResearch.put(key, inProgress.getLong(key));
        }
        
        progress.totalResearchTime = nbt.getLong("total_time");
        
        // 解锁效果
        NbtList effects = nbt.getList("unlocked_effects", NbtList.STRING_TYPE);
        for (int i = 0; i < effects.size(); i++) {
            progress.unlockedEffects.add(effects.getString(i));
        }
        
        return progress;
    }
}