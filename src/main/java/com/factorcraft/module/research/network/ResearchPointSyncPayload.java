package com.factorcraft.module.research.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 研究点同步网络包
 */
public record ResearchPointSyncPayload(
    UUID playerId,
    int currentPoints,
    int totalEarned,
    int totalSpent,
    List<String> sourceKeys,
    List<Integer> sourceValues
) implements CustomPayload {
    
    public static final Id<ResearchPointSyncPayload> PACKET_ID = 
        new Id<>(Identifier.of("factorcraft", "research_point_sync"));
    
    /**
     * UUID PacketCodec
     */
    public static final PacketCodec<PacketByteBuf, java.util.UUID> UUID_CODEC = PacketCodec.of(
        (uuid, buf) -> {
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
        },
        buf -> new java.util.UUID(buf.readLong(), buf.readLong())
    );
    
    public static final PacketCodec<PacketByteBuf, ResearchPointSyncPayload> CODEC = PacketCodec.tuple(
        UUID_CODEC,
        ResearchPointSyncPayload::playerId,
        PacketCodecs.INTEGER,
        ResearchPointSyncPayload::currentPoints,
        PacketCodecs.INTEGER,
        ResearchPointSyncPayload::totalEarned,
        PacketCodecs.INTEGER,
        ResearchPointSyncPayload::totalSpent,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING),
        ResearchPointSyncPayload::sourceKeys,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER),
        ResearchPointSyncPayload::sourceValues,
        ResearchPointSyncPayload::new
    );
    
    /**
     * 从 Map 创建 payload
     */
    public static ResearchPointSyncPayload of(UUID playerId, int currentPoints, int totalEarned, int totalSpent, Map<String, Integer> pointsBySource) {
        return new ResearchPointSyncPayload(playerId, currentPoints, totalEarned, totalSpent, 
            new ArrayList<>(pointsBySource.keySet()), 
            new ArrayList<>(pointsBySource.values()));
    }
    
    /**
     * 转换为 Map
     */
    public Map<String, Integer> getPointsBySourceMap() {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < sourceKeys.size(); i++) {
            map.put(sourceKeys.get(i), sourceValues.get(i));
        }
        return map;
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
