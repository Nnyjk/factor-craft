package com.factorcraft.module.core.achievement;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

/**
 * 成就分类枚举
 * 定义成就的五大类别
 */
public enum AchievementCategory {
    /**
     * 主线剧情成就
     * 跟随游戏进程解锁的关键里程碑
     */
    STORY("story", Text.translatable("achievement.category.factor_craft.story")),
    
    /**
     * Factor 相关成就
     * 涉及 Factor 生产、提纯、应用等
     */
    FACTOR("factor", Text.translatable("achievement.category.factor_craft.factor")),
    
    /**
     * 机器设备成就
     * 涉及各种机器的制作和使用
     */
    MACHINE("machine", Text.translatable("achievement.category.factor_craft.machine")),
    
    /**
     * 探索发现成就
     * 涉及维度探索、结构发现等
     */
    EXPLORATION("exploration", Text.translatable("achievement.category.factor_craft.exploration")),
    
    /**
     * 战斗挑战成就
     * 涉及 Boss 击杀、装备获取等
     */
    COMBAT("combat", Text.translatable("achievement.category.factor_craft.combat"));
    
    private final String name;
    private final Text displayName;
    
    AchievementCategory(String name, Text displayName) {
        this.name = name;
        this.displayName = displayName;
    }
    
    public String getName() {
        return name;
    }
    
    public Text getDisplayName() {
        return displayName;
    }
}
