package com.factorcraft.module.social.screen;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.resource.featuretoggle.FeatureFlags;

/**
 * 社交模块 ScreenHandler 注册
 */
public class ModScreenHandlers {
    
    public static ScreenHandlerType<MarketScreenHandler> MARKET;
    public static ScreenHandlerType<ExchangeScreenHandler> EXCHANGE;
    public static ScreenHandlerType<LeaderboardScreenHandler> LEADERBOARD;
    
    /**
     * 注册所有 ScreenHandlerType
     */
    public static void register() {
        MARKET = new ScreenHandlerType<>(MarketScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
        EXCHANGE = new ScreenHandlerType<>(ExchangeScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
        LEADERBOARD = new ScreenHandlerType<>(LeaderboardScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
    }
    
    public static Identifier getMarketId() {
        return Identifier.of("factorcraft", "market");
    }
    
    public static Identifier getExchangeId() {
        return Identifier.of("factorcraft", "exchange");
    }
    
    public static Identifier getLeaderboardId() {
        return Identifier.of("factorcraft", "leaderboard");
    }
}
