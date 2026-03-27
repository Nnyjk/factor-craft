package com.factorcraft.module.profession;

import com.factorcraft.module.profession.api.ProfessionAPI;
import com.factorcraft.module.profession.model.PlayerProfessionData;
import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 隐藏职业解锁管理器
 * 
 * 负责检测和解锁隐藏职业「因子掌控者」
 * 
 * 解锁条件：
 * 1. 3个基础职业全部达到满级（10级）
 * 2. 收集齐5种稀有Factor
 */
public class HiddenProfessionUnlockManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/HiddenProfession");
    
    /** 解锁所需的稀有Factor数量 */
    public static final int REQUIRED_RARE_FACTORS = 5;
    
    /** 基础职业满级等级 */
    public static final int MASTERY_LEVEL = 10;
    
    /** 解锁后额外获得的天赋点 */
    public static final int BONUS_TALENT_POINTS = 10;
    
    /**
     * 检查玩家是否满足解锁条件
     * 
     * @param player 玩家
     * @param api 职业API
     * @return 是否满足解锁条件
     */
    public static boolean canUnlock(ServerPlayerEntity player, ProfessionAPI api) {
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) return false;
        
        // 检查是否已解锁
        if (data.isHiddenProfessionUnlocked()) {
            return false;
        }
        
        // 检查条件1：3个基础职业全部满级
        if (!hasAllBasicProfessionsMastered(data)) {
            return false;
        }
        
        // 检查条件2：收集齐5种稀有Factor
        if (data.getCollectedRareFactorCount() < REQUIRED_RARE_FACTORS) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查玩家是否已解锁隐藏职业
     */
    public static boolean isUnlocked(ServerPlayerEntity player, ProfessionAPI api) {
        PlayerProfessionData data = api.getPlayerData(player);
        return data != null && data.isHiddenProfessionUnlocked();
    }
    
    /**
     * 检查是否所有基础职业都已满级
     */
    public static boolean hasAllBasicProfessionsMastered(PlayerProfessionData data) {
        return data.hasMasteredProfession(ProfessionType.ENGINEER) &&
               data.hasMasteredProfession(ProfessionType.CULTIVATOR) &&
               data.hasMasteredProfession(ProfessionType.EXPLORER);
    }
    
    /**
     * 获取解锁进度描述
     */
    public static UnlockProgress getUnlockProgress(ServerPlayerEntity player, ProfessionAPI api) {
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) {
            return new UnlockProgress(false, false, false, 0, false);
        }
        
        boolean hasEngineer = data.hasMasteredProfession(ProfessionType.ENGINEER);
        boolean hasCultivator = data.hasMasteredProfession(ProfessionType.CULTIVATOR);
        boolean hasExplorer = data.hasMasteredProfession(ProfessionType.EXPLORER);
        int rareFactorCount = data.getCollectedRareFactorCount();
        boolean unlocked = data.isHiddenProfessionUnlocked();
        
        return new UnlockProgress(hasEngineer, hasCultivator, hasExplorer, rareFactorCount, unlocked);
    }
    
    /**
     * 尝试解锁隐藏职业
     * 
     * @param player 玩家
     * @param api 职业API
     * @return 解锁结果
     */
    public static UnlockResult tryUnlock(ServerPlayerEntity player, ProfessionAPI api) {
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) {
            return UnlockResult.failed("无法获取玩家职业数据");
        }
        
        // 检查是否已解锁
        if (data.isHiddenProfessionUnlocked()) {
            return UnlockResult.failed("已经解锁了隐藏职业");
        }
        
        // 检查解锁条件
        if (!hasAllBasicProfessionsMastered(data)) {
            return UnlockResult.failed("需要3个基础职业全部达到满级");
        }
        
        if (data.getCollectedRareFactorCount() < REQUIRED_RARE_FACTORS) {
            return UnlockResult.failed("需要收集" + REQUIRED_RARE_FACTORS + "种稀有Factor");
        }
        
        // 执行解锁
        data.unlockHiddenProfession();
        
        // 记录历史天赋点（保留之前职业的天赋点）
        data.setHistoricalTalentPoints(data.getTalentPoints());
        
        // 额外获得10个专属天赋点
        data.addTalentPoints(BONUS_TALENT_POINTS);
        
        // 播放解锁效果
        playUnlockEffect(player);
        
        LOGGER.info("玩家 {} 解锁了隐藏职业「因子掌控者」", player.getName().getString());
        
        return UnlockResult.success("恭喜你解锁了隐藏职业「因子掌控者」！获得" + BONUS_TALENT_POINTS + "个专属天赋点！");
    }
    
    /**
     * 记录玩家职业达到满级
     * 当玩家切换职业时，如果该职业已满级，记录到已满级职业列表
     */
    public static void recordMasteredProfession(ServerPlayerEntity player, ProfessionAPI api, ProfessionType type) {
        if (type == null || type.isHidden()) return;
        
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) return;
        
        // 检查当前职业等级
        if (data.getLevel() >= MASTERY_LEVEL && data.getProfessionType() == type) {
            if (!data.hasMasteredProfession(type)) {
                data.addMasteredProfession(type);
                LOGGER.info("玩家 {} 的 {} 职业达到满级", player.getName().getString(), type.getDisplayName());
                
                // 检查是否满足解锁条件
                if (canUnlock(player, api)) {
                    player.sendMessage(Text.literal("§d§l[隐藏职业] §e你已满足解锁条件！使用职业命令进行转职！"), false);
                }
            }
        }
    }
    
    /**
     * 记录玩家收集稀有Factor
     */
    public static void recordRareFactor(ServerPlayerEntity player, ProfessionAPI api, String factorId) {
        PlayerProfessionData data = api.getPlayerData(player);
        if (data == null) return;
        
        if (!data.hasCollectedRareFactor(factorId)) {
            data.addCollectedRareFactor(factorId);
            int count = data.getCollectedRareFactorCount();
            LOGGER.info("玩家 {} 收集了稀有Factor: {} ({}/{})", 
                player.getName().getString(), factorId, count, REQUIRED_RARE_FACTORS);
            
            // 检查是否满足解锁条件
            if (count >= REQUIRED_RARE_FACTORS && canUnlock(player, api)) {
                player.sendMessage(Text.literal("§d§l[隐藏职业] §e你已满足解锁条件！使用职业命令进行转职！"), false);
            }
        }
    }
    
    /**
     * 播放解锁效果
     */
    private static void playUnlockEffect(ServerPlayerEntity player) {
        // 粒子效果
        for (int i = 0; i < 50; i++) {
            double dx = (player.getRandom().nextDouble() - 0.5) * 2;
            double dy = player.getRandom().nextDouble() * 2;
            double dz = (player.getRandom().nextDouble() - 0.5) * 2;
            player.getServerWorld().spawnParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX() + dx,
                player.getY() + dy,
                player.getZ() + dz,
                1, 0, 0, 0, 0
            );
        }
        
        // 音效
        player.getServerWorld().playSound(
            null,
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
            SoundCategory.PLAYERS,
            1.0f, 1.0f
        );
        
        // 发送消息
        player.sendMessage(Text.literal("§d§l✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦"), false);
        player.sendMessage(Text.literal("§d§l     恭喜解锁隐藏职业"), false);
        player.sendMessage(Text.literal("§f§l       因子掌控者"), false);
        player.sendMessage(Text.literal("§d§l✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦ ✦"), false);
    }
    
    /**
     * 解锁进度记录
     */
    public static class UnlockProgress {
        public final boolean hasEngineerMastered;
        public final boolean hasCultivatorMastered;
        public final boolean hasExplorerMastered;
        public final int rareFactorCount;
        public final boolean unlocked;
        
        public UnlockProgress(boolean engineer, boolean cultivator, boolean explorer, int factorCount, boolean unlocked) {
            this.hasEngineerMastered = engineer;
            this.hasCultivatorMastered = cultivator;
            this.hasExplorerMastered = explorer;
            this.rareFactorCount = factorCount;
            this.unlocked = unlocked;
        }
        
        public int getMasteredCount() {
            int count = 0;
            if (hasEngineerMastered) count++;
            if (hasCultivatorMastered) count++;
            if (hasExplorerMastered) count++;
            return count;
        }
        
        public String getProgressText() {
            return String.format("职业满级: %d/3, 稀有Factor: %d/%d", 
                getMasteredCount(), rareFactorCount, REQUIRED_RARE_FACTORS);
        }
    }
    
    /**
     * 解锁结果
     */
    public static class UnlockResult {
        public final boolean success;
        public final String message;
        
        private UnlockResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static UnlockResult success(String message) {
            return new UnlockResult(true, message);
        }
        
        public static UnlockResult failed(String message) {
            return new UnlockResult(false, message);
        }
    }
}