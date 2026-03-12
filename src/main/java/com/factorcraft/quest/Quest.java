package com.factorcraft.quest;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

/**
 * 任务定义
 */
public record Quest(
    String id,
    String name,
    String description,
    QuestReward reward,
    QuestType type,
    int required
) {
    /**
     * 检查任务是否可以完成
     */
    public boolean canComplete(ServerPlayerEntity player, int currentProgress) {
        return currentProgress >= required;
    }
}

/**
 * 任务类型
 */
enum QuestType {
    EXTRACTION("提取"),
    TRAIT("特性"),
    RESONANCE("共振"),
    DISCOVERY("发现"),
    COMBAT("战斗"),
    CRAFTING("合成");
    
    private final String displayName;
    
    QuestType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

/**
 * 任务奖励
 */
record QuestReward(
    ItemStack item,
    int experience,
    int factorPoints
) {
    public void giveTo(ServerPlayerEntity player) {
        // 给予物品
        if (!item.isEmpty()) {
            player.giveItemStack(item.copy());
        }
        
        // 给予经验
        if (experience > 0) {
            player.addExperience(experience);
        }
        
        // TODO: 给予 Factor 点数
        if (factorPoints > 0) {
            // Factor 点数系统待实现
        }
    }
}

/**
 * 玩家任务数据
 */
class PlayerQuestData {
    private final Map<String, Integer> progress = new HashMap<>();
    private final Set<String> completed = new HashSet<>();
    
    public void updateProgress(String questId, int amount) {
        progress.merge(questId, amount, Integer::sum);
    }
    
    public int getProgress(String questId) {
        return progress.getOrDefault(questId, 0);
    }
    
    public boolean completeQuest(String questId) {
        if (completed.contains(questId)) {
            return false;
        }
        completed.add(questId);
        return true;
    }
    
    public boolean isCompleted(String questId) {
        return completed.contains(questId);
    }
    
    public Map<String, Integer> getAllProgress() {
        return Collections.unmodifiableMap(progress);
    }
    
    public Set<String> getCompletedQuests() {
        return Collections.unmodifiableSet(completed);
    }
}