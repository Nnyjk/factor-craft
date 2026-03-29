package com.factorcraft.module.factor.balance;

import com.factorcraft.config.MultiplayerBalanceConfig;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * R3.4 多人 Factor 平衡管理器
 * 
 * 根据玩家数量动态调整 Factor 浓度增长速率
 * 确保多人游戏体验平衡
 */
public class MultiplayerFactorBalance {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplayerFactorBalance.class);
    private static MultiplayerFactorBalance instance;
    
    private MultiplayerBalanceConfig config;
    
    private MultiplayerFactorBalance() {
        this.config = MultiplayerBalanceConfig.getInstance();
    }
    
    /**
     * 获取实例
     */
    public static MultiplayerFactorBalance getInstance() {
        if (instance == null) {
            instance = new MultiplayerFactorBalance();
        }
        return instance;
    }
    
    /**
     * 根据玩家数量计算浓度倍数
     * 
     * @param playerCount 玩家数量
     * @return 浓度倍数
     */
    public double getConcentrationMultiplier(int playerCount) {
        if (!config.isEnabled()) {
            return 1.0;
        }
        
        // 限制最大玩家数阈值
        int effectivePlayers = Math.min(playerCount, config.getMaxPlayerThreshold());
        
        // 基础倍数 + 每玩家额外倍数
        double multiplier = 1.0 + (effectivePlayers - 1) * config.getConcentrationMultiplierPerPlayer();
        
        return multiplier;
    }
    
    /**
     * 调整 Factor 浓度值
     * 
     * @param concentration 原始浓度
     * @param playerCount 玩家数量
     * @return 调整后的浓度
     */
    public double adjustForMultiplayer(double concentration, int playerCount) {
        if (playerCount <= 1 || !config.isEnabled()) {
            return concentration;
        }
        
        double multiplier = getConcentrationMultiplier(playerCount);
        return concentration * multiplier;
    }
    
    /**
     * 计算动态难度等级
     * 
     * @param playerCount 玩家数量
     * @param averagePlayTime 平均游戏时间（tick）
     * @return 难度等级 (1-5)
     */
    public int getDifficultyLevel(int playerCount, long averagePlayTime) {
        if (!config.isEnabled()) {
            return 1;
        }
        
        // 基于玩家数量的基础难度
        int playerDifficulty = Math.min(playerCount, 5);
        
        // 基于游戏时间的难度加成
        int timeDifficulty = 1;
        if (averagePlayTime > 72000) { // 1 小时
            timeDifficulty = 2;
        }
        if (averagePlayTime > 240000) { // 4 小时
            timeDifficulty = 3;
        }
        if (averagePlayTime > 720000) { // 12 小时
            timeDifficulty = 4;
        }
        
        // 综合难度
        int difficulty = Math.max(playerDifficulty, timeDifficulty);
        
        LOGGER.debug("计算难度等级：玩家={}, 时间={}tick, 难度={}", playerCount, averagePlayTime, difficulty);
        
        return difficulty;
    }
    
    /**
     * 获取世界中的有效玩家数量
     * 
     * @param world 世界
     * @return 玩家数量
     */
    public int getEffectivePlayerCount(ServerWorld world) {
        return world.getPlayers().size();
    }
    
    /**
     * 计算 Factor 生成速率调整
     * 
     * @param baseRate 基础速率
     * @param playerCount 玩家数量
     * @return 调整后的速率
     */
    public double adjustGenerationRate(double baseRate, int playerCount) {
        if (!config.isEnabled() || playerCount <= 1) {
            return baseRate;
        }
        
        // 生成速率随玩家数量增加，但有递减效应
        double playerFactor = Math.log(playerCount + 1) / Math.log(2);
        return baseRate * playerFactor;
    }
    
    /**
     * 计算 Factor 消耗速率调整
     * 
     * @param baseRate 基础速率
     * @param playerCount 玩家数量
     * @return 调整后的速率
     */
    public double adjustConsumptionRate(double baseRate, int playerCount) {
        if (!config.isEnabled() || playerCount <= 1) {
            return baseRate;
        }
        
        // 消耗速率线性增加
        return baseRate * playerCount;
    }
    
    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        this.config = MultiplayerBalanceConfig.getInstance();
        this.config.reload();
    }
}
