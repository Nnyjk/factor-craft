package com.factorcraft.module.quest.template;

import com.factorcraft.module.quest.condition.QuestCondition;
import com.factorcraft.module.quest.reward.QuestReward;

import net.minecraft.util.Identifier;

import java.util.List;

/**
 * 任务模板 - JSON 配置定义的任务结构
 */
public class QuestTemplate {
    
    private final Identifier id;
    private final String category;
    private final boolean repeatable;
    private final int priority;
    private final String title;
    private final String description;
    private final Identifier icon;
    private final List<QuestCondition> conditions;
    private final List<QuestReward> rewards;
    private final List<Identifier> nextQuests;
    
    public QuestTemplate(Identifier id, String category, boolean repeatable, int priority,
                         String title, String description, Identifier icon,
                         List<QuestCondition> conditions, List<QuestReward> rewards,
                         List<Identifier> nextQuests) {
        this.id = id;
        this.category = category;
        this.repeatable = repeatable;
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.conditions = conditions;
        this.rewards = rewards;
        this.nextQuests = nextQuests;
    }
    
    public Identifier getId() { return id; }
    public String getCategory() { return category; }
    public boolean isRepeatable() { return repeatable; }
    public int getPriority() { return priority; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Identifier getIcon() { return icon; }
    public List<QuestCondition> getConditions() { return conditions; }
    public List<QuestReward> getRewards() { return rewards; }
    public List<Identifier> getNextQuests() { return nextQuests; }
}
