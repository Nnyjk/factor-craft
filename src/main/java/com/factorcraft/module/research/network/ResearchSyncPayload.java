package com.factorcraft.module.research.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.*;

/**
 * 研究进度同步网络包
 */
public record ResearchSyncPayload(
    UUID playerId,
    List<String> completedResearch,
    List<String> inProgressKeys,
    List<Long> inProgressValues,
    long totalResearchTime
) implements CustomPayload {
    
    public static final Id<ResearchSyncPayload> PACKET_ID = 
        new Id<>(Identifier.of("factorcraft", "research_sync"));
    
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
    
    public static final PacketCodec<PacketByteBuf, ResearchSyncPayload> CODEC = PacketCodec.tuple(
        UUID_CODEC,
        ResearchSyncPayload::playerId,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING),
        ResearchSyncPayload::completedResearch,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING),
        ResearchSyncPayload::inProgressKeys,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.LONG),
        ResearchSyncPayload::inProgressValues,
        PacketCodecs.LONG,
        ResearchSyncPayload::totalResearchTime,
        ResearchSyncPayload::new
    );
    
    /**
     * 从 Set 和 Map 创建 payload
     */
    public static ResearchSyncPayload of(UUID playerId, Set<String> completed, Map<String, Long> inProgress, long totalTime) {
        return new ResearchSyncPayload(playerId, new ArrayList<>(completed), 
            new ArrayList<>(inProgress.keySet()), new ArrayList<>(inProgress.values()), totalTime);
    }
    
    /**
     * 转换为 Set
     */
    public Set<String> getCompletedResearch() {
        return new HashSet<>(completedResearch);
    }
    
    /**
     * 转换为 Map
     */
    public Map<String, Long> getInProgressResearch() {
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < inProgressKeys.size(); i++) {
            map.put(inProgressKeys.get(i), inProgressValues.get(i));
        }
        return map;
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
