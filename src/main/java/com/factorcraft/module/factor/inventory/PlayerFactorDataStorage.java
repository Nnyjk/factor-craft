package com.factorcraft.module.factor.inventory;

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
 * 玩家 Factor 数据持久化存储
 * 
 * 使用 PersistentState 存储玩家的 Factor 背包数据
 * 支持玩家死亡重生后数据保留
 */
public class PlayerFactorDataStorage extends PersistentState {
    
    private static final String KEY = "factorcraft_factor_data";
    
    private final Map<UUID, PlayerFactorInventory> playerDataMap = new HashMap<>();
    
    public PlayerFactorDataStorage() {
    }
    
    /**
     * 从 NBT 加载
     */
    public PlayerFactorDataStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (String key : nbt.getKeys()) {
            try {
                UUID playerId = UUID.fromString(key);
                NbtCompound playerNbt = nbt.getCompound(key);
                PlayerFactorInventory inventory = new PlayerFactorInventory();
                inventory.readNbt(playerNbt);
                playerDataMap.put(playerId, inventory);
            } catch (IllegalArgumentException e) {
                // 忽略无效的 UUID
            }
        }
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        for (Map.Entry<UUID, PlayerFactorInventory> entry : playerDataMap.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            entry.getValue().writeNbt(playerNbt);
            nbt.put(entry.getKey().toString(), playerNbt);
        }
        return nbt;
    }
    
    /**
     * 获取玩家 Factor 背包
     * 
     * @param playerId 玩家 UUID
     * @return 玩家 Factor 背包（不存在则创建新的）
     */
    public PlayerFactorInventory getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, id -> new PlayerFactorInventory());
    }
    
    /**
     * 获取玩家 Factor 背包
     * 
     * @param player 玩家
     * @return 玩家 Factor 背包
     */
    public PlayerFactorInventory getPlayerData(ServerPlayerEntity player) {
        return getPlayerData(player.getUuid());
    }
    
    /**
     * 设置玩家 Factor 背包
     * 
     * @param playerId 玩家 UUID
     * @param inventory Factor 背包
     */
    public void setPlayerData(UUID playerId, PlayerFactorInventory inventory) {
        playerDataMap.put(playerId, inventory);
        markDirty();
    }
    
    /**
     * 设置玩家 Factor 背包
     * 
     * @param player 玩家
     * @param inventory Factor 背包
     */
    public void setPlayerData(ServerPlayerEntity player, PlayerFactorInventory inventory) {
        setPlayerData(player.getUuid(), inventory);
    }
    
    /**
     * 获取玩家 Factor 背包（Optional 版本）
     * 
     * @param playerId 玩家 UUID
     * @return Optional 包装的 Factor 背包
     */
    public Optional<PlayerFactorInventory> getData(UUID playerId) {
        return Optional.ofNullable(playerDataMap.get(playerId));
    }
    
    /**
     * 检查玩家是否有数据
     * 
     * @param playerId 玩家 UUID
     * @return 是否有数据
     */
    public boolean hasData(UUID playerId) {
        return playerDataMap.containsKey(playerId);
    }
    
    /**
     * 删除玩家数据
     * 
     * @param playerId 玩家 UUID
     */
    public void removeData(UUID playerId) {
        playerDataMap.remove(playerId);
        markDirty();
    }
    
    /**
     * 标记数据已修改，需要保存
     */
    public void markModified() {
        markDirty();
    }
    
    // ==================== 静态访问方法 ====================
    
    /**
     * 从世界获取存储实例
     * 
     * @param world 服务器世界
     * @return Factor 数据存储实例
     */
    public static PlayerFactorDataStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            new PersistentState.Type<>(
                PlayerFactorDataStorage::new,
                PlayerFactorDataStorage::new,
                null
            ),
            KEY
        );
    }
    
    /**
     * 便捷方法：获取玩家 Factor 背包
     * 
     * @param world 服务器世界
     * @param playerId 玩家 UUID
     * @return 玩家 Factor 背包
     */
    public static PlayerFactorInventory getPlayerFactorData(ServerWorld world, UUID playerId) {
        return get(world).getPlayerData(playerId);
    }
    
    /**
     * 便捷方法：获取玩家 Factor 背包
     * 
     * @param world 服务器世界
     * @param player 玩家
     * @return 玩家 Factor 背包
     */
    public static PlayerFactorInventory getPlayerFactorData(ServerWorld world, ServerPlayerEntity player) {
        return get(world).getPlayerData(player);
    }
}