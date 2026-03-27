package com.factorcraft.module.profession.talent;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;
import java.util.UUID;

/**
 * 天赋数据提供者
 * 
 * 提供对玩家天赋数据的访问
 */
public class TalentDataProvider {
    
    /**
     * 获取服务端玩家的天赋数据
     * 
     * @param player 服务端玩家
     * @return 天赋数据
     */
    public static PlayerTalentData getPlayerData(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        TalentDataStorage storage = TalentDataStorage.get(world);
        return storage.getPlayerData(player.getUuid());
    }
    
    /**
     * 获取服务端玩家的天赋数据
     * 
     * @param world 服务端世界
     * @param playerId 玩家UUID
     * @return 天赋数据
     */
    public static PlayerTalentData getPlayerData(ServerWorld world, UUID playerId) {
        TalentDataStorage storage = TalentDataStorage.get(world);
        return storage.getPlayerData(playerId);
    }
    
    /**
     * 获取玩家的天赋效果处理器
     * 
     * @param player 服务端玩家
     * @return 效果处理器
     */
    public static TalentEffectHandler getEffectHandler(ServerPlayerEntity player) {
        return getPlayerData(player).getEffectHandler();
    }
    
    /**
     * 标记玩家天赋数据已更改
     * 
     * @param player 服务端玩家
     */
    public static void markDirty(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        TalentDataStorage storage = TalentDataStorage.get(world);
        storage.markPlayerDirty(player.getUuid());
    }
    
    /**
     * 重置玩家的所有天赋
     * 
     * @param player 服务端玩家
     */
    public static void resetPlayerTalents(ServerPlayerEntity player) {
        PlayerTalentData data = getPlayerData(player);
        data.resetAllTalents();
        markDirty(player);
    }
    
    /**
     * 清除玩家的天赋数据
     * 
     * @param player 服务端玩家
     */
    public static void clearPlayerData(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        TalentDataStorage storage = TalentDataStorage.get(world);
        storage.clearPlayerData(player.getUuid());
    }
}