package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * 网络包注册 - Fabric 1.21.4
 * 
 * 必须在发送/接收 payload 之前注册类型
 */
public class NetworkPackets {
    
    /**
     * 注册所有网络包类型
     * 在服务器初始化时调用
     */
    public static void register() {
        // 注册服务器 -> 客户端的 payload 类型
        PayloadTypeRegistry.playS2C().register(
            FactorSyncPayload.ID,
            FactorSyncPayload.CODEC
        );
        
        PayloadTypeRegistry.playS2C().register(
            TraitSyncPayload.ID,
            TraitSyncPayload.CODEC
        );
        
        PayloadTypeRegistry.playS2C().register(
            QuestRewardPayload.ID,
            QuestRewardPayload.CODEC
        );
        
        PayloadTypeRegistry.playS2C().register(
            QuestSyncPayload.ID,
            QuestSyncPayload.CODEC
        );
        
        // 如果有客户端 -> 服务器的 payload，使用 playC2S()
        // PayloadTypeRegistry.playC2S().register(...);
        
        System.out.println("[NetworkPackets] 已注册 4 个网络包类型");
    }
}