package com.factorcraft.module.quest.generator;

import com.factorcraft.module.quest.instance.QuestInstance;
import com.factorcraft.module.quest.manager.QuestManager;
import com.factorcraft.module.quest.template.QuestTemplate;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Random;

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
        questManager.getActiveQuests(player.getUuid()).stream()
            .filter(quest -> quest.getTemplate().getCategory().equals("daily"))
            .forEach(quest -> questManager.completeQuest(player.getUuid(), quest.getTemplate().getId()));
        
        // 生成 3 个每日任务
        int dailyQuestCount = 3;
        List<QuestTemplate> dailyTemplates = templates.stream()
            .filter(t -> t.getCategory().equals("daily"))
            .toList();
        
        for (int i = 0; i < dailyQuestCount && i < dailyTemplates.size(); i++) {
            QuestTemplate template = dailyTemplates.get(RANDOM.nextInt(dailyTemplates.size()));
            questManager.addQuest(player.getUuid(), template);
        }
    }
    
    /**
     * 根据玩家进度生成推荐任务
     */
    public static void generateRecommendedQuests(PlayerEntity player, QuestManager questManager, List<QuestTemplate> templates) {
        List<QuestInstance> completedQuests = questManager.getCompletedQuests(player.getUuid());
        
        // 找到已完成任务的后续任务
        templates.stream()
            .filter(template -> isRecommended(template, completedQuests))
            .filter(template -> !questManager.hasQuest(player.getUuid(), template.getId()))
            .limit(5)
            .forEach(template -> questManager.addQuest(player.getUuid(), template));
    }
    
    private static boolean isRecommended(QuestTemplate template, List<QuestInstance> completedQuests) {
        // 检查前置任务是否完成
        for (String prerequisite : template.getPrerequisites()) {
            boolean completed = completedQuests.stream()
                .anyMatch(quest -> quest.getTemplate().getId().equals(prerequisite));
            if (!completed) {
                return false;
            }
        }
        return true;
    }
}
