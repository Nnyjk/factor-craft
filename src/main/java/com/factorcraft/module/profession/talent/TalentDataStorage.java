package com.factorcraft.module.profession.talent;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 天赋数据持久化存储
 * 
 * 使用 PersistentState 机制将玩家天赋数据存储到世界数据中
 */
public class TalentDataStorage extends PersistentState {
    
    private static final String KEY = "factorcraft_talent_data";
    
    private final Map<UUID, PlayerTalentData> playerDataMap = new HashMap<>();
    
    public TalentDataStorage() {
    }
    
    // ==================== PersistentState 实现 ====================
    
    /**
     * 从 NBT 加载数据
     */
    public TalentDataStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound dataNbt = nbt.getCompound("player_data");
        for (String key : dataNbt.getKeys()) {
            try {
                UUID playerId = UUID.fromString(key);
                NbtCompound playerNbt = dataNbt.getCompound(key);
                PlayerTalentData data = new PlayerTalentData();
                data.readNbt(playerNbt);
                playerDataMap.put(playerId, data);
            } catch (IllegalArgumentException e) {
                // 忽略无效的UUID
            }
        }
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound dataNbt = new NbtCompound();
        for (Map.Entry<UUID, PlayerTalentData> entry : playerDataMap.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            entry.getValue().writeNbt(playerNbt);
            dataNbt.put(entry.getKey().toString(), playerNbt);
        }
        nbt.put("player_data", dataNbt);
        return nbt;
    }
    
    // ==================== 数据访问 ====================
    
    /**
     * 获取玩家天赋数据
     */
    public PlayerTalentData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, id -> new PlayerTalentData());
    }
    
    /**
     * 获取所有玩家数据
     */
    public Map<UUID, PlayerTalentData> getAllPlayerData() {
        return playerDataMap;
    }
    
    /**
     * 清除玩家数据
     */
    public void clearPlayerData(UUID playerId) {
        playerDataMap.remove(playerId);
        markDirty();
    }
    
    /**
     * 清除所有数据
     */
    public void clearAll() {
        playerDataMap.clear();
        markDirty();
    }
    
    /**
     * 标记玩家数据已修改
     */
    public void markPlayerDirty(UUID playerId) {
        markDirty();
    }
    
    // ==================== 静态访问方法 ====================
    
    /**
     * 获取天赋数据存储实例
     */
    public static TalentDataStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            new PersistentState.Type<>(
                TalentDataStorage::new,
                TalentDataStorage::new,
                null
            ),
            KEY
        );
    }
}