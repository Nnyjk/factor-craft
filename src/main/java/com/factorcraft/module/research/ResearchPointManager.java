package com.factorcraft.module.research;

import com.factorcraft.module.research.network.ResearchPointSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 研究点管理器
 * 
 * 管理研究点的获取和消耗
 */
public class ResearchPointManager {
    
    private static ResearchPointManager instance;
    
    // 玩家研究点存储
    private final Map<java.util.UUID, ResearchPointStorage> playerStorages;
    
    public ResearchPointManager() {
        this.playerStorages = new ConcurrentHashMap<>();
        instance = this;
    }
    
    public static ResearchPointManager getInstance() {
        return instance;
    }
    
    /**
     * 获取玩家研究点存储
     */
    public ResearchPointStorage getStorage(PlayerEntity player) {
        return playerStorages.computeIfAbsent(player.getUuid(), 
            uuid -> new ResearchPointStorage(uuid));
    }
    
    /**
     * 获取玩家当前研究点
     */
    public int getPoints(PlayerEntity player) {
        return getStorage(player).getCurrentPoints();
    }
    
    /**
     * 添加研究点
     */
    public void addPoints(PlayerEntity player, int amount, String source) {
        if (amount <= 0) return;
        
        ResearchPointStorage storage = getStorage(player);
        storage.addPoints(amount, source);
        
        // 通知客户端更新
        if (player instanceof ServerPlayerEntity serverPlayer) {
            sendUpdate(serverPlayer);
            
            // 发送提示消息
            serverPlayer.sendMessage(
                Text.literal("§6§l[研究点] §r§e获得 " + amount + " 研究点 (" + getSourceName(source) + ")"),
                true
            );
        }
    }
    
    /**
     * 消耗研究点
     * @return 是否成功
     */
    public boolean consumePoints(PlayerEntity player, int amount) {
        ResearchPointStorage storage = getStorage(player);
        boolean success = storage.consumePoints(amount);
        
        if (success && player instanceof ServerPlayerEntity serverPlayer) {
            sendUpdate(serverPlayer);
        }
        
        return success;
    }
    
    /**
     * 检查研究点是否足够
     */
    public boolean hasPoints(PlayerEntity player, int amount) {
        return getStorage(player).hasPoints(amount);
    }
    
    /**
     * 发送更新到客户端
     */
    private void sendUpdate(ServerPlayerEntity player) {
        ResearchPointStorage storage = getStorage(player);
        ResearchPointSyncPayload payload = ResearchPointSyncPayload.of(
            player.getUuid(),
            storage.getCurrentPoints(),
            storage.getTotalEarned(),
            storage.getTotalSpent(),
            storage.getPointsBySource()
        );
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 处理任务完成事件 - 奖励研究点
     */
    public void onQuestCompleted(ServerPlayerEntity player, String questId, int tier) {
        // 根据任务等级给予研究点
        int points = switch (tier) {
            case 1 -> 5;    // T1: 5 点
            case 2 -> 10;   // T2: 10 点
            case 3 -> 20;   // T3: 20 点
            case 4 -> 35;   // T4: 35 点
            case 5 -> 50;   // T5: 50 点
            default -> 5;
        };
        addPoints(player, points, ResearchPointStorage.SOURCE_QUEST);
    }
    
    /**
     * 处理成就解锁事件 - 奖励研究点
     */
    public void onAchievementUnlocked(ServerPlayerEntity player, String achievementId, boolean isHidden) {
        int points = isHidden ? 25 : 10;  // 隐藏成就额外奖励
        addPoints(player, points, ResearchPointStorage.SOURCE_ACHIEVEMENT);
    }
    
    /**
     * 处理 Factor 合成 - 奖励研究点
     */
    public void onFactorSynthesized(ServerPlayerEntity player, String factorId, int amount) {
        // 根据 Factor 等级给予研究点
        int points = calculateSynthesisPoints(factorId, amount);
        addPoints(player, points, ResearchPointStorage.SOURCE_SYNTHESIS);
    }
    
    /**
     * 处理首次合成 - 额外奖励
     */
    public void onFirstCraft(ServerPlayerEntity player, String factorId) {
        int points = 15;  // 首研奖励 15 点
        addPoints(player, points, ResearchPointStorage.SOURCE_FIRST_CRAFT);
    }
    
    /**
     * 计算合成研究点
     */
    private int calculateSynthesisPoints(String factorId, int amount) {
        // 根据 Factor ID 判断等级
        // 简化实现：根据 ID 中的 tier 标识
        int tier = 1;
        if (factorId.contains("t2") || factorId.contains("tier2")) tier = 2;
        else if (factorId.contains("t3") || factorId.contains("tier3")) tier = 3;
        else if (factorId.contains("t4") || factorId.contains("tier4")) tier = 4;
        else if (factorId.contains("t5") || factorId.contains("tier5")) tier = 5;
        
        // 基础点数 * 等级系数 * 数量
        int basePoints = 2;
        int tierMultiplier = switch (tier) {
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 8;
            case 5 -> 16;
            default -> 1;
        };
        
        return basePoints * tierMultiplier * amount;
    }
    
    /**
     * 获取研究点来源名称
     */
    private String getSourceName(String source) {
        return switch (source) {
            case ResearchPointStorage.SOURCE_SYNTHESIS -> "Factor 合成";
            case ResearchPointStorage.SOURCE_QUEST -> "任务完成";
            case ResearchPointStorage.SOURCE_ACHIEVEMENT -> "成就解锁";
            case ResearchPointStorage.SOURCE_FIRST_CRAFT -> "首次合成";
            case ResearchPointStorage.SOURCE_EVENT -> "事件奖励";
            case ResearchPointStorage.SOURCE_COMMAND -> "命令给予";
            default -> source;
        };
    }
    
    /**
     * 清除玩家数据 (玩家离线时)
     */
    public void removePlayer(java.util.UUID playerId) {
        playerStorages.remove(playerId);
    }
}
