package com.factorcraft.module.social;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.social.manager.PermissionConfig;
import com.factorcraft.module.social.market.MarketConfig;
import com.factorcraft.module.social.market.MarketManager;
import com.factorcraft.module.social.exchange.ExchangeManager;
import com.factorcraft.module.social.leaderboard.LeaderboardManager;

import java.util.List;

/**
 * 社交模块 - 多人游戏功能
 * 
 * 功能:
 * - 权限管理
 * - 权限组配置
 * - 玩家交易市场
 * - Factor 交易所
 * - 排行榜系统
 */
public final class SocialModule implements FactorCraftModule {
    
    private static SocialModule instance;
    
    public SocialModule() {
        instance = this;
    }
    
    public static SocialModule getInstance() {
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "social";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of(); // 无依赖
    }
    
    @Override
    public void initialize() {
        // 加载权限配置
        PermissionConfig.load();
        
        // 加载市场配置
        MarketConfig.getInstance().load();
        
        // 初始化市场管理器
        MarketManager marketManager = MarketManager.getInstance();
        
        // 初始化交易所管理器
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        
        // 初始化排行榜管理器
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Social] 社交模块已加载");
        FactorCraftMod.LOGGER.info("[FactorCraft:Social] 功能：权限管理，交易市场，Factor 交易所，排行榜");
        FactorCraftMod.LOGGER.info("[FactorCraft:Social] 市场税费：{}%", MarketConfig.getInstance().getMarketTaxRate());
        FactorCraftMod.LOGGER.info("[FactorCraft:Social] Factor 基准价格：{}", exchangeManager.getCurrentPrice());
    }
}
