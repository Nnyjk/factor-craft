package com.factorcraft.module.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

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
    }
}