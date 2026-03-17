package com.factorcraft.module.quest.ui;

import com.factorcraft.module.network.QuestSyncPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端任务追踪缓存
 * 
 * 存储从服务端同步的任务数据，供 QuestTrackerScreen 使用
 */
public class QuestTrackerCache {
    
    private static volatile List<QuestSyncPayload.QuestData> ACTIVE_QUESTS = new ArrayList<>();
    private static volatile Set<Identifier> COMPLETED_QUESTS = ConcurrentHashMap.newKeySet();
    
    /**
     * 更新缓存数据
     */
    public static void update(List<QuestSyncPayload.QuestData> activeQuests, Set<Identifier> completedQuests) {
        ACTIVE_QUESTS = new ArrayList<>(activeQuests);
        COMPLETED_QUESTS = ConcurrentHashMap.newKeySet();
        COMPLETED_QUESTS.addAll(completedQuests);
    }
    
    /**
     * 获取活跃任务列表
     */
    public static List<QuestSyncPayload.QuestData> getActiveQuests() {
        return Collections.unmodifiableList(ACTIVE_QUESTS);
    }
    
    /**
     * 获取已完成任务 ID 列表
     */
    public static Set<Identifier> getCompletedQuests() {
        return Collections.unmodifiableSet(COMPLETED_QUESTS);
    }
    
    /**
     * 检查任务是否已完成
     */
    public static boolean isQuestCompleted(Identifier questId) {
        return COMPLETED_QUESTS.contains(questId);
    }
    
    /**
     * 清除缓存（登出时调用）
     */
    public static void clear() {
        ACTIVE_QUESTS.clear();
        COMPLETED_QUESTS.clear();
    }
}
