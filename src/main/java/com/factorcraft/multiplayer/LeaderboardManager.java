package com.factorcraft.multiplayer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 排行榜系统
 * 追踪玩家在 Factor Craft 中的表现
 */
public class LeaderboardManager {
    private static final Map<String, Leaderboard> LEADERBOARDS = new ConcurrentHashMap<>();
    
    // 排行榜类型
    public static final String EXTRACTIONS = "extractions";
    public static final String RESONANCES = "resonances";
    public static final String FACTOR_POINTS = "factor_points";
    public static final String QUESTS_COMPLETED = "quests_completed";
    public static final String HIGH_ENERGY_CHUNKS = "high_energy_chunks";
    
    static {
        LEADERBOARDS.put(EXTRACTIONS, new Leaderboard("提取次数", 100));
        LEADERBOARDS.put(RESONANCES, new Leaderboard("共振次数", 100));
        LEADERBOARDS.put(FACTOR_POINTS, new Leaderboard("Factor 点数", 100));
        LEADERBOARDS.put(QUESTS_COMPLETED, new Leaderboard("完成任务", 100));
        LEADERBOARDS.put(HIGH_ENERGY_CHUNKS, new Leaderboard("发现高能区块", 100));
    }
    
    /**
     * 更新玩家分数
     */
    public static void updateScore(UUID playerId, String playerName, String leaderboardType, long score) {
        Leaderboard board = LEADERBOARDS.get(leaderboardType);
        if (board != null) {
            board.updateScore(playerId, playerName, score);
        }
    }
    
    /**
     * 增加玩家分数
     */
    public static void addScore(UUID playerId, String playerName, String leaderboardType, long amount) {
        Leaderboard board = LEADERBOARDS.get(leaderboardType);
        if (board != null) {
            board.addScore(playerId, playerName, amount);
        }
    }
    
    /**
     * 获取玩家排名
     */
    public static int getPlayerRank(UUID playerId, String leaderboardType) {
        Leaderboard board = LEADERBOARDS.get(leaderboardType);
        return board != null ? board.getPlayerRank(playerId) : -1;
    }
    
    /**
     * 获取玩家分数
     */
    public static long getPlayerScore(UUID playerId, String leaderboardType) {
        Leaderboard board = LEADERBOARDS.get(leaderboardType);
        return board != null ? board.getPlayerScore(playerId) : 0;
    }
    
    /**
     * 获取排行榜前 N 名
     */
    public static List<LeaderboardEntry> getTopPlayers(String leaderboardType, int count) {
        Leaderboard board = LEADERBOARDS.get(leaderboardType);
        return board != null ? board.getTopPlayers(count) : Collections.emptyList();
    }
    
    /**
     * 显示排行榜给玩家
     */
    public static void displayLeaderboard(ServerPlayerEntity player, String leaderboardType) {
        Leaderboard board = LEADERBOARDS.get(leaderboardType);
        if (board == null) {
            player.sendMessage(Text.literal("§c排行榜不存在"), false);
            return;
        }
        
        player.sendMessage(Text.literal("§6=== " + board.getDisplayName() + " 排行榜 ==="), false);
        player.sendMessage(Text.literal(""), false);
        
        List<LeaderboardEntry> topPlayers = board.getTopPlayers(10);
        for (int i = 0; i < topPlayers.size(); i++) {
            LeaderboardEntry entry = topPlayers.get(i);
            String medal = switch (i) {
                case 0 -> "§6🥇 ";
                case 1 -> "§7🥈 ";
                case 2 -> "§8🥉 ";
                default -> "§f" + (i + 1) + ". ";
            };
            player.sendMessage(Text.literal(medal + "§f" + entry.playerName() + " §7- §e" + entry.score()), false);
        }
        
        // 显示玩家排名
        int rank = board.getPlayerRank(player.getUuid());
        long score = board.getPlayerScore(player.getUuid());
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("§7你的排名: §f#" + (rank + 1) + " §7(" + score + " 分)"), false);
    }
    
    /**
     * 显示所有排行榜概览
     */
    public static void displayOverview(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("§6=== Factor Craft 排行榜 ==="), false);
        player.sendMessage(Text.literal(""), false);
        
        for (Map.Entry<String, Leaderboard> entry : LEADERBOARDS.entrySet()) {
            Leaderboard board = entry.getValue();
            int rank = board.getPlayerRank(player.getUuid());
            long score = board.getPlayerScore(player.getUuid());
            
            player.sendMessage(Text.literal("§e" + board.getDisplayName() + ": §f#" + (rank + 1) + " §7(" + score + ")"), false);
        }
    }
}

/**
 * 排行榜
 */
class Leaderboard {
    private final String displayName;
    private final int maxSize;
    private final Map<UUID, LeaderboardEntry> entries = new ConcurrentHashMap<>();
    private final List<LeaderboardEntry> sortedEntries = new ArrayList<>();
    
    public Leaderboard(String displayName, int maxSize) {
        this.displayName = displayName;
        this.maxSize = maxSize;
    }
    
    public synchronized void updateScore(UUID playerId, String playerName, long score) {
        LeaderboardEntry entry = new LeaderboardEntry(playerId, playerName, score);
        entries.put(playerId, entry);
        resort();
    }
    
    public synchronized void addScore(UUID playerId, String playerName, long amount) {
        LeaderboardEntry existing = entries.get(playerId);
        if (existing != null) {
            updateScore(playerId, existing.playerName(), existing.score() + amount);
        } else {
            updateScore(playerId, playerName, amount);
        }
    }
    
    public int getPlayerRank(UUID playerId) {
        LeaderboardEntry entry = entries.get(playerId);
        if (entry == null) return sortedEntries.size();
        
        int rank = 0;
        for (LeaderboardEntry e : sortedEntries) {
            if (e.playerId().equals(playerId)) {
                return rank;
            }
            rank++;
        }
        return sortedEntries.size();
    }
    
    public long getPlayerScore(UUID playerId) {
        LeaderboardEntry entry = entries.get(playerId);
        return entry != null ? entry.score() : 0;
    }
    
    public List<LeaderboardEntry> getTopPlayers(int count) {
        return sortedEntries.stream().limit(count).toList();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    private synchronized void resort() {
        sortedEntries.clear();
        sortedEntries.addAll(entries.values());
        sortedEntries.sort((a, b) -> Long.compare(b.score(), a.score()));
        
        // 限制大小
        if (sortedEntries.size() > maxSize) {
            for (int i = maxSize; i < sortedEntries.size(); i++) {
                entries.remove(sortedEntries.get(i).playerId());
            }
            sortedEntries.subList(maxSize, sortedEntries.size()).clear();
        }
    }
}

/**
 * 排行榜条目
 */
record LeaderboardEntry(
    UUID playerId,
    String playerName,
    long score
) implements Comparable<LeaderboardEntry> {
    @Override
    public int compareTo(LeaderboardEntry other) {
        return Long.compare(other.score(), this.score());
    }
}