package com.factorcraft.module.sync.service;

import com.factorcraft.module.sync.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区块数据同步服务
 */
public class ChunkSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkSyncService.class);
    private static final String KEY_PREFIX = "factorcraft:chunk:";
    
    private final RedisClient redisClient;
    
    public ChunkSyncService(RedisClient redisClient) {
        this.redisClient = redisClient;
    }
    
    public void syncChunkFactor(String dimension, int x, int z, double concentration) {
        String key = KEY_PREFIX + dimension + ":" + x + ":" + z;
        redisClient.set(key, String.valueOf(concentration));
    }
    
    public Double getChunkFactor(String dimension, int x, int z) {
        String value = redisClient.get(KEY_PREFIX + dimension + ":" + x + ":" + z);
        return value != null ? Double.parseDouble(value) : null;
    }
    
    public record ChunkData(int x, int z, double concentration) {}
}