package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * 网络包注册 - Fabric 1.21.4
 * 
 * 必须在发送/接收 payload 之前注册类型
 * 
 * 已注册的 Payload 类型：
 * - FactorSyncPayload: 区块 Factor 浓度同步
 * - TraitSyncPayload: 物品 Trait 同步
 * - QuestRewardPayload: 任务奖励同步
 * - QuestSyncPayload: 任务进度同步
 * - PlayerFactorSyncPayload: 玩家 Factor 状态同步 (新增)
 * - DimensionActivitySyncPayload: 维度活性同步 (新增)
 * - MachineStateSyncPayload: 机器状态同步 (新增)
 * - AchievementSyncPayload: 成就解锁同步 (新增)
 */
public class NetworkPackets {
    
    /**
     * 注册所有网络包类型
     * 在服务器初始化时调用
     */
    public static void register() {
        // ==================== 核心 Factor 同步 ====================
        PayloadTypeRegistry.playS2C().register(
            FactorSyncPayload.ID,
            FactorSyncPayload.CODEC
        );
        
        PayloadTypeRegistry.playS2C().register(
            PlayerFactorSyncPayload.ID,
            PlayerFactorSyncPayload.CODEC
        );
        
        PayloadTypeRegistry.playS2C().register(
            DimensionActivitySyncPayload.ID,
            DimensionActivitySyncPayload.CODEC
        );
        
        // ==================== 物品系统同步 ====================
        PayloadTypeRegistry.playS2C().register(
            TraitSyncPayload.ID,
            TraitSyncPayload.CODEC
        );
        
        // ==================== 任务系统同步 ====================
        PayloadTypeRegistry.playS2C().register(
            QuestRewardPayload.ID,
            QuestRewardPayload.CODEC
        );
        
        PayloadTypeRegistry.playS2C().register(
            QuestSyncPayload.ID,
            QuestSyncPayload.CODEC
        );
        
        // ==================== 机器系统同步 ====================
        PayloadTypeRegistry.playS2C().register(
            MachineStateSyncPayload.ID,
            MachineStateSyncPayload.CODEC
        );
        
        // ==================== 成就系统同步 ====================
        PayloadTypeRegistry.playS2C().register(
            AchievementSyncPayload.ID,
            AchievementSyncPayload.CODEC
        );
        
        // ==================== 客户端请求 (C2S) ====================
        PayloadTypeRegistry.playC2S().register(
            MachineOperationPayload.ID,
            MachineOperationPayload.CODEC
        );
        
        System.out.println("[NetworkPackets] 已注册 9 个网络包类型 (8 S2C + 1 C2S)");
    }
}