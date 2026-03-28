package com.factorcraft.module.core.achievement;

import com.factorcraft.FactorCraftMod;
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
            // 发送 Toast 通知
            player.sendMessage(Text.translatable("achievement.factor_craft.unlocked", achievement.getTitle()), true);
            
            // TODO: 发送成就解锁包给客户端显示 Toast
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
}
