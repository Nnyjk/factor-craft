package com.factorcraft.module.quest.generator;

import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.template.QuestTemplate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务生成器 - 生成动态任务和每日任务
 */
public class QuestGenerator {
    
    private static final Random RANDOM = new Random();
    
    /**
     * 为玩家生成每日任务
     */
    public static void generateDailyQuests(PlayerEntity player, QuestManager questManager, List<QuestTemplate> templates) {
        // 清除旧的每日任务
        List<QuestInstance> activeQuests = questManager.getActiveQuests(player.getUuid());
        activeQuests.stream()
            .filter(quest -> quest.getTemplate().getCategory().equals("daily"))
            .forEach(quest -> questManager.completeQuest(player, quest.getTemplate().getId()));
        
        // 生成 3 个每日任务
        int dailyQuestCount = 3;
        List<QuestTemplate> dailyTemplates = templates.stream()
            .filter(t -> t.getCategory().equals("daily"))
            .toList();
        
        for (int i = 0; i < dailyQuestCount && i < dailyTemplates.size(); i++) {
            QuestTemplate template = dailyTemplates.get(RANDOM.nextInt(dailyTemplates.size()));
            questManager.startQuest(player, template.getId());
        }
    }
    
    /**
     * 根据玩家进度生成推荐任务
     */
    public static void generateRecommendedQuests(PlayerEntity player, QuestManager questManager, List<QuestTemplate> templates) {
        Set<Identifier> completedQuests = questManager.getCompletedQuests(player.getUuid());
        List<Identifier> activeQuestIds = questManager.getActiveQuests(player.getUuid()).stream()
            .map(q -> q.getTemplate().getId())
            .collect(Collectors.toList());
        
        // 找到已完成任务的后续任务
        templates.stream()
            .filter(template -> isRecommended(template, completedQuests))
            .filter(template -> !completedQuests.contains(template.getId()))
            .filter(template -> !activeQuestIds.contains(template.getId()))
            .limit(5)
            .forEach(template -> questManager.startQuest(player, template.getId()));
    }
    
    private static boolean isRecommended(QuestTemplate template, Set<Identifier> completedIds) {
        // 简单逻辑：如果任务没有前置要求，或者是新手任务，则推荐
        // 更复杂的逻辑需要添加 prerequisites 字段到 QuestTemplate
        return template.getCategory().equals("newbie") || completedIds.isEmpty();
    }
}
