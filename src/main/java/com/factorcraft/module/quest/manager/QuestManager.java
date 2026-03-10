package com.factorcraft.module.quest.manager;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.quest.template.QuestTemplate;
import com.factorcraft.module.quest.instance.QuestInstance;

import net.minecraft.entity.player.PlayerEntity;
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
        FactorCraftMod.LOGGER.debug("[QuestManager] 注册任务模板：{}", template.getId());
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
            FactorCraftMod.LOGGER.warn("[QuestManager] 尝试开始不存在的任务：{}", questId);
            return false;
        }
        
        if (isQuestCompleted(player.getUuid(), questId)) {
            FactorCraftMod.LOGGER.warn("[QuestManager] 任务已完成：{}", questId);
            return false;
        }
        
        Map<Identifier, QuestInstance> playerQuests = 
            this.activeQuests.computeIfAbsent(player.getUuid(), k -> new ConcurrentHashMap<>());
        
        if (playerQuests.containsKey(questId)) {
            FactorCraftMod.LOGGER.warn("[QuestManager] 任务已在进行中：{}", questId);
            return false;
        }
        
        QuestInstance instance = new QuestInstance(template, player.getUuid());
        playerQuests.put(questId, instance);
        
        FactorCraftMod.LOGGER.info("[QuestManager] 玩家 {} 开始任务：{}", 
            player.getName().getString(), questId);
        return true;
    }
    
    public void updateProgress(PlayerEntity player, Identifier questId) {
        QuestInstance instance = getActiveQuest(player.getUuid(), questId);
        if (instance != null && instance.isCompleted()) {
            completeQuest(player, questId);
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
            template.getRewards().forEach(reward -> reward.give(player));
        }
        
        FactorCraftMod.LOGGER.info("[QuestManager] 玩家 {} 完成任务：{}", 
            player.getName().getString(), questId);
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
}
