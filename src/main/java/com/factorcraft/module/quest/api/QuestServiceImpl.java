package com.factorcraft.module.quest.api;

import com.factorcraft.module.quest.event.QuestAcceptEvent;
import com.factorcraft.module.quest.event.QuestCompleteEvent;
import com.factorcraft.module.quest.event.QuestEventBus;
import com.factorcraft.module.quest.event.QuestProgressEvent;
import com.factorcraft.module.quest.model.QuestData;
import com.factorcraft.module.quest.model.QuestType;
import com.factorcraft.module.quest.storage.QuestProgressStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务系统 API 实现
 */
public class QuestServiceImpl implements QuestAPI {
    
    private final QuestProgressStorage storage;
    private final QuestEventBus eventBus;
    private final Set<Identifier> registeredQuests;
    
    public QuestServiceImpl(ServerWorld world) {
        this.storage = QuestProgressStorage.get(world);
        this.eventBus = new QuestEventBus();
        this.registeredQuests = new HashSet<>();
        
        // 注册 API 实例
        QuestAPIHolder.setInstance(this);
    }
    
    // ==================== 任务查询 ====================
    
    @Override
    public Map<Identifier, QuestData> getPlayerQuests(UUID playerId) {
        return storage.getPlayerQuests(playerId);
    }
    
    @Override
    public Optional<QuestData> getQuestData(UUID playerId, Identifier questId) {
        return storage.getQuestData(playerId, questId);
    }
    
    @Override
    public List<QuestData> getActiveQuests(UUID playerId) {
        return getPlayerQuests(playerId).values().stream()
            .filter(q -> q.getState() == QuestData.QuestState.ACTIVE)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<QuestData> getCompletedQuests(UUID playerId) {
        return getPlayerQuests(playerId).values().stream()
            .filter(q -> q.getState() == QuestData.QuestState.COMPLETED || 
                         q.getState() == QuestData.QuestState.TURNED_IN)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean canAcceptQuest(UUID playerId, Identifier questId) {
        // 检查任务是否存在
        if (!questExists(questId)) {
            return false;
        }
        
        // 检查是否已接取
        Optional<QuestData> existing = getQuestData(playerId, questId);
        if (existing.isPresent()) {
            QuestData.QuestState state = existing.get().getState();
            return state == QuestData.QuestState.AVAILABLE || 
                   state == QuestData.QuestState.FAILED;
        }
        
        return true;
    }
    
    @Override
    public boolean questExists(Identifier questId) {
        return registeredQuests.contains(questId);
    }
    
    // ==================== 任务接取 ====================
    
    @Override
    public boolean acceptQuest(UUID playerId, Identifier questId) {
        if (!canAcceptQuest(playerId, questId)) {
            return false;
        }
        
        QuestData questData = getQuestData(playerId, questId)
            .orElseGet(() -> new QuestData(questId, playerId));
        
        // 触发前置事件
        // QuestAcceptEvent.Pre event = new QuestAcceptEvent.Pre(questData, player);
        // eventBus.publishAcceptPre(event);
        // if (event.isCancelled()) {
        //     return false;
        // }
        
        // 接取任务（默认 1 个条件）
        questData.accept(1);
        storage.setQuestData(playerId, questData);
        
        // 触发后置事件
        // eventBus.publishAcceptPost(new QuestAcceptEvent.Post(questData, player));
        
        return true;
    }
    
    @Override
    public boolean acceptQuest(ServerPlayerEntity player, Identifier questId) {
        return acceptQuest(player.getUuid(), questId);
    }
    
    // ==================== 进度管理 ====================
    
    @Override
    public void updateProgress(UUID playerId, Identifier questId, int conditionIndex, float progress) {
        Optional<QuestData> optional = getQuestData(playerId, questId);
        if (optional.isEmpty()) {
            return;
        }
        
        QuestData questData = optional.get();
        if (questData.getState() != QuestData.QuestState.ACTIVE) {
            return;
        }
        
        float oldProgress = questData.getConditionProgress()[conditionIndex];
        questData.updateProgress(conditionIndex, progress);
        storage.setQuestData(playerId, questData);
        
        // 触发进度事件
        // QuestProgressEvent event = new QuestProgressEvent(questData, player, conditionIndex, oldProgress, progress);
        // eventBus.publishProgress(event);
    }
    
    @Override
    public void updateProgress(ServerPlayerEntity player, Identifier questId, int conditionIndex, float progress) {
        updateProgress(player.getUuid(), questId, conditionIndex, progress);
    }
    
    @Override
    public float getOverallProgress(UUID playerId, Identifier questId) {
        return getQuestData(playerId, questId)
            .map(QuestData::getOverallProgress)
            .orElse(0.0f);
    }
    
    @Override
    public boolean canCompleteQuest(UUID playerId, Identifier questId) {
        return getQuestData(playerId, questId)
            .map(QuestData::isAllConditionsCompleted)
            .orElse(false);
    }
    
    // ==================== 任务完成 ====================
    
    @Override
    public boolean completeQuest(UUID playerId, Identifier questId) {
        Optional<QuestData> optional = getQuestData(playerId, questId);
        if (optional.isEmpty()) {
            return false;
        }
        
        QuestData questData = optional.get();
        if (questData.getState() != QuestData.QuestState.ACTIVE) {
            return false;
        }
        
        if (!questData.isAllConditionsCompleted()) {
            return false;
        }
        
        // 触发前置事件
        // QuestCompleteEvent.Pre event = new QuestCompleteEvent.Pre(questData, player);
        // eventBus.publishCompletePre(event);
        
        questData.complete();
        storage.setQuestData(playerId, questData);
        
        // 触发后置事件
        // eventBus.publishCompletePost(new QuestCompleteEvent.Post(questData, player));
        
        return true;
    }
    
    @Override
    public boolean completeQuest(ServerPlayerEntity player, Identifier questId) {
        return completeQuest(player.getUuid(), questId);
    }
    
    @Override
    public boolean claimReward(UUID playerId, Identifier questId) {
        Optional<QuestData> optional = getQuestData(playerId, questId);
        if (optional.isEmpty()) {
            return false;
        }
        
        QuestData questData = optional.get();
        if (questData.getState() != QuestData.QuestState.COMPLETED) {
            return false;
        }
        
        questData.turnIn();
        storage.setQuestData(playerId, questData);
        
        return true;
    }
    
    @Override
    public boolean claimReward(ServerPlayerEntity player, Identifier questId) {
        return claimReward(player.getUuid(), questId);
    }
    
    // ==================== 任务类型筛选 ====================
    
    @Override
    public List<QuestData> getQuestsByType(UUID playerId, QuestType type) {
        // TODO: 需要从 QuestTemplate 获取任务类型
        return getActiveQuests(playerId);
    }
    
    @Override
    public List<Identifier> getAvailableQuests(UUID playerId) {
        // TODO: 需要检查前置任务条件
        return new ArrayList<>(registeredQuests);
    }
    
    // ==================== 重置功能 ====================
    
    @Override
    public void resetPlayerQuests(UUID playerId, QuestType type) {
        List<QuestData> quests = getQuestsByType(playerId, type);
        for (QuestData quest : quests) {
            storage.removeQuestData(playerId, quest.getQuestId());
        }
    }
    
    @Override
    public void clearAllQuests(UUID playerId) {
        storage.clearPlayerQuests(playerId);
    }
    
    // ==================== 任务注册 ====================
    
    /**
     * 注册任务
     */
    public void registerQuest(Identifier questId) {
        registeredQuests.add(questId);
    }
    
    /**
     * 取消注册任务
     */
    public void unregisterQuest(Identifier questId) {
        registeredQuests.remove(questId);
    }
    
    /**
     * 获取事件总线
     */
    public QuestEventBus getEventBus() {
        return eventBus;
    }
}