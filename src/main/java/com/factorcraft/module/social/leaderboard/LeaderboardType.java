package com.factorcraft.module.social.leaderboard;

/**
 * 排行榜类型枚举
 */
public enum LeaderboardType {
    // 产量排行榜
    PRODUCTION("产量榜", "machine_output_total"),
    
    // 效率排行榜
    EFFICIENCY("效率榜", "output_per_hour"),
    
    // 探索排行榜
    EXPLORATION("探索榜", "recipes_discovered"),
    
    // 财富排行榜
    WEALTH("财富榜", "total_assets"),
    
    // Factor 收集榜
    FACTOR_COLLECTOR("Factor 收集榜", "factor_collected"),
    
    // 任务完成榜
    QUEST_COMPLETION("任务完成榜", "quests_completed"),
    
    // 市场交易榜
    MARKET_TRADER("市场交易榜", "market_trades");
    
    private final String displayName;
    private final String internalName;
    
    LeaderboardType(String displayName, String internalName) {
        this.displayName = displayName;
        this.internalName = internalName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getInternalName() {
        return internalName;
    }
    
    public static LeaderboardType fromInternalName(String name) {
        for (LeaderboardType type : values()) {
            if (type.internalName.equals(name)) {
                return type;
            }
        }
        return PRODUCTION;
    }
}
