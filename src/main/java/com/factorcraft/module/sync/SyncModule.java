package com.factorcraft.module.sync;

import com.factorcraft.module.sync.api.SyncApi;
import com.factorcraft.module.sync.api.SyncRegistrar;
import com.factorcraft.module.sync.config.SyncConfig;
import com.factorcraft.module.sync.network.SyncNetwork;
import com.factorcraft.module.sync.redis.RedisClient;
import com.factorcraft.module.sync.service.PlayerSyncService;
import com.factorcraft.module.sync.service.ChunkSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 12: 跨服同步模块
 */
public class SyncModule implements SyncApi, SyncRegistrar {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncModule.class);
    private static SyncModule INSTANCE;
    
    private final SyncConfig config;
    private RedisClient redisClient;
    private SyncNetwork network;
    private PlayerSyncService playerSyncService;
    private ChunkSyncService chunkSyncService;
    
    private SyncModule() {
        this.config = SyncConfig.load();
    }
    
    public static synchronized SyncModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SyncModule();
        }
        return INSTANCE;
    }
    
    public void initialize() {
        if (!config.enabled()) {
            LOGGER.info("Cross-server sync disabled");
            return;
        }
        
        LOGGER.info("Initializing cross-server sync...");
        
        this.redisClient = new RedisClient(config.redisHost(), config.redisPort());
        this.network = new SyncNetwork(redisClient);
        this.playerSyncService = new PlayerSyncService(redisClient);
        this.chunkSyncService = new ChunkSyncService(redisClient);
        
        redisClient.connect();
        network.subscribe();
        
        LOGGER.info("Cross-server sync initialized");
    }
    
    public void shutdown() {
        if (network != null) network.unsubscribe();
        if (redisClient != null) redisClient.disconnect();
        LOGGER.info("Cross-server sync shut down");
    }
    
    @Override
    public SyncConfig getSyncConfig() {
        return config;
    }
    
    @Override
    public boolean isEnabled() {
        return config.enabled() && redisClient != null && redisClient.isConnected();
    }
    
    public PlayerSyncService getPlayerSyncService() {
        return playerSyncService;
    }
    
    public ChunkSyncService getChunkSyncService() {
        return chunkSyncService;
    }
}