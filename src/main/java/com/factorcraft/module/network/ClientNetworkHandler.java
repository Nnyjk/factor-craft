package com.factorcraft.module.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

public class ClientNetworkHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(FactorSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // 客户端 Factor 状态更新（待实现）
            });
        });
        
        ClientPlayNetworking.registerGlobalReceiver(TraitSyncPayload.ID, (payload, context) -> {
            int slot = payload.slot();
            var traits = payload.traits();
            
            context.client().execute(() -> {
                TraitDisplayCache.update(slot, traits);
            });
        });
        
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
    }
}