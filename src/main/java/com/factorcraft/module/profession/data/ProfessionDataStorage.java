package com.factorcraft.module.profession.data;

import com.factorcraft.module.profession.model.PlayerProfessionData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 职业数据持久化存储
 */
public class ProfessionDataStorage extends PersistentState {
    
    private static final String KEY = "factorcraft_profession_data";
    
    private final Map<UUID, PlayerProfessionData> playerDataMap = new HashMap<>();
    
    public ProfessionDataStorage() {
    }
    
    /**
     * 从 NBT 加载
     */
    public ProfessionDataStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (String key : nbt.getKeys()) {
            try {
                UUID playerId = UUID.fromString(key);
                NbtCompound playerNbt = nbt.getCompound(key);
                PlayerProfessionData data = new PlayerProfessionData();
                data.readNbt(playerNbt);
                playerDataMap.put(playerId, data);
            } catch (IllegalArgumentException e) {
                // 忽略无效的 UUID
            }
        }
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (Map.Entry<UUID, PlayerProfessionData> entry : playerDataMap.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            entry.getValue().writeNbt(playerNbt);
            nbt.put(entry.getKey().toString(), playerNbt);
        }
        return nbt;
    }
    
    public PlayerProfessionData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, id -> new PlayerProfessionData());
    }
    
    public PlayerProfessionData getPlayerData(ServerPlayerEntity player) {
        return getPlayerData(player.getUuid());
    }
    
    public void setPlayerData(UUID playerId, PlayerProfessionData data) {
        playerDataMap.put(playerId, data);
        markDirty();
    }
    
    public void setPlayerData(ServerPlayerEntity player, PlayerProfessionData data) {
        setPlayerData(player.getUuid(), data);
    }
    
    public Optional<PlayerProfessionData> getData(UUID playerId) {
        return Optional.ofNullable(playerDataMap.get(playerId));
    }
    
    public boolean hasData(UUID playerId) {
        return playerDataMap.containsKey(playerId);
    }
    
    public void removeData(UUID playerId) {
        playerDataMap.remove(playerId);
        markDirty();
    }
    
    // ==================== 静态访问方法 ====================
    
    /**
     * 从世界获取存储实例
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
    
    /**
     * 便捷方法：获取玩家职业数据
     */
    public static PlayerProfessionData getPlayerProfessionData(ServerWorld world, UUID playerId) {
        return get(world).getPlayerData(playerId);
    }
    
    /**
     * 便捷方法：获取玩家职业数据
     */
    public static PlayerProfessionData getPlayerProfessionData(ServerWorld world, ServerPlayerEntity player) {
        return get(world).getPlayerData(player);
    }
}