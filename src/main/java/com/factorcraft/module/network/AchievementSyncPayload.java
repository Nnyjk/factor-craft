package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 成就解锁同步 Payload
 * 
 * 同步玩家成就解锁状态到客户端
 * 
 * 支持操作：
 * - 解锁单个成就
 * - 批量同步已解锁成就
 * - 成就进度更新
 */
public record AchievementSyncPayload(
    Operation operation,
    Identifier achievementId,
    List<Identifier> unlockedAchievements,
    String unlockMessage
) implements CustomPayload {
    
    public enum Operation {
        UNLOCK,         // 解锁单个成就
        SYNC_ALL,       // 批量同步
        PROGRESS_UPDATE // 进度更新
    }
    
    public static final CustomPayload.Id<AchievementSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "achievement_sync"));
    
    public static final PacketCodec<RegistryByteBuf, AchievementSyncPayload> CODEC = 
        PacketCodec.of(AchievementSyncPayload::write, AchievementSyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeEnumConstant(operation);
        buf.writeIdentifier(achievementId);
        buf.writeString(unlockMessage != null ? unlockMessage : "");
        
        buf.writeInt(unlockedAchievements.size());
        for (Identifier id : unlockedAchievements) {
            buf.writeIdentifier(id);
        }
    }
    
    private static AchievementSyncPayload read(RegistryByteBuf buf) {
        Operation op = buf.readEnumConstant(Operation.class);
        Identifier achievementId = buf.readIdentifier();
        String message = buf.readString();
        if (message.isEmpty()) message = null;
        
        int count = buf.readInt();
        List<Identifier> unlocked = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            unlocked.add(buf.readIdentifier());
        }
        
        return new AchievementSyncPayload(op, achievementId, unlocked, message);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 创建解锁单个成就的 Payload
     */
    public static AchievementSyncPayload unlock(Identifier achievementId, String message) {
        return new AchievementSyncPayload(
            Operation.UNLOCK,
            achievementId,
            List.of(achievementId),
            message
        );
    }
    
    /**
     * 创建批量同步的 Payload
     */
    public static AchievementSyncPayload syncAll(List<Identifier> unlockedAchievements) {
        return new AchievementSyncPayload(
            Operation.SYNC_ALL,
            Identifier.of("factorcraft", "batch"),
            new ArrayList<>(unlockedAchievements),
            null
        );
    }
    
    /**
     * 创建进度更新的 Payload
     */
    public static AchievementSyncPayload progress(Identifier achievementId) {
        return new AchievementSyncPayload(
            Operation.PROGRESS_UPDATE,
            achievementId,
            List.of(),
            null
        );
    }
    
    /**
     * 发送给玩家
     */
    public void sendTo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this);
    }
    
    /**
     * 解锁并发送给玩家
     */
    public static void unlockAndSend(ServerPlayerEntity player, Identifier achievementId, String message) {
        unlock(achievementId, message).sendTo(player);
    }
    
    /**
     * 批量同步给玩家
     */
    public static void syncToPlayer(ServerPlayerEntity player, List<Identifier> unlockedAchievements) {
        syncAll(unlockedAchievements).sendTo(player);
    }
}