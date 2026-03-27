package com.factorcraft.module.profession.guide;

import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

/**
 * 引导触发器
 * 
 * 在特定事件发生时触发新手引导
 * 集成到职业系统的各个事件点
 */
public class GuideTrigger {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/GuideTrigger");
    
    private static GuideTrigger INSTANCE;
    
    private GuideTrigger() {}
    
    public static GuideTrigger getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuideTrigger();
        }
        return INSTANCE;
    }
    
    /**
     * 当玩家打开职业选择界面时触发
     */
    public void onProfessionMenuOpen(ServerPlayerEntity player, ProfessionAPI api) {
        UUID playerId = player.getUuid();
        ProfessionGuideManager guideManager = ProfessionGuideManager.getInstance();
        
        // 检查是否已完成职业选择引导
        if (!guideManager.hasCompletedGuide(playerId, ProfessionGuideManager.GuideStage.PROFESSION_SELECTION)) {
            guideManager.triggerProfessionSelectionGuide(player);
            LOGGER.debug("触发玩家 {} 的职业选择引导", player.getName().getString());
        }
    }
    
    /**
     * 当玩家首次获得天赋点时触发
     */
    public void onTalentPointGain(ServerPlayerEntity player, ProfessionAPI api) {
        UUID playerId = player.getUuid();
        ProfessionGuideManager guideManager = ProfessionGuideManager.getInstance();
        
        // 检查是否已完成天赋分配引导
        if (!guideManager.hasCompletedGuide(playerId, ProfessionGuideManager.GuideStage.TALENT_ALLOCATION)) {
            PlayerProfessionData data = api.getPlayerData(player);
            
            // 只有当玩家确实有天赋点时才触发
            if (data != null && data.getTalentPoints() > 0) {
                guideManager.triggerTalentAllocationGuide(player);
                LOGGER.debug("触发玩家 {} 的天赋分配引导", player.getName().getString());
            }
        }
    }
    
    /**
     * 当玩家首次解锁技能时触发
     */
    public void onSkillUnlock(ServerPlayerEntity player, ProfessionAPI api) {
        UUID playerId = player.getUuid();
        ProfessionGuideManager guideManager = ProfessionGuideManager.getInstance();
        
        // 检查是否已完成技能使用引导
        if (!guideManager.hasCompletedGuide(playerId, ProfessionGuideManager.GuideStage.SKILL_USAGE)) {
            guideManager.triggerSkillUsageGuide(player);
            LOGGER.debug("触发玩家 {} 的技能使用引导", player.getName().getString());
        }
    }
    
    /**
     * 当玩家选择职业时触发
     * 检查是否需要触发相关引导
     */
    public void onProfessionSelect(ServerPlayerEntity player, ProfessionType profession, ProfessionAPI api) {
        UUID playerId = player.getUuid();
        ProfessionGuideManager guideManager = ProfessionGuideManager.getInstance();
        
        // 发送职业选择确认消息
        player.sendMessage(
            net.minecraft.text.Text.literal("§a§l✓ 已选择职业: " + profession.getDisplayName()),
            false
        );
        player.sendMessage(
            net.minecraft.text.Text.literal("§7使用 /profession info 查看职业详情"),
            false
        );
        
        // 检查是否需要触发天赋引导
        PlayerProfessionData data = api.getPlayerData(player);
        if (data != null && data.getTalentPoints() > 0) {
            if (!guideManager.hasCompletedGuide(playerId, ProfessionGuideManager.GuideStage.TALENT_ALLOCATION)) {
                guideManager.triggerTalentAllocationGuide(player);
            }
        }
        
        LOGGER.info("玩家 {} 选择了职业 {}", player.getName().getString(), profession.getDisplayName());
    }
    
    /**
     * 当玩家升级时触发
     * 检查是否解锁了新技能
     */
    public void onLevelUp(ServerPlayerEntity player, int newLevel, ProfessionAPI api) {
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) return;
        
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) return;
        
        ProfessionType profession = professionOpt.get();
        
        // 检查是否解锁了新技能槽
        int unlockedSlots = calculateUnlockedSkillSlots(newLevel);
        int previousSlots = calculateUnlockedSkillSlots(newLevel - 1);
        
        if (unlockedSlots > previousSlots) {
            // 解锁了新技能槽，触发技能引导
            GuideTrigger.getInstance().onSkillUnlock(player, api);
            
            player.sendMessage(
                net.minecraft.text.Text.literal("§b§l★ 解锁新技能槽！当前可用: " + unlockedSlots),
                false
            );
        }
        
        // 检查天赋点奖励
        int talentPoints = data != null ? data.getTalentPoints() : 0;
        if (talentPoints > 0) {
            player.sendMessage(
                net.minecraft.text.Text.literal("§d§l★ 获得天赋点！当前可用: " + talentPoints),
                false
            );
        }
    }
    
    /**
     * 计算已解锁的技能槽数量
     * 等级 1: 1 槽位
     * 等级 5: 2 槽位
     * 等级 10: 3 槽位
     * 等级 15: 4 槽位
     * 等级 20: 5 槽位
     */
    private int calculateUnlockedSkillSlots(int level) {
        if (level < 1) return 0;
        if (level < 5) return 1;
        if (level < 10) return 2;
        if (level < 15) return 3;
        if (level < 20) return 4;
        return 5;
    }
    
    /**
     * 综合检查玩家的引导状态
     * 建议在玩家加入服务器时调用
     */
    public void checkAndTriggerGuides(ServerPlayerEntity player, ProfessionAPI api) {
        UUID playerId = player.getUuid();
        ProfessionGuideManager guideManager = ProfessionGuideManager.getInstance();
        
        // 检查是否需要触发职业选择引导
        Optional<ProfessionType> professionOpt = api.getPlayerProfession(player);
        if (professionOpt.isEmpty()) {
            // 玩家尚未选择职业，在下次打开职业菜单时触发
            return;
        }
        
        // 已选择职业，检查天赋引导
        PlayerProfessionData data = api.getPlayerData(player);
        if (data != null && data.getTalentPoints() > 0) {
            if (!guideManager.hasCompletedGuide(playerId, ProfessionGuideManager.GuideStage.TALENT_ALLOCATION)) {
                guideManager.triggerTalentAllocationGuide(player);
            }
        }
        
        // 检查技能引导
        int level = data != null ? data.getLevel() : 1;
        if (level >= 1 && !guideManager.hasCompletedGuide(playerId, ProfessionGuideManager.GuideStage.SKILL_USAGE)) {
            guideManager.triggerSkillUsageGuide(player);
        }
    }
}