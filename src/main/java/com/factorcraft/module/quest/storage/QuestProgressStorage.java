package com.factorcraft.module.quest.storage;

import com.factorcraft.module.quest.model.QuestData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 任务进度持久化存储
 * 
 * 使用 PersistentState 机制将玩家任务数据存储到世界数据中
 */
public class QuestProgressStorage extends PersistentState {
    
    private static final String KEY = "factorcraft_quest_data";
    
    private final Map<UUID, Map<Identifier, QuestData>> playerQuestData = new HashMap<>();
    
    public QuestProgressStorage() {
    }
    
    // ==================== PersistentState 实现 ====================
    
    public QuestProgressStorage(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList playersList = nbt.getList("players", NbtList.COMPOUND_TYPE);
        for (int i = 0; i < playersList.size(); i++) {
            NbtCompound playerNbt = playersList.getCompound(i);
            UUID playerId = UUID.fromString(playerNbt.getString("uuid"));
            
            Map<Identifier, QuestData> quests = new HashMap<>();
            NbtList questsList = playerNbt.getList("quests", NbtList.COMPOUND_TYPE);
            for (int j = 0; j < questsList.size(); j++) {
                NbtCompound questNbt = questsList.getCompound(j);
                QuestData questData = QuestData.fromNbt(questNbt);
                quests.put(questData.getQuestId(), questData);
            }
            
            playerQuestData.put(playerId, quests);
        }
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList playersList = new NbtList();
        for (Map.Entry<UUID, Map<Identifier, QuestData>> entry : playerQuestData.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putString("uuid", entry.getKey().toString());
            
            NbtList questsList = new NbtList();
            for (QuestData questData : entry.getValue().values()) {
                questsList.add(questData.toNbt());
            }
            playerNbt.put("quests", questsList);
            
            playersList.add(playerNbt);
        }
        nbt.put("players", playersList);
        
        return nbt;
    }
    
    // ==================== 数据访问方法 ====================
    
    /**
     * 获取玩家的任务数据
     */
    public Map<Identifier, QuestData> getPlayerQuests(UUID playerId) {
        return playerQuestData.computeIfAbsent(playerId, k -> new HashMap<>());
    }
    
    /**
     * 获取玩家特定任务数据
     */
    public Optional<QuestData> getQuestData(UUID playerId, Identifier questId) {
        Map<Identifier, QuestData> quests = playerQuestData.get(playerId);
        if (quests != null) {
            return Optional.ofNullable(quests.get(questId));
        }
        return Optional.empty();
    }
    
    /**
     * 设置玩家任务数据
     */
    public void setQuestData(UUID playerId, QuestData questData) {
        Map<Identifier, QuestData> quests = getPlayerQuests(playerId);
        quests.put(questData.getQuestId(), questData);
        markDirty();
    }
    
    /**
     * 移除玩家任务数据
     */
    public void removeQuestData(UUID playerId, Identifier questId) {
        Map<Identifier, QuestData> quests = playerQuestData.get(playerId);
        if (quests != null) {
            quests.remove(questId);
            markDirty();
        }
    }
    
    /**
     * 清空玩家所有任务数据
     */
    public void clearPlayerQuests(UUID playerId) {
        playerQuestData.remove(playerId);
        markDirty();
    }
    
    /**
     * 标记数据已修改
     */
    public void markModified() {
        markDirty();
    }
    
    // ==================== 静态访问方法 ====================
    
    /**
     * 从世界获取存储实例
     * 
     * @param world 服务器世界
     * @return 任务数据存储实例
     */
    public static QuestProgressStorage get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            new PersistentState.Type<>(
                QuestProgressStorage::new,
                QuestProgressStorage::new,
                null
            ),
            KEY
        );
    }
    
    /**
     * 便捷方法：获取玩家任务数据
     * 
     * @param world 服务器世界
     * @param playerId 玩家 UUID
     * @return 玩家任务数据 Map
     */
    public static Map<Identifier, QuestData> getPlayerData(ServerWorld world, UUID playerId) {
        return get(world).getPlayerQuests(playerId);
    }
}