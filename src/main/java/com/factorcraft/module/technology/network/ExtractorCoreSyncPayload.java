package com.factorcraft.module.technology.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * 提取核心数据同步包
 */
public record ExtractorCoreSyncPayload(
    double factorStorage,
    double maxStorage,
    double efficiency,
    double dimensionEfficiency,
    double extractRate,
    double progress,
    int tier,
    boolean structureValid,
    String dimension,
    String recommendedDimension
) implements CustomPayload {
    
    public static final CustomPayload.Id<ExtractorCoreSyncPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "extractor_core_sync"));
    
    public static final PacketCodec<ByteBuf, ExtractorCoreSyncPayload> CODEC = 
        PacketCodec.of(ExtractorCoreSyncPayload::write, ExtractorCoreSyncPayload::read);
    
    public void write(ByteBuf buf) {
        buf.writeDouble(factorStorage);
        buf.writeDouble(maxStorage);
        buf.writeDouble(efficiency);
        buf.writeDouble(dimensionEfficiency);
        buf.writeDouble(extractRate);
        buf.writeDouble(progress);
        buf.writeInt(tier);
        buf.writeBoolean(structureValid);
        writeString(buf, dimension);
        writeString(buf, recommendedDimension);
    }
    
    public static ExtractorCoreSyncPayload read(ByteBuf buf) {
        return new ExtractorCoreSyncPayload(
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readDouble(),
            buf.readInt(),
            buf.readBoolean(),
            readString(buf),
            readString(buf)
        );
    }
    
    private static void writeString(ByteBuf buf, String str) {
        if (str == null) {
            buf.writeInt(-1);
        } else {
            byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }
    
    private static String readString(ByteBuf buf) {
        int len = buf.readInt();
        if (len < 0) return null;
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 发送给指定玩家
     */
    public void sendTo(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this);
    }
}