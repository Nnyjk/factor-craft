package com.factorcraft.module.profession.data;

import com.factorcraft.module.profession.model.PlayerProfessionData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 职业数据持久化存储
 * 
 * 使用 PersistentState 机制将玩家职业数据存储到世界数据中
 */
public class ProfessionDataStorage extends PersistentState {
    
    private static final String KEY = "factorcraft_profession_data";
    
    private final Map<UUID, PlayerProfessionData> playerDataMap = new HashMap<>();
    
    public ProfessionDataStorage() {
    }
    
    // ==================== PersistentState 实现 ====================
    
    /**
     * 从 NBT 加载数据
     */
    public ProfessionDataStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound dataNbt = nbt.getCompound("player_data");
        for (String key : dataNbt.getKeys()) {
            try {
                UUID playerId = UUID.fromString(key);
                NbtCompound playerNbt = dataNbt.getCompound(key);
                PlayerProfessionData data = new PlayerProfessionData();
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
        for (Map.Entry<UUID, PlayerProfessionData> entry : playerDataMap.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            entry.getValue().writeNbt(playerNbt);
            dataNbt.put(entry.getKey().toString(), playerNbt);
        }
        nbt.put("player_data", dataNbt);
        return nbt;
    }
    
    // ==================== 数据访问 ====================
    
    /**
     * 获取玩家职业数据
     */
    public PlayerProfessionData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, id -> new PlayerProfessionData());
    }
    
    /**
     * 获取所有玩家数据
     */
    public Map<UUID, PlayerProfessionData> getAllPlayerData() {
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
    
    // ==================== 静态访问方法 ====================
    
    /**
     * 获取职业数据存储实例
     */
    public static ProfessionDataStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            new PersistentState.Type<>(
                ProfessionDataStorage::new,
                ProfessionDataStorage::new,
                null
            ),
            KEY
        );
    }
}