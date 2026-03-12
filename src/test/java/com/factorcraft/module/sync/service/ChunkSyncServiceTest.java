package com.factorcraft.module.sync.service;

import com.factorcraft.module.sync.redis.RedisClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChunkSyncServiceTest {
    
    private RedisClient redisClient;
    private ChunkSyncService service;
    
    @BeforeEach
    void setUp() {
        redisClient = new RedisClient("localhost", 6379);
        service = new ChunkSyncService(redisClient);
    }
    
    @Test
    void testSyncChunkFactor() {
        assertDoesNotThrow(() -> service.syncChunkFactor("minecraft:overworld", 10, 20, 50.5));
    }
    
    @Test
    void testGetChunkFactorWhenDisconnected() {
        assertNull(service.getChunkFactor("minecraft:overworld", 10, 20));
    }
    
    @Test
    void testChunkDataRecord() {
        ChunkSyncService.ChunkData data = new ChunkSyncService.ChunkData(10, 20, 50.5);
        assertEquals(10, data.x());
        assertEquals(20, data.z());
        assertEquals(50.5, data.concentration());
    }
}