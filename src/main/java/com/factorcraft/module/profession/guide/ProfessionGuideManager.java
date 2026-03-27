package com.factorcraft.module.profession.guide;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 职业系统新手引导管理器
 * 
 * 提供三阶段引导：
 * 1. 职业选择引导 - 帮助玩家了解各职业特点
 * 2. 天赋分配引导 - 指导天赋点分配策略
 * 3. 技能使用引导 - 教授技能使用时机和策略
 */
public class ProfessionGuideManager {
    
    // 引导阶段枚举
    public enum GuideStage {
        PROFESSION_SELECTION("职业选择引导", "帮助您了解各职业特点"),
        TALENT_ALLOCATION("天赋分配引导", "指导天赋点分配策略"),
        SKILL_USAGE("技能使用引导", "教授技能使用时机");
        
        private final String title;
        private final String description;
        
        GuideStage(String title, String description) {
            this.title = title;
            this.description = description;
        }
        
        public String getTitle() { return title; }
        public String getDescription() { return description; }
    }
    
    // 单例实例
    private static final ProfessionGuideManager INSTANCE = new ProfessionGuideManager();
    
    // 已完成引导的玩家集合（内存缓存）
    private final Set<UUID> completedSelectionGuide = new HashSet<>();
    private final Set<UUID> completedTalentGuide = new HashSet<>();
    private final Set<UUID> completedSkillGuide = new HashSet<>();
    
    private ProfessionGuideManager() {}
    
    public static ProfessionGuideManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 触发职业选择引导
     * 当玩家首次打开职业选择界面时调用
     */
    public void triggerProfessionSelectionGuide(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        
        if (completedSelectionGuide.contains(playerId)) {
            return; // 已完成，不重复触发
        }
        
        // 发送引导消息
        player.sendMessage(Text.literal("§6§l========== 职业选择引导 =========="), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§e欢迎来到 Factor Craft 职业系统！"), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§f请根据您的游戏风格选择职业："), false);
        player.sendMessage(Text.literal(""), false);
        
        // 显示各职业特点
        for (ProfessionType type : ProfessionType.getBasicProfessions()) {
            String tags = String.join("§7 | §b", type.getCoreTags());
            player.sendMessage(Text.literal("§b§l" + type.getDisplayName() + " §7- §f" + type.getDescription()), false);
            player.sendMessage(Text.literal("  §7核心标签: §b" + tags), false);
            player.sendMessage(Text.literal(""), false);
        }
        
        player.sendMessage(Text.literal("§7提示：您可以在游戏过程中更换职业，但会损失部分经验。"), false);
        player.sendMessage(Text.literal("§6§l==================================="), false);
        
        // 播放提示音
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1.0f, 1.2f);
        
        // 标记完成
        completedSelectionGuide.add(playerId);
    }
    
    /**
     * 触发天赋分配引导
     * 当玩家首次获得天赋点时调用
     */
    public void triggerTalentAllocationGuide(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        
        if (completedTalentGuide.contains(playerId)) {
            return;
        }
        
        player.sendMessage(Text.literal("§6§l========== 天赋分配引导 =========="), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§e您已获得天赋点！合理分配可以大幅提升职业能力。"), false);
        player.sendMessage(Text.literal(""), false);
        
        player.sendMessage(Text.literal("§f§l天赋类型说明："), false);
        player.sendMessage(Text.literal("§c§l攻击天赋 §7- 提升伤害输出能力"), false);
        player.sendMessage(Text.literal("§a§l生存天赋 §7- 提升生命值和防御"), false);
        player.sendMessage(Text.literal("§b§l效率天赋 §7- 提升生产和采集效率"), false);
        player.sendMessage(Text.literal("§d§l辅助天赋 §7- 提升团队增益效果"), false);
        player.sendMessage(Text.literal(""), false);
        
        player.sendMessage(Text.literal("§f§l分配建议："), false);
        player.sendMessage(Text.literal("§7• §f新手建议均衡分配，各类型点数差异不超过2点"), false);
        player.sendMessage(Text.literal("§7• §f确定主要玩法后，可专注于一条天赋路线"), false);
        player.sendMessage(Text.literal("§7• §f每种职业有推荐天赋搭配，参考职业说明"), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§7使用 §e/profession talent §7查看和分配天赋点"), false);
        player.sendMessage(Text.literal("§6§l==================================="), false);
        
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 1.0f);
        
        completedTalentGuide.add(playerId);
    }
    
    /**
     * 触发技能使用引导
     * 当玩家首次解锁技能时调用
     */
    public void triggerSkillUsageGuide(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        
        if (completedSkillGuide.contains(playerId)) {
            return;
        }
        
        player.sendMessage(Text.literal("§6§l========== 技能使用引导 =========="), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§e恭喜解锁第一个职业技能！"), false);
        player.sendMessage(Text.literal(""), false);
        
        player.sendMessage(Text.literal("§f§l技能使用方法："), false);
        player.sendMessage(Text.literal("§7• §f按下 §e[技能快捷键] §f打开技能面板"), false);
        player.sendMessage(Text.literal("§7• §f将技能拖拽到快捷栏进行装备"), false);
        player.sendMessage(Text.literal("§7• §f使用快捷键激活技能（默认 1-9 键）"), false);
        player.sendMessage(Text.literal(""), false);
        
        player.sendMessage(Text.literal("§f§l技能类型："), false);
        player.sendMessage(Text.literal("§c主动技能 §7- 需要手动激活，有冷却时间"), false);
        player.sendMessage(Text.literal("§a被动技能 §7- 自动触发，持续生效"), false);
        player.sendMessage(Text.literal(""), false);
        
        player.sendMessage(Text.literal("§f§l使用技巧："), false);
        player.sendMessage(Text.literal("§7• §f注意技能冷却时间，合理安排使用顺序"), false);
        player.sendMessage(Text.literal("§7• §f部分技能消耗 Factor，确保有足够储备"), false);
        player.sendMessage(Text.literal("§7• §f组合技能可以产生更强的效果"), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§7使用 §e/profession skill §7查看技能列表"), false);
        player.sendMessage(Text.literal("§6§l==================================="), false);
        
        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1.0f, 1.2f);
        
        completedSkillGuide.add(playerId);
    }
    
    /**
     * 检查是否已完成指定阶段的引导
     */
    public boolean hasCompletedGuide(UUID playerId, GuideStage stage) {
        return switch (stage) {
            case PROFESSION_SELECTION -> completedSelectionGuide.contains(playerId);
            case TALENT_ALLOCATION -> completedTalentGuide.contains(playerId);
            case SKILL_USAGE -> completedSkillGuide.contains(playerId);
        };
    }
    
    /**
     * 重置玩家的引导状态（用于测试或特殊需求）
     */
    public void resetGuideStatus(UUID playerId) {
        completedSelectionGuide.remove(playerId);
        completedTalentGuide.remove(playerId);
        completedSkillGuide.remove(playerId);
    }
    
    /**
     * 获取玩家的引导进度
     */
    public int getGuideProgress(UUID playerId) {
        int completed = 0;
        if (completedSelectionGuide.contains(playerId)) completed++;
        if (completedTalentGuide.contains(playerId)) completed++;
        if (completedSkillGuide.contains(playerId)) completed++;
        return completed;
    }
    
    /**
     * 获取引导完成百分比
     */
    public float getGuideProgressPercent(UUID playerId) {
        return getGuideProgress(playerId) / 3.0f * 100;
    }
    
    /**
     * 显示当前引导进度
     */
    public void showGuideProgress(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        int progress = getGuideProgress(playerId);
        
        player.sendMessage(Text.literal("§6§l===== 引导完成度: §e" + progress + "/3 §6§l====="), false);
        
        String selection = completedSelectionGuide.contains(playerId) ? "§a✓" : "§c✗";
        String talent = completedTalentGuide.contains(playerId) ? "§a✓" : "§c✗";
        String skill = completedSkillGuide.contains(playerId) ? "§a✓" : "§c✗";
        
        player.sendMessage(Text.literal(selection + " §f职业选择引导"), false);
        player.sendMessage(Text.literal(talent + " §f天赋分配引导"), false);
        player.sendMessage(Text.literal(skill + " §f技能使用引导"), false);
    }
}