package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 任务奖励通知 - 服务器 -> 客户端
 */
public record QuestRewardPayload(String rewardType, String description) implements CustomPayload {
    public static final CustomPayload.Id<QuestRewardPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "quest_reward"));
    
    public static final PacketCodec<RegistryByteBuf, QuestRewardPayload> CODEC = 
        PacketCodec.of(QuestRewardPayload::write, QuestRewardPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeString(rewardType);
        buf.writeString(description);
    }
    
    private static QuestRewardPayload read(RegistryByteBuf buf) {
        String rewardType = buf.readString();
        String description = buf.readString();
        return new QuestRewardPayload(rewardType, description);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 发送奖励通知到客户端
     */
    public static void sendToPlayer(ServerPlayerEntity player, String rewardType, String description) {
        if (ServerPlayNetworking.canSend(player, ID)) {
            ServerPlayNetworking.send(player, new QuestRewardPayload(rewardType, description));
        }
    }
}
