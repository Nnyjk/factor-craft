package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家 Factor 状态同步 Payload
 * 
 * 同步玩家自身的 Factor 数据到客户端，用于 UI 显示和游戏逻辑
 * 
 * 包含数据：
 * - 各类型 Factor 累积值 (fire, water, earth, air, life, death)
 * - Factor 容器容量
 * - 维度活性修正
 */
public record PlayerFactorSyncPayload(
    Map<String, Double> factorValues,
    double totalCapacity,
    Map<String, Double> dimensionBonuses
) implements CustomPayload {
    
    public static final CustomPayload.Id<PlayerFactorSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "player_factor_sync"));
    
    public static final PacketCodec<RegistryByteBuf, PlayerFactorSyncPayload> CODEC = 
        PacketCodec.of(PlayerFactorSyncPayload::write, PlayerFactorSyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        // 写入 Factor 值
        buf.writeInt(factorValues.size());
        for (Map.Entry<String, Double> entry : factorValues.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeDouble(entry.getValue());
        }
        
        // 写入总容量
        buf.writeDouble(totalCapacity);
        
        // 写入维度加成
        buf.writeInt(dimensionBonuses.size());
        for (Map.Entry<String, Double> entry : dimensionBonuses.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeDouble(entry.getValue());
        }
    }
    
    private static PlayerFactorSyncPayload read(RegistryByteBuf buf) {
        // 读取 Factor 值
        int factorCount = buf.readInt();
        Map<String, Double> factorValues = new HashMap<>();
        for (int i = 0; i < factorCount; i++) {
            String key = buf.readString();
            double value = buf.readDouble();
            factorValues.put(key, value);
        }
        
        // 读取总容量
        double totalCapacity = buf.readDouble();
        
        // 读取维度加成
        int bonusCount = buf.readInt();
        Map<String, Double> dimensionBonuses = new HashMap<>();
        for (int i = 0; i < bonusCount; i++) {
            String key = buf.readString();
            double value = buf.readDouble();
            dimensionBonuses.put(key, value);
        }
        
        return new PlayerFactorSyncPayload(factorValues, totalCapacity, dimensionBonuses);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 发送玩家 Factor 状态同步
     * 
     * @param player 目标玩家
     * @param factorValues Factor 累积值
     * @param totalCapacity 总容量
     * @param dimensionBonuses 维度加成
     */
    public static void sendToPlayer(ServerPlayerEntity player, 
                                    Map<String, Double> factorValues,
                                    double totalCapacity,
                                    Map<String, Double> dimensionBonuses) {
        PlayerFactorSyncPayload payload = new PlayerFactorSyncPayload(
            factorValues, totalCapacity, dimensionBonuses
        );
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 创建空的默认 Payload
     */
    public static PlayerFactorSyncPayload empty() {
        return new PlayerFactorSyncPayload(
            new HashMap<>(),
            0.0,
            new HashMap<>()
        );
    }
}