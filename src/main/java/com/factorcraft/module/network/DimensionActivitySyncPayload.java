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
 * 维度活性同步 Payload
 * 
 * 同步当前维度的活性系数到客户端，用于 Factor 计算和 UI 显示
 * 
 * 活性系数影响：
 * - Factor 生成效率
 * - Factor 转化效率
 * - 特定 Factor 类型的加成
 */
public record DimensionActivitySyncPayload(
    String dimensionKey,
    double activityCoefficient,
    Map<String, Double> factorTypeBonuses,
    long worldTick
) implements CustomPayload {
    
    public static final CustomPayload.Id<DimensionActivitySyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "dimension_activity_sync"));
    
    public static final PacketCodec<RegistryByteBuf, DimensionActivitySyncPayload> CODEC = 
        PacketCodec.of(DimensionActivitySyncPayload::write, DimensionActivitySyncPayload::read);
    
    private void write(RegistryByteBuf buf) {
        buf.writeString(dimensionKey);
        buf.writeDouble(activityCoefficient);
        buf.writeLong(worldTick);
        
        // 写入 Factor 类型加成
        buf.writeInt(factorTypeBonuses.size());
        for (Map.Entry<String, Double> entry : factorTypeBonuses.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeDouble(entry.getValue());
        }
    }
    
    private static DimensionActivitySyncPayload read(RegistryByteBuf buf) {
        String dimensionKey = buf.readString();
        double activityCoefficient = buf.readDouble();
        long worldTick = buf.readLong();
        
        int bonusCount = buf.readInt();
        Map<String, Double> factorTypeBonuses = new HashMap<>();
        for (int i = 0; i < bonusCount; i++) {
            String key = buf.readString();
            double value = buf.readDouble();
            factorTypeBonuses.put(key, value);
        }
        
        return new DimensionActivitySyncPayload(dimensionKey, activityCoefficient, factorTypeBonuses, worldTick);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 发送维度活性同步
     */
    public static void sendToPlayer(ServerPlayerEntity player, 
                                    String dimensionKey,
                                    double activityCoefficient,
                                    Map<String, Double> factorTypeBonuses,
                                    long worldTick) {
        DimensionActivitySyncPayload payload = new DimensionActivitySyncPayload(
            dimensionKey, activityCoefficient, factorTypeBonuses, worldTick
        );
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 获取当前维度的活性系数（基于世界时间）
     */
    public static double calculateActivityCoefficient(long worldTick, String dimensionKey) {
        // 基础活性：基于日夜循环
        double dayCycle = Math.sin(worldTick * 2 * Math.PI / 24000);
        double baseActivity = 0.5 + 0.5 * dayCycle; // 0.0 - 1.0
        
        // 维度修正
        double dimensionModifier = switch (dimensionKey) {
            case "minecraft:the_nether" -> 1.2;  // 下界 Factor 活跃度更高
            case "minecraft:the_end" -> 0.8;     // 末地 Factor 活跃度较低
            default -> 1.0;                       // 主世界标准
        };
        
        return baseActivity * dimensionModifier;
    }
}