package com.factorcraft.network;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.config.ConfigManager;
import com.factorcraft.network.payload.ConfigSyncPayload;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;

/**
 * 配置同步处理器
 * 
 * 处理服务端到客户端的配置同步
 */
public class ConfigSyncHandler {
    
    /**
     * 注册网络包
     */
    public static void register() {
        // 服务端和客户端都需要注册
        PayloadTypeRegistry.playC2S().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.ID, ConfigSyncPayload.CODEC);
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Network] 配置同步网络包已注册");
    }
    
    /**
     * 客户端接收器注册
     */
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                JsonObject config = payload.toJson();
                String configName = payload.configName();
                
                FactorCraftMod.LOGGER.debug("[FactorCraft:Config] 收到服务端配置同步：{}", configName);
                
                // 缓存到客户端配置管理器
                ClientConfigCache.cacheConfig(configName, config);
            });
        });
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Network] 客户端配置同步接收器已注册");
    }
    
    /**
     * 服务端发送配置到客户端
     * 
     * @param player 目标玩家
     * @param configName 配置名称
     * @param config 配置数据
     */
    public static void sendConfigToClient(ServerPlayerEntity player, String configName, JsonObject config) {
        ConfigSyncPayload payload = ConfigSyncPayload.fromJson(configName, config);
        ServerPlayNetworking.send(player, payload);
        
        FactorCraftMod.LOGGER.debug("[FactorCraft:Config] 发送配置到客户端 {}：{}", player.getName().getString(), configName);
    }
    
    /**
     * 发送所有配置到客户端
     * 
     * @param player 目标玩家
     */
    public static void sendAllConfigsToClient(ServerPlayerEntity player) {
        Set<String> configNames = ConfigManager.getConfigNames();
        
        for (String configName : configNames) {
            JsonObject config = ConfigManager.getConfig(configName);
            if (config != null) {
                sendConfigToClient(player, configName, config);
            }
        }
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Config] 已发送 {} 个配置到客户端 {}", configNames.size(), player.getName().getString());
    }
    
    /**
     * 玩家加入时同步配置
     * 
     * 在玩家加入事件处理器中调用此方法
     */
    public static void onPlayerJoin(ServerPlayerEntity player) {
        // 延迟一 tick 发送，确保玩家完全加入
        player.getServer().execute(() -> sendAllConfigsToClient(player));
    }
}
