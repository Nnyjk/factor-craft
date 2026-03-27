package com.factorcraft.module.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 研究点存储 - 玩家研究点数据
 * 
 * 使用 PlayerComponent 模式存储玩家的研究点
 */
public class ResearchPointStorage {
    
    public static final Codec<ResearchPointStorage> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("playerId").forGetter(ResearchPointStorage::getPlayerId),
            Codec.INT.fieldOf("currentPoints").forGetter(ResearchPointStorage::getCurrentPoints),
            Codec.INT.fieldOf("totalEarned").forGetter(ResearchPointStorage::getTotalEarned),
            Codec.INT.fieldOf("totalSpent").forGetter(ResearchPointStorage::getTotalSpent),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("pointsBySource").forGetter(ResearchPointStorage::getPointsBySource)
        ).apply(instance, (playerId, currentPoints, totalEarned, totalSpent, pointsBySource) -> {
            ResearchPointStorage storage = new ResearchPointStorage(playerId);
            storage.currentPoints = currentPoints;
            storage.totalEarned = totalEarned;
            storage.totalSpent = totalSpent;
            storage.pointsBySource.putAll(pointsBySource);
            return storage;
        })
    );
    
    private final UUID playerId;
    
    // 当前研究点
    private int currentPoints;
    
    // 累计获得研究点 (统计用)
    private int totalEarned;
    
    // 累计消耗研究点 (统计用)
    private int totalSpent;
    
    // 各来源研究点统计
    private final Map<String, Integer> pointsBySource;
    
    public ResearchPointStorage(UUID playerId) {
        this.playerId = playerId;
        this.currentPoints = 0;
        this.totalEarned = 0;
        this.totalSpent = 0;
        this.pointsBySource = new HashMap<>();
    }
    
    // ==================== 研究点操作 ====================
    
    /**
     * 添加研究点
     */
    public void addPoints(int amount, String source) {
        if (amount <= 0) return;
        
        this.currentPoints += amount;
        this.totalEarned += amount;
        
        // 更新来源统计
        pointsBySource.merge(source, amount, Integer::sum);
    }
    
    /**
     * 消耗研究点
     * @return 是否成功消耗
     */
    public boolean consumePoints(int amount) {
        if (amount <= 0) return true;
        if (this.currentPoints < amount) return false;
        
        this.currentPoints -= amount;
        this.totalSpent += amount;
        return true;
    }
    
    /**
     * 检查是否有足够研究点
     */
    public boolean hasPoints(int amount) {
        return this.currentPoints >= amount;
    }
    
    // ==================== Getters ====================
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public int getCurrentPoints() {
        return currentPoints;
    }
    
    public int getTotalEarned() {
        return totalEarned;
    }
    
    public int getTotalSpent() {
        return totalSpent;
    }
    
    public Map<String, Integer> getPointsBySource() {
        return new HashMap<>(pointsBySource);
    }
    
    public int getPointsFromSource(String source) {
        return pointsBySource.getOrDefault(source, 0);
    }
    
    // ==================== NBT 序列化 ====================
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("CurrentPoints", currentPoints);
        nbt.putInt("TotalEarned", totalEarned);
        nbt.putInt("TotalSpent", totalSpent);
        
        // 序列化来源统计
        NbtCompound sourceNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : pointsBySource.entrySet()) {
            sourceNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("PointsBySource", sourceNbt);
        
        return nbt;
    }
    
    public void fromNbt(NbtCompound nbt) {
        this.currentPoints = nbt.getInt("CurrentPoints");
        this.totalEarned = nbt.getInt("TotalEarned");
        this.totalSpent = nbt.getInt("TotalSpent");
        
        this.pointsBySource.clear();
        NbtCompound sourceNbt = nbt.getCompound("PointsBySource");
        for (String key : sourceNbt.getKeys()) {
            this.pointsBySource.put(key, sourceNbt.getInt(key));
        }
    }
    
    // ==================== 研究点来源常量 ====================
    
    public static final String SOURCE_SYNTHESIS = "synthesis";      // Factor 合成
    public static final String SOURCE_QUEST = "quest";              // 任务完成
    public static final String SOURCE_ACHIEVEMENT = "achievement";  // 成就解锁
    public static final String SOURCE_FIRST_CRAFT = "first_craft";  // 首研奖励
    public static final String SOURCE_EVENT = "event";              // 事件奖励
    public static final String SOURCE_COMMAND = "command";          // 命令给予
}
