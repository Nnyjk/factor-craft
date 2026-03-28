package com.factorcraft.module.core.achievement;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 成就进度追踪类
 * 用于记录玩家各成就的当前进度
 */
public class AchievementProgress {
    
    // 成就 ID -> 当前进度
    private final Map<String, Integer> progressMap;
    
    // 已解锁的成就 ID 集合
    private final Map<String, Boolean> unlockedMap;
    
    public AchievementProgress() {
        this.progressMap = new HashMap<>();
        this.unlockedMap = new HashMap<>();
    }
    
    /**
     * 更新成就进度
     * @param achievementId 成就 ID
     * @param amount 进度增量
     * @return 更新后的总进度
     */
    public int addProgress(String achievementId, int amount) {
        int current = progressMap.getOrDefault(achievementId, 0);
        int newValue = current + amount;
        progressMap.put(achievementId, newValue);
        return newValue;
    }
    
    /**
     * 设置成就进度
     */
    public void setProgress(String achievementId, int amount) {
        progressMap.put(achievementId, amount);
    }
    
    /**
     * 获取成就当前进度
     */
    public int getProgress(String achievementId) {
        return progressMap.getOrDefault(achievementId, 0);
    }
    
    /**
     * 解锁成就
     */
    public void unlock(String achievementId) {
        unlockedMap.put(achievementId, true);
    }
    
    /**
     * 检查成就是否已解锁
     */
    public boolean isUnlocked(String achievementId) {
        return unlockedMap.getOrDefault(achievementId, false);
    }
    
    /**
     * 获取所有已解锁成就
     */
    public Map<String, Boolean> getUnlockedAchievements() {
        return new HashMap<>(unlockedMap);
    }
    
    /**
     * 检查是否可以解锁成就（检查前置成就）
     */
    public boolean canUnlock(Achievement achievement) {
        // 检查前置成就
        for (Identifier prereq : achievement.getPrerequisites()) {
            if (!isUnlocked(prereq.toString())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        // 保存进度
        NbtList progressList = new NbtList();
        for (Map.Entry<String, Integer> entry : progressMap.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString("id", entry.getKey());
            entryNbt.putInt("progress", entry.getValue());
            progressList.add(entryNbt);
        }
        nbt.put("progress", progressList);
        
        // 保存已解锁成就
        NbtList unlockedList = new NbtList();
        for (String id : unlockedMap.keySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString("id", id);
            unlockedList.add(entryNbt);
        }
        nbt.put("unlocked", unlockedList);
        
        return nbt;
    }
    
    /**
     * 从 NBT 读取
     */
    public static AchievementProgress fromNbt(NbtCompound nbt) {
        AchievementProgress progress = new AchievementProgress();
        
        // 读取进度
        if (nbt.contains("progress")) {
            NbtList progressList = nbt.getList("progress", 10); // CompoundNbt type
            for (int i = 0; i < progressList.size(); i++) {
                NbtCompound entryNbt = progressList.getCompound(i);
                String id = entryNbt.getString("id");
                int amount = entryNbt.getInt("progress");
                progress.progressMap.put(id, amount);
            }
        }
        
        // 读取已解锁成就
        if (nbt.contains("unlocked")) {
            NbtList unlockedList = nbt.getList("unlocked", 10);
            for (int i = 0; i < unlockedList.size(); i++) {
                NbtCompound entryNbt = unlockedList.getCompound(i);
                String id = entryNbt.getString("id");
                progress.unlockedMap.put(id, true);
            }
        }
        
        return progress;
    }
    
    /**
     * 写入网络包
     */
    public void toPacket(PacketByteBuf buf) {
        // 写入进度
        buf.writeInt(progressMap.size());
        for (Map.Entry<String, Integer> entry : progressMap.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }
        
        // 写入已解锁成就
        buf.writeInt(unlockedMap.size());
        for (String id : unlockedMap.keySet()) {
            buf.writeString(id);
        }
    }
    
    /**
     * 从网络包读取
     */
    public static AchievementProgress fromPacket(PacketByteBuf buf) {
        AchievementProgress progress = new AchievementProgress();
        
        // 读取进度
        int progressCount = buf.readInt();
        for (int i = 0; i < progressCount; i++) {
            String id = buf.readString();
            int amount = buf.readInt();
            progress.progressMap.put(id, amount);
        }
        
        // 读取已解锁成就
        int unlockedCount = buf.readInt();
        for (int i = 0; i < unlockedCount; i++) {
            String id = buf.readString();
            progress.unlockedMap.put(id, true);
        }
        
        return progress;
    }
    
    /**
     * 重置所有进度
     */
    public void reset() {
        progressMap.clear();
        unlockedMap.clear();
    }
}
