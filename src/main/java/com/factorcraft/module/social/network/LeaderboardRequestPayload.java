package com.factorcraft.module.social.network;

import com.factorcraft.module.social.leaderboard.LeaderboardType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * 排行榜数据请求包 (C2S)
 */
public record LeaderboardRequestPayload(LeaderboardType type) implements CustomPayload {
    
    public static final CustomPayload.Id<LeaderboardRequestPayload> ID = 
        new CustomPayload.Id<>(Identifier.of("factorcraft", "leaderboard_request"));
    
    public static final PacketCodec<PacketByteBuf, LeaderboardRequestPayload> CODEC = 
        PacketCodec.of(LeaderboardRequestPayload::write, LeaderboardRequestPayload::read);
    
    private void write(PacketByteBuf buf) {
        buf.writeInt(type.ordinal());
    }
    
    private static LeaderboardRequestPayload read(PacketByteBuf buf) {
        int typeOrdinal = buf.readInt();
        LeaderboardType type = LeaderboardType.values()[typeOrdinal];
        return new LeaderboardRequestPayload(type);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
