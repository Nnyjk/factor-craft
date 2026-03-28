package com.factorcraft.module.core.achievement;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.core.achievement.trigger.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * 成就管理器
 * 单例模式，管理所有成就的注册、查询、解锁和同步
 */
public class AchievementManager {
    
    private static AchievementManager instance;
    
    // 已注册的成就
    private final Map<Identifier, Achievement> registeredAchievements;
    
    // 按分类组织的成就
    private final Map<AchievementCategory, List<Achievement>> achievementsByCategory;
    
    // 玩家成就进度（服务器端）
    private final Map<UUID, AchievementProgress> playerProgress;
    
    private AchievementManager() {
        this.registeredAchievements = new HashMap<>();
        this.achievementsByCategory = new HashMap<>();
        this.playerProgress = new HashMap<>();
        
        // 初始化分类列表
        for (AchievementCategory category : AchievementCategory.values()) {
            achievementsByCategory.put(category, new ArrayList<>());
        }
    }
    
    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }
    
    /**
     * 注册成就
     */
    public void register(Achievement achievement) {
        registeredAchievements.put(achievement.getId(), achievement);
        achievementsByCategory.get(achievement.getCategory()).add(achievement);
        FactorCraftMod.LOGGER.info("Registered achievement: {}", achievement.getId());
    }
    
    /**
     * 批量注册成就
     */
    public void registerAll(Achievement... achievements) {
        for (Achievement achievement : achievements) {
            register(achievement);
        }
    }
    
    /**
     * 根据 ID 获取成就
     */
    public Optional<Achievement> getAchievement(Identifier id) {
        return Optional.ofNullable(registeredAchievements.get(id));
    }
    
    /**
     * 获取所有成就
     */
    public Collection<Achievement> getAllAchievements() {
        return registeredAchievements.values();
    }
    
    /**
     * 按分类获取成就
     */
    public List<Achievement> getAchievementsByCategory(AchievementCategory category) {
        return achievementsByCategory.get(category);
    }
    
    /**
     * 获取玩家进度
     */
    public AchievementProgress getPlayerProgress(UUID playerId) {
        return playerProgress.computeIfAbsent(playerId, k -> new AchievementProgress());
    }
    
    /**
     * 更新成就进度并检查是否解锁
     * @param playerId 玩家 UUID
     * @param achievementId 成就 ID
     * @param amount 进度增量
     * @return 是否新解锁了成就
     */
    public boolean updateProgress(UUID playerId, Identifier achievementId, int amount) {
        AchievementProgress progress = getPlayerProgress(playerId);
        Optional<Achievement> achievement = getAchievement(achievementId);
        
        if (achievement.isEmpty()) {
            return false;
        }
        
        int newProgress = progress.addProgress(achievementId.toString(), amount);
        
        // 检查是否可以解锁
        if (!progress.isUnlocked(achievementId.toString()) && 
            achievement.get().isCompleted(newProgress) &&
            progress.canUnlock(achievement.get())) {
            progress.unlock(achievementId.toString());
            return true;
        }
        
        return false;
    }
    
    /**
     * 直接解锁成就（用于完成特定任务）
     */
    public boolean unlockAchievement(UUID playerId, Identifier achievementId) {
        AchievementProgress progress = getPlayerProgress(playerId);
        Optional<Achievement> achievement = getAchievement(achievementId);
        
        if (achievement.isEmpty() || progress.isUnlocked(achievementId.toString())) {
            return false;
        }
        
        if (progress.canUnlock(achievement.get())) {
            progress.unlock(achievementId.toString());
            return true;
        }
        
        return false;
    }
    
    /**
     * 通知玩家成就解锁
     */
    public void notifyUnlock(ServerPlayerEntity player, Identifier achievementId) {
        getAchievement(achievementId).ifPresent(achievement -> {
            // 发送聊天消息
            player.sendMessage(Text.translatable("achievement.factor_craft.unlocked", achievement.getTitle()), true);
            
            // 发送成就解锁包给客户端显示 Toast
            // 客户端收到后会显示 AchievementToast
            // TODO: 实现网络包发送
        });
    }
    
    /**
     * 保存玩家成就进度到 NBT
     */
    public NbtCompound savePlayerData(UUID playerId) {
        AchievementProgress progress = getPlayerProgress(playerId);
        return progress.toNbt();
    }
    
    /**
     * 从 NBT 加载玩家成就进度
     */
    public void loadPlayerData(UUID playerId, NbtCompound nbt) {
        AchievementProgress progress = AchievementProgress.fromNbt(nbt);
        playerProgress.put(playerId, progress);
    }
    
    /**
     * 同步成就数据到客户端
     */
    public void syncToClient(ServerPlayerEntity player) {
        AchievementProgress progress = getPlayerProgress(player.getUuid());
        // TODO: 发送同步包
    }
    
    /**
     * 清除玩家进度（用于测试或重置）
     */
    public void clearPlayerProgress(UUID playerId) {
        playerProgress.remove(playerId);
    }
    
    /**
     * 获取成就总数
     */
    public int getTotalAchievements() {
        return registeredAchievements.size();
    }
    
    /**
     * 获取玩家已解锁成就数
     */
    public int getUnlockedCount(UUID playerId) {
        AchievementProgress progress = getPlayerProgress(playerId);
        return progress.getUnlockedAchievements().size();
    }
    
    // ========== 触发器集成方法 ==========
    
    /**
     * 触发 Factor 生产事件
     */
    public void onFactorProduction(ServerPlayerEntity player, String factorType, int amount, String source) {
        FactorProductionData data = new FactorProductionData(factorType, amount, source);
        TriggerRegistry.getInstance().fireEvent(player, TriggerType.FACTOR_PRODUCTION, data);
        
        // 更新相关成就进度
        updateProgressForTrigger(player.getUuid(), TriggerType.FACTOR_PRODUCTION, factorType, amount);
    }
    
    /**
     * 触发机器制作事件
     */
    public void onMachineCraft(ServerPlayerEntity player, String machineId, int tier) {
        MachineCraftData data = new MachineCraftData(machineId, tier);
        TriggerRegistry.getInstance().fireEvent(player, TriggerType.MACHINE_CRAFT, data);
        
        // 更新相关成就进度
        updateProgressForTrigger(player.getUuid(), TriggerType.MACHINE_CRAFT, machineId, 1);
    }
    
    /**
     * 触发任务完成事件
     */
    public void onQuestComplete(ServerPlayerEntity player, String questId, String category, boolean isMainQuest) {
        QuestCompleteData data = new QuestCompleteData(questId, category, isMainQuest);
        TriggerRegistry.getInstance().fireEvent(player, TriggerType.QUEST_COMPLETE, data);
        
        // 更新相关成就进度
        updateProgressForTrigger(player.getUuid(), TriggerType.QUEST_COMPLETE, questId, 1);
    }
    
    /**
     * 触发 Boss 击杀事件
     */
    public void onBossKill(ServerPlayerEntity player, String bossId, String bossType, int level) {
        BossKillData data = new BossKillData(bossId, bossType, level);
        TriggerRegistry.getInstance().fireEvent(player, TriggerType.BOSS_KILL, data);
        
        // 更新相关成就进度
        updateProgressForTrigger(player.getUuid(), TriggerType.BOSS_KILL, bossId, 1);
    }
    
    /**
     * 触发探索事件
     */
    public void onExploration(ServerPlayerEntity player, String dimension, String structure, double x, double z) {
        ExplorationData data = new ExplorationData(dimension, structure, x, z);
        TriggerRegistry.getInstance().fireEvent(player, TriggerType.EXPLORATION, data);
        
        // 更新相关成就进度
        updateProgressForTrigger(player.getUuid(), TriggerType.EXPLORATION, dimension, 1);
    }
    
    /**
     * 根据触发器类型更新成就进度
     * 自动更新所有匹配分类的成就进度
     */
    private void updateProgressForTrigger(UUID playerId, TriggerType type, String key, int amount) {
        // 遍历所有成就，更新分类匹配的成就进度
        for (Achievement achievement : getAllAchievements()) {
            if (achievement.getCategory().matchesTriggerType(type)) {
                // 对于简单成就，直接增加进度
                // 具体成就的过滤逻辑在 Step 3 预定义成就中实现
                updateProgress(playerId, achievement.getId(), amount);
            }
        }
    }
}
