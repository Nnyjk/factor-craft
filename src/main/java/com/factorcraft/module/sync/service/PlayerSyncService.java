package com.factorcraft.module.sync.service;

import com.factorcraft.module.sync.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

/**
 * 玩家数据同步服务
 */
public class PlayerSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerSyncService.class);
    private static final String KEY_PREFIX = "factorcraft:player:";
    
    private final RedisClient redisClient;
    
    public PlayerSyncService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }
    
    public void syncPlayerFactor(UUID playerId, double factor) {
        String key = KEY_PREFIX + playerId + ":factor";
        redisClient.set(key, String.valueOf(factor));
    }
    
    public Double getPlayerFactor(UUID playerId) {
        String value = redisClient.get(KEY_PREFIX + playerId + ":factor");
        return value != null ? Double.parseDouble(value) : null;
    }
    
    public void syncPlayerTraits(UUID playerId, String traitsJson) {
        redisClient.set(KEY_PREFIX + playerId + ":traits", traitsJson);
    }
    
    public String getPlayerTraits(UUID playerId) {
        return redisClient.get(KEY_PREFIX + playerId + ":traits");
    }
}