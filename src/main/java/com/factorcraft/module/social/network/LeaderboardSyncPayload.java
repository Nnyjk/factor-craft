package com.factorcraft.module.social.network;

import com.factorcraft.module.social.leaderboard.LeaderboardEntry;
import com.factorcraft.module.social.leaderboard.LeaderboardType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 排行榜数据同步包
 */
public record LeaderboardSyncPayload(LeaderboardType type, List<EntryData> entries) implements CustomPayload {
    
    public static final Id<LeaderboardSyncPayload> ID = new Id<>(Identifier.of("factorcraft", "leaderboard_sync"));
    
    public static final PacketCodec<PacketByteBuf, LeaderboardSyncPayload> CODEC = PacketCodec.of(
        (LeaderboardSyncPayload payload, PacketByteBuf buf) -> {
            buf.writeInt(payload.type().ordinal());
            buf.writeInt(payload.entries().size());
            for (EntryData data : payload.entries()) {
                data.write(buf);
            }
        },
        (PacketByteBuf buf) -> {
            LeaderboardType type = LeaderboardType.values()[buf.readInt()];
            int count = buf.readInt();
            List<EntryData> entries = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                entries.add(EntryData.read(buf));
            }
            return new LeaderboardSyncPayload(type, entries);
        }
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 排行榜条目数据（简化版，用于网络传输）
     */
    public static class EntryData {
        public final UUID playerId;
        public final String playerName;
        public final long value;
        public final int rank;
        
        public EntryData(UUID playerId, String playerName, long value, int rank) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.value = value;
            this.rank = rank;
        }
        
        public static EntryData fromEntry(LeaderboardEntry entry) {
            return new EntryData(
                entry.getPlayerId(),
                entry.getPlayerName(),
                entry.getScore(),
                entry.getRank()
            );
        }
        
        public void write(PacketByteBuf buf) {
            buf.writeLong(playerId.getMostSignificantBits());
            buf.writeLong(playerId.getLeastSignificantBits());
            buf.writeString(playerName);
            buf.writeLong(value);
            buf.writeInt(rank);
        }
        
        public static EntryData read(PacketByteBuf buf) {
            UUID playerId = new UUID(buf.readLong(), buf.readLong());
            String playerName = buf.readString();
            long value = buf.readLong();
            int rank = buf.readInt();
            return new EntryData(playerId, playerName, value, rank);
        }
    }
}
