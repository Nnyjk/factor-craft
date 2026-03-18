package com.factorcraft.module.event.worldevent;

/**
 * 世界事件类型枚举
 * 
 * 定义 5 种 Factor 世界事件类型
 */
public enum WorldEventType {
    /**
     * Factor 浓度波动
     * - 区域 Factor 浓度突然变化
     * - 机器效率变化，生物变异率变化
     * - 持续时间: 10-30 分钟
     * - 触发: 随机或玩家行为
     */
    CONCENTRATION_FLUCTUATION(
        "concentration_fluctuation",
        "Factor Fluctuation",
        10 * 60 * 20,  // 最小持续时间: 10 分钟 (ticks)
        30 * 60 * 20,  // 最大持续时间: 30 分钟
        0.15,          // 基础触发概率
        12000         // 检测间隔: 10 分钟 (ticks)
    ),
    
    /**
     * Factor 风暴
     * - 大规模 Factor 能量爆发
     * - 高浓度区域扩张，生物大量变异
     * - 机器过载风险，玩家临时效果
     * - 持续时间: 5-15 分钟
     * - 触发: 浓度达到阈值
     */
    FACTOR_STORM(
        "factor_storm",
        "Factor Storm",
        5 * 60 * 20,   // 最小持续时间: 5 分钟
        15 * 60 * 20,  // 最大持续时间: 15 分钟
        0.05,          // 基础触发概率 (较低)
        24000         // 检测间隔: 20 分钟
    ),
    
    /**
     * Factor 潮汐
     * - 周期性浓度涨落
     * - 全球/维度范围浓度变化
     * - 周期: 游戏内 1 天
     * - 与 TideSystem 关联
     */
    FACTOR_TIDE(
        "factor_tide",
        "Factor Tide",
        20 * 60 * 20,  // 持续时间: 20 分钟 (半个游戏日)
        20 * 60 * 20,  // 固定持续时间
        1.0,           // 必然触发 (周期性)
        24000         // 周期: 1 游戏日 (20 分钟)
    ),
    
    /**
     * Factor 喷发
     * - 地底 Factor 喷涌而出
     * - 新 Factor 源生成，地形改变
     * - 稀有物品生成
     * - 持续时间: 瞬间事件
     */
    FACTOR_ERUPTION(
        "factor_eruption",
        "Factor Eruption",
        30 * 20,       // 持续时间: 30 秒
        2 * 60 * 20,   // 最大持续时间: 2 分钟 (后续效果)
        0.02,          // 极低触发概率
        6000          // 检测间隔: 5 分钟
    ),
    
    /**
     * 虚空侵蚀
     * - 虚空 Factor 吞噬区域
     * - 浓度下降，方块消失风险
     * - 生物消失
     * - 持续时间: 持续到被阻止
     */
    VOID_EROSION(
        "void_erosion",
        "Void Erosion",
        5 * 60 * 20,   // 最小持续时间: 5 分钟
        -1,            // 无限持续 (直到被阻止)
        0.01,          // 极低触发概率
        12000         // 检测间隔: 10 分钟
    );
    
    private final String id;
    private final String displayName;
    private final int minDurationTicks;
    private final int maxDurationTicks;
    private final double baseProbability;
    private final int checkIntervalTicks;
    
    WorldEventType(String id, String displayName, 
                   int minDurationTicks, int maxDurationTicks,
                   double baseProbability, int checkIntervalTicks) {
        this.id = id;
        this.displayName = displayName;
        this.minDurationTicks = minDurationTicks;
        this.maxDurationTicks = maxDurationTicks;
        this.baseProbability = baseProbability;
        this.checkIntervalTicks = checkIntervalTicks;
    }
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getMinDurationTicks() {
        return minDurationTicks;
    }
    
    public int getMaxDurationTicks() {
        return maxDurationTicks;
    }
    
    public double getBaseProbability() {
        return baseProbability;
    }
    
    public int getCheckIntervalTicks() {
        return checkIntervalTicks;
    }
    
    /**
     * 是否为瞬间事件
     */
    public boolean isInstant() {
        return this == FACTOR_ERUPTION;
    }
    
    /**
     * 是否为持续事件（需要手动终止）
     */
    public boolean isInfinite() {
        return maxDurationTicks < 0;
    }
    
    /**
     * 是否为周期性事件
     */
    public boolean isPeriodic() {
        return this == FACTOR_TIDE;
    }
}