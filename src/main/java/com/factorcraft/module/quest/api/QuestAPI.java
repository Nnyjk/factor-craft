package com.factorcraft.module.quest.api;

import com.factorcraft.module.quest.model.QuestData;
import com.factorcraft.module.quest.model.QuestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 任务系统统一 API 接口
 * 
 * 提供任务查询、接取、进度管理、奖励领取等功能
 */
public interface QuestAPI {
    
    // ==================== 任务查询 ====================
    
    /**
     * 获取玩家所有任务数据
     */
    Map<Identifier, QuestData> getPlayerQuests(UUID playerId);
    
    /**
     * 获取特定任务数据
     */
    Optional<QuestData> getQuestData(UUID playerId, Identifier questId);
    
    /**
     * 获取玩家进行中的任务
     */
    List<QuestData> getActiveQuests(UUID playerId);
    
    /**
     * 获取玩家已完成的任务
     */
    List<QuestData> getCompletedQuests(UUID playerId);
    
    /**
     * 检查玩家是否可以接取任务
     */
    boolean canAcceptQuest(UUID playerId, Identifier questId);
    
    /**
     * 检查任务是否存在
     */
    boolean questExists(Identifier questId);
    
    // ==================== 任务接取 ====================
    
    /**
     * 接取任务
     * @return 是否成功接取
     */
    boolean acceptQuest(UUID playerId, Identifier questId);
    
    /**
     * 接取任务（带玩家实例）
     */
    boolean acceptQuest(ServerPlayerEntity player, Identifier questId);
    
    // ==================== 进度管理 ====================
    
    /**
     * 更新任务条件进度
     */
    void updateProgress(UUID playerId, Identifier questId, int conditionIndex, float progress);
    
    /**
     * 更新任务条件进度（带玩家实例）
     */
    void updateProgress(ServerPlayerEntity player, Identifier questId, int conditionIndex, float progress);
    
    /**
     * 获取任务总进度
     */
    float getOverallProgress(UUID playerId, Identifier questId);
    
    /**
     * 检查任务是否可完成
     */
    boolean canCompleteQuest(UUID playerId, Identifier questId);
    
    // ==================== 任务完成 ====================
    
    /**
     * 完成任务
     */
    boolean completeQuest(UUID playerId, Identifier questId);
    
    /**
     * 完成任务（带玩家实例）
     */
    boolean completeQuest(ServerPlayerEntity player, Identifier questId);
    
    /**
     * 领取任务奖励
     */
    boolean claimReward(UUID playerId, Identifier questId);
    
    /**
     * 领取任务奖励（带玩家实例）
     */
    boolean claimReward(ServerPlayerEntity player, Identifier questId);
    
    // ==================== 任务类型筛选 ====================
    
    /**
     * 按类型获取任务
     */
    List<QuestData> getQuestsByType(UUID playerId, QuestType type);
    
    /**
     * 获取可接取的任务
     */
    List<Identifier> getAvailableQuests(UUID playerId);
    
    // ==================== 重置功能 ====================
    
    /**
     * 重置玩家任务进度（用于日常/周常任务）
     */
    void resetPlayerQuests(UUID playerId, QuestType type);
    
    /**
     * 清空玩家所有任务数据
     */
    void clearAllQuests(UUID playerId);
    
    // ==================== 静态访问 ====================
    
    /**
     * 获取 API 实例
     */
    static QuestAPI getInstance() {
        return QuestAPIHolder.INSTANCE;
    }
    
    /**
     * API 实例持有者
     */
    class QuestAPIHolder {
        static QuestAPI INSTANCE;
        
        public static void setInstance(QuestAPI instance) {
            INSTANCE = instance;
        }
    }
}