package com.factorcraft.module.social.leaderboard;

import com.factorcraft.FactorCraftMod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 排行榜管理器 - 单例模式
 */
public class LeaderboardManager {
    private static LeaderboardManager instance;
    
    // 各类型排行榜数据（LeaderboardType -> List<LeaderboardEntry>）
    private final Map<LeaderboardType, List<LeaderboardEntry>> leaderboards = new ConcurrentHashMap<>();
    
    // 玩家分数缓存（PlayerUUID -> Map<LeaderboardType, Long>）
    private final Map<UUID, Map<LeaderboardType, Long>> playerScores = new ConcurrentHashMap<>();
    
    // 最后更新时间
    private long lastUpdateTime = 0;
    
    // 更新间隔（tick）
    private static final int UPDATE_INTERVAL = 12000; // 10 分钟
    
    private LeaderboardManager() {
        // 初始化所有排行榜类型
        for (LeaderboardType type : LeaderboardType.values()) {
            leaderboards.put(type, new ArrayList<>());
        }
    }
    
    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }
    
    /**
     * 更新玩家分数
     */
    public void updatePlayerScore(UUID playerId, String playerName, LeaderboardType type, long score) {
        playerScores.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardType.class))
            .put(type, score);
        
        rebuildLeaderboard(type);
    }
    
    /**
     * 增加玩家分数
     */
    public void addPlayerScore(UUID playerId, String playerName, LeaderboardType type, long delta) {
        Map<LeaderboardType, Long> scores = playerScores.computeIfAbsent(playerId, k -> new EnumMap<>(LeaderboardType.class));
        long currentScore = scores.getOrDefault(type, 0L);
        scores.put(type, currentScore + delta);
        
        rebuildLeaderboard(type);
    }
    
    /**
     * 重建排行榜
     */
    private void rebuildLeaderboard(LeaderboardType type) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        
        for (Map.Entry<UUID, Map<LeaderboardType, Long>> playerEntry : playerScores.entrySet()) {
            UUID playerId = playerEntry.getKey();
            Map<LeaderboardType, Long> scores = playerEntry.getValue();
            long score = scores.getOrDefault(type, 0L);
            if (score > 0) {
                // TODO: 获取玩家名称（需要服务器引用）
                entries.add(new LeaderboardEntry(playerId, "Unknown", score));
            }
        }
        
        // 排序
        entries.sort(LeaderboardEntry::compareTo);
        
        // 设置排名
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
        
        leaderboards.put(type, entries);
    }
    
    /**
     * 获取排行榜前 N 名
     */
    public List<LeaderboardEntry> getTopN(LeaderboardType type, int n) {
        List<LeaderboardEntry> leaderboard = leaderboards.get(type);
        if (leaderboard == null) {
            return Collections.emptyList();
        }
        
        return leaderboard.stream()
            .limit(n)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取完整排行榜
     */
    public List<LeaderboardEntry> getLeaderboard(LeaderboardType type) {
        List<LeaderboardEntry> leaderboard = leaderboards.get(type);
        return leaderboard != null ? new ArrayList<>(leaderboard) : Collections.emptyList();
    }
    
    /**
     * 获取玩家排名
     */
    public int getPlayerRank(UUID playerId, LeaderboardType type) {
        List<LeaderboardEntry> leaderboard = leaderboards.get(type);
        if (leaderboard == null) {
            return -1;
        }
        
        for (LeaderboardEntry entry : leaderboard) {
            if (entry.getPlayerId().equals(playerId)) {
                return entry.getRank();
            }
        }
        
        return -1;
    }
    
    /**
     * 获取玩家分数
     */
    public long getPlayerScore(UUID playerId, LeaderboardType type) {
        Map<LeaderboardType, Long> scores = playerScores.get(playerId);
        if (scores == null) {
            return 0L;
        }
        
        return scores.getOrDefault(type, 0L);
    }
    
    /**
     * 获取所有排行榜类型
     */
    public Set<LeaderboardType> getAvailableTypes() {
        return leaderboards.keySet();
    }
    
    /**
     * 检查是否需要更新
     */
    public boolean needsUpdate(long currentTime) {
        return (currentTime - lastUpdateTime) > UPDATE_INTERVAL;
    }
    
    /**
     * 更新最后更新时间
     */
    public void markUpdated() {
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * 清理排行榜（定期重置）
     */
    public void resetLeaderboard(LeaderboardType type) {
        leaderboards.put(type, new ArrayList<>());
        FactorCraftMod.LOGGER.info("[FactorCraft:Leaderboard] 排行榜已重置：{}", type.getDisplayName());
    }
}
