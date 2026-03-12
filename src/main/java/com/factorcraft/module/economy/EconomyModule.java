package com.factorcraft.module.economy;

import com.factorcraft.module.economy.api.EconomyApi;
import com.factorcraft.module.economy.config.EconomyConfig;
import com.factorcraft.module.economy.service.MarketService;
import com.factorcraft.module.economy.service.AuctionService;
import com.factorcraft.module.economy.service.TradingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 13: 经济系统深化模块
 */
public class EconomyModule implements EconomyApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(EconomyModule.class);
    private static EconomyModule INSTANCE;
    
    private final EconomyConfig config;
    private MarketService marketService;
    private AuctionService auctionService;
    private TradingService tradingService;
    
    private EconomyModule() {
        this.config = EconomyConfig.load();
    }
    
    public static synchronized EconomyModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EconomyModule();
        }
        return INSTANCE;
    }
    
    public void initialize() {
        if (!config.enabled()) {
            LOGGER.info("Economy module disabled");
            return;
        }
        
        LOGGER.info("Initializing economy module...");
        
        this.marketService = new MarketService(config);
        this.auctionService = new AuctionService(config);
        this.tradingService = new TradingService(config);
        
        LOGGER.info("Economy module initialized");
    }
    
    public void shutdown() {
        LOGGER.info("Economy module shut down");
    }
    
    @Override
    public boolean isEnabled() {
        return config.enabled();
    }
    
    @Override
    public EconomyConfig getConfig() {
        return config;
    }
    
    public MarketService getMarketService() { return marketService; }
    public AuctionService getAuctionService() { return auctionService; }
    public TradingService getTradingService() { return tradingService; }
}