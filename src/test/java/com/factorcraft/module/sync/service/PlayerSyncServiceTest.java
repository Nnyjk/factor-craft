package com.factorcraft.module.sync.service;

import com.factorcraft.module.sync.redis.RedisClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PlayerSyncServiceTest {
    
    private RedisClient redisClient;
    private PlayerSyncService service;
    
    @BeforeEach
    void setUp() {
        redisClient = new RedisClient("localhost", 6379);
        service = new PlayerSyncService(redisClient);
    }
    
    @Test
    void testSyncPlayerFactor() {
        UUID playerId = UUID.randomUUID();
        assertDoesNotThrow(() -> service.syncPlayerFactor(playerId, 100.5));
    }
    
    @Test
    void testSyncPlayerTraits() {
        UUID playerId = UUID.randomUUID();
        assertDoesNotThrow(() -> service.syncPlayerTraits(playerId, "{\"traits\":[]}"));
    }
    
    @Test
    void testGetPlayerFactorWhenDisconnected() {
        UUID playerId = UUID.randomUUID();
        assertNull(service.getPlayerFactor(playerId));
    }
}