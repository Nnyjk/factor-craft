package com.factorcraft.network.payload;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * 配置同步网络包
 * 
 * 用于服务端向客户端同步配置数据
 */
public record ConfigSyncPayload(String configName, String configData) implements CustomPayload {
    
    public static final Id<ConfigSyncPayload> ID = new Id<>(Identifier.of("factorcraft", "config_sync"));
    
    /**
     * PacketCodec for ConfigSyncPayload
     */
    public static final PacketCodec<PacketByteBuf, ConfigSyncPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.STRING,
        ConfigSyncPayload::configName,
        PacketCodecs.STRING,
        ConfigSyncPayload::configData,
        ConfigSyncPayload::new
    );
    
    /**
     * 从 JsonObject 创建 ConfigSyncPayload
     */
    public static ConfigSyncPayload fromJson(String configName, JsonObject configData) {
        return new ConfigSyncPayload(configName, configData.toString());
    }
    
    /**
     * 解析为 JsonObject
     */
    public JsonObject toJson() {
        return com.google.gson.JsonParser.parseString(configData).getAsJsonObject();
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
