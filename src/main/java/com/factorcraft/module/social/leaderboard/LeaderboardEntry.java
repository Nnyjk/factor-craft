package com.factorcraft.module.social.leaderboard;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

/**
 * 排行榜条目
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private final UUID playerId;
    private final String playerName;
    private final long score;
    private int rank;
    
    public LeaderboardEntry(UUID playerId, String playerName, long score) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.score = score;
        this.rank = 0;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public long getScore() {
        return score;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public int compareTo(LeaderboardEntry other) {
        // 按分数降序排列
        return Long.compare(other.score, this.score);
    }
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("player_id", playerId);
        nbt.putString("player_name", playerName);
        nbt.putLong("score", score);
        nbt.putInt("rank", rank);
        return nbt;
    }
    
    /**
     * 从 NBT 读取
     */
    public static LeaderboardEntry fromNbt(NbtCompound nbt) {
        UUID playerId = nbt.getUuid("player_id");
        String playerName = nbt.getString("player_name");
        long score = nbt.getLong("score");
        
        LeaderboardEntry entry = new LeaderboardEntry(playerId, playerName, score);
        entry.setRank(nbt.getInt("rank"));
        return entry;
    }
    
    /**
     * 写入网络包
     */
    public void write(PacketByteBuf buf) {
        buf.writeUuid(playerId);
        buf.writeString(playerName);
        buf.writeLong(score);
        buf.writeInt(rank);
    }
    
    /**
     * 从网络包读取
     */
    public static LeaderboardEntry read(PacketByteBuf buf) {
        UUID playerId = buf.readUuid();
        String playerName = buf.readString();
        long score = buf.readLong();
        int rank = buf.readInt();
        
        LeaderboardEntry entry = new LeaderboardEntry(playerId, playerName, score);
        entry.setRank(rank);
        return entry;
    }
}
