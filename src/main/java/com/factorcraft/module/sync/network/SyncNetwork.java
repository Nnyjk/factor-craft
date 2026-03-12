package com.factorcraft.module.sync.network;

import com.factorcraft.module.sync.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步网络层
 */
public class SyncNetwork {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncNetwork.class);
    private static final String CHANNEL_PLAYER = "factorcraft:player";
    private static final String CHANNEL_CHUNK = "factorcraft:chunk";
    
    private final RedisClient redisClient;
    
    public SyncNetwork(RedisClient redisClient) {
        this.redisClient = redisClient;
    }
    
    public void subscribe() {
        redisClient.subscribe(CHANNEL_PLAYER, this::handlePlayerMessage);
        redisClient.subscribe(CHANNEL_CHUNK, this::handleChunkMessage);
        LOGGER.info("Subscribed to sync channels");
    }
    
    public void unsubscribe() {}
    
    public void publishPlayerData(String playerUuid, String data) {
        redisClient.publish(CHANNEL_PLAYER, playerUuid + ":" + data);
    }
    
    public void publishChunkData(String chunkKey, String data) {
        redisClient.publish(CHANNEL_CHUNK, chunkKey + ":" + data);
    }
    
    private void handlePlayerMessage(String msg) {}
    private void handleChunkMessage(String msg) {}
}