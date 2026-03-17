package com.factorcraft.module.quest.manager;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.advancement.AdvancementManager;
import com.factorcraft.module.network.QuestRewardPayload;
import com.factorcraft.module.network.QuestSyncPayload;
import com.factorcraft.module.quest.template.QuestTemplate;
import com.factorcraft.module.quest.instance.QuestInstance;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务管理器 - 管理任务模板和玩家进度
 */
public class QuestManager {
    
    private final Map<Identifier, QuestTemplate> templates;
    private final Map<UUID, Map<Identifier, QuestInstance>> activeQuests;
    private final Map<UUID, Set<Identifier>> completedQuests;
    
    public QuestManager() {
        this.templates = new ConcurrentHashMap<>();
        this.activeQuests = new ConcurrentHashMap<>();
        this.completedQuests = new ConcurrentHashMap<>();
    }
    
    public void registerTemplate(QuestTemplate template) {
        this.templates.put(template.getId(), template);
        FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 注册任务模板：{}", template.getId());
    }
    
    public QuestTemplate getTemplate(Identifier id) {
        return this.templates.get(id);
    }
    
    public Collection<QuestTemplate> getAllTemplates() {
        return Collections.unmodifiableCollection(this.templates.values());
    }
    
    public boolean startQuest(PlayerEntity player, Identifier questId) {
        QuestTemplate template = this.templates.get(questId);
        if (template == null) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 尝试开始不存在的任务：{}", questId);
            return false;
        }
        
        if (isQuestCompleted(player.getUuid(), questId)) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 任务已完成：{}", questId);
            return false;
        }
        
        Map<Identifier, QuestInstance> playerQuests = 
            this.activeQuests.computeIfAbsent(player.getUuid(), k -> new ConcurrentHashMap<>());
        
        if (playerQuests.containsKey(questId)) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Quest] 任务已在进行中：{}", questId);
            return false;
        }
        
        QuestInstance instance = new QuestInstance(template, player.getUuid());
        playerQuests.put(questId, instance);
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 玩家 {} 开始任务：{}", 
            player.getName().getString(), questId);
        
        // 同步到客户端
        if (player instanceof ServerPlayerEntity serverPlayer) {
            syncToClient(serverPlayer);
        }
        
        return true;
    }
    
    public void updateProgress(PlayerEntity player, Identifier questId) {
        QuestInstance instance = getActiveQuest(player.getUuid(), questId);
        if (instance != null && instance.isCompleted()) {
            completeQuest(player, questId);
        } else if (instance != null && player instanceof ServerPlayerEntity serverPlayer) {
            // 进度更新时也同步（可选，避免过度同步）
            // syncToClient(serverPlayer);
        }
    }
    
    public void completeQuest(PlayerEntity player, Identifier questId) {
        QuestInstance instance = getActiveQuest(player.getUuid(), questId);
        if (instance == null) return;
        
        this.completedQuests.computeIfAbsent(player.getUuid(), k -> new HashSet<>()).add(questId);
        
        Map<Identifier, QuestInstance> playerQuests = this.activeQuests.get(player.getUuid());
        if (playerQuests != null) {
            playerQuests.remove(questId);
        }
        
        QuestTemplate template = this.templates.get(questId);
        if (template != null) {
            // 发放奖励并发送通知
            template.getRewards().forEach(reward -> {
                reward.give(player);
                // 发送客户端通知
                QuestRewardPayload.sendToPlayer(
                    (ServerPlayerEntity) player,
                    reward.getType().name(),
                    reward.getDescription()
                );
            });
            
            // 触发关联成就
            if (player instanceof ServerPlayerEntity serverPlayer) {
                template.getAdvancementIds().forEach(advancementId -> {
                    AdvancementManager.grantAdvancement(serverPlayer, advancementId);
                    FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 触发成就：{} for player {}", 
                        advancementId, player.getName().getString());
                });
            }
        }
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Quest] 玩家 {} 完成任务：{}", 
            player.getName().getString(), questId);
        
        // 同步到客户端
        if (player instanceof ServerPlayerEntity serverPlayer) {
            syncToClient(serverPlayer);
        }
    }
    
    public boolean isQuestCompleted(UUID playerId, Identifier questId) {
        Set<Identifier> completed = this.completedQuests.get(playerId);
        return completed != null && completed.contains(questId);
    }
    
    public QuestInstance getActiveQuest(UUID playerId, Identifier questId) {
        Map<Identifier, QuestInstance> playerQuests = this.activeQuests.get(playerId);
        return playerQuests != null ? playerQuests.get(questId) : null;
    }
    
    public List<QuestInstance> getActiveQuests(UUID playerId) {
        Map<Identifier, QuestInstance> playerQuests = this.activeQuests.get(playerId);
        return playerQuests != null ? new ArrayList<>(playerQuests.values()) : Collections.emptyList();
    }
    
    public Set<Identifier> getCompletedQuests(UUID playerId) {
        Set<Identifier> completed = this.completedQuests.get(playerId);
        return completed != null ? Collections.unmodifiableSet(completed) : Collections.emptySet();
    }
    
    public float getProgress(PlayerEntity player, Identifier questId) {
        QuestInstance instance = getActiveQuest(player.getUuid(), questId);
        return instance != null ? instance.getOverallProgress() : 0.0f;
    }
    
    /**
     * 同步任务数据到客户端
     * 在任务状态变化时调用
     */
    public void syncToClient(ServerPlayerEntity player) {
        List<QuestInstance> activeQuests = getActiveQuests(player.getUuid());
        Set<Identifier> completedQuests = getCompletedQuests(player.getUuid());
        
        QuestSyncPayload.sendToPlayer(player, activeQuests, completedQuests);
        FactorCraftMod.LOGGER.debug("[FactorCraft:Quest] 已同步任务数据到玩家 {}: {} 个活跃，{} 个已完成",
            player.getName().getString(), activeQuests.size(), completedQuests.size());
    }
}