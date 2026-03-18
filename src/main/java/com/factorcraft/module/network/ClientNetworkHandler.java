package com.factorcraft.module.network;

import com.factorcraft.module.quest.ui.QuestTrackerCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端网络包处理器
 * 
 * 处理所有从服务端接收的网络包
 */
public class ClientNetworkHandler {
    
    // 客户端缓存
    private static final Map<String, Double> clientFactorValues = new HashMap<>();
    private static double clientTotalCapacity = 0.0;
    private static final Map<String, Double> clientDimensionBonuses = new HashMap<>();
    private static double clientActivityCoefficient = 0.0;
    private static long clientWorldTick = 0;
    
    public static void register() {
        // ==================== Factor 同步 ====================
        ClientPlayNetworking.registerGlobalReceiver(FactorSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 区块 Factor 浓度同步 - 可用于 HUD 显示
                int x = payload.chunkPos().x;
                int z = payload.chunkPos().z;
                double concentration = payload.concentration();
                // 可以缓存到客户端区块数据中
            });
        });
        
        // 玩家 Factor 状态同步
        ClientPlayNetworking.registerGlobalReceiver(PlayerFactorSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                clientFactorValues.clear();
                clientFactorValues.putAll(payload.factorValues());
                clientTotalCapacity = payload.totalCapacity();
                clientDimensionBonuses.clear();
                clientDimensionBonuses.putAll(payload.dimensionBonuses());
            });
        });
        
        // 维度活性同步
        ClientPlayNetworking.registerGlobalReceiver(DimensionActivitySyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                clientActivityCoefficient = payload.activityCoefficient();
                clientWorldTick = payload.worldTick();
            });
        });
        
        // ==================== Trait 同步 ====================
        ClientPlayNetworking.registerGlobalReceiver(TraitSyncPayload.ID, (payload, context) -> {
            int slot = payload.slot();
            var traits = payload.traits();
            
            context.client().execute(() -> {
                TraitDisplayCache.update(slot, traits);
            });
        });
        
        // ==================== 任务系统同步 ====================
        // 任务奖励通知
        ClientPlayNetworking.registerGlobalReceiver(QuestRewardPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                String rewardType = payload.rewardType();
                String description = payload.description();
                
                // 显示奖励通知
                var rewardText = Text.literal(description).setStyle(net.minecraft.text.Style.EMPTY.withBold(true).withColor(0x00FF00));
                context.player().sendMessage(
                    Text.literal("🎁 任务奖励：").append(rewardText),
                    false
                );
            });
        });
        
        // 任务数据同步
        ClientPlayNetworking.registerGlobalReceiver(QuestSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 更新客户端任务缓存
                QuestTrackerCache.update(
                    payload.activeQuests(),
                    payload.completedQuests()
                );
            });
        });
        
        // ==================== 机器状态同步 ====================
        ClientPlayNetworking.registerGlobalReceiver(MachineStateSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 机器状态更新 - 可用于 BlockEntityRenderer
                // 数据已通过 payload 传递，可以在渲染时使用
            });
        });
        
        // ==================== 成就系统同步 ====================
        ClientPlayNetworking.registerGlobalReceiver(AchievementSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                switch (payload.operation()) {
                    case UNLOCK -> {
                        // 显示成就解锁通知
                        if (payload.unlockMessage() != null) {
                            context.player().sendMessage(
                                Text.literal("🏆 ").append(Text.literal(payload.unlockMessage())),
                                false
                            );
                        }
                    }
                    case SYNC_ALL -> {
                        // 批量同步已解锁成就
                        // 可用于成就界面初始化
                    }
                    case PROGRESS_UPDATE -> {
                        // 成就进度更新
                        // 可用于 HUD 显示
                    }
                }
            });
        });
    }
    
    // ==================== 客户端数据访问 API ====================
    
    /**
     * 获取客户端缓存的 Factor 值
     */
    public static double getFactorValue(String type) {
        return clientFactorValues.getOrDefault(type, 0.0);
    }
    
    /**
     * 获取所有 Factor 值
     */
    public static Map<String, Double> getAllFactorValues() {
        return new HashMap<>(clientFactorValues);
    }
    
    /**
     * 获取总容量
     */
    public static double getTotalCapacity() {
        return clientTotalCapacity;
    }
    
    /**
     * 获取维度加成
     */
    public static double getDimensionBonus(String dimensionKey) {
        return clientDimensionBonuses.getOrDefault(dimensionKey, 1.0);
    }
    
    /**
     * 获取当前活性系数
     */
    public static double getActivityCoefficient() {
        return clientActivityCoefficient;
    }
    
    /**
     * 获取世界 tick
     */
    public static long getWorldTick() {
        return clientWorldTick;
    }
    
    /**
     * 清除客户端缓存（断开连接时调用）
     */
    public static void clearCache() {
        clientFactorValues.clear();
        clientTotalCapacity = 0.0;
        clientDimensionBonuses.clear();
        clientActivityCoefficient = 0.0;
        clientWorldTick = 0;
    }
}