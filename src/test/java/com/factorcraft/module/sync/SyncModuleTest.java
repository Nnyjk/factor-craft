package com.factorcraft.module.sync;

import com.factorcraft.module.sync.config.SyncConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SyncModule 单元测试
 */
class SyncModuleTest {
    
    @Test
    void testDefaultConfig() {
        SyncConfig config = SyncConfig.load();
        assertFalse(config.enabled());
        assertEquals("localhost", config.redisHost());
        assertEquals(6379, config.redisPort());
        assertEquals(100, config.syncIntervalTicks());
        assertEquals(50, config.batchSize());
        assertEquals("default", config.serverId());
    }
    
    @Test
    void testModuleDisabledByDefault() {
        SyncModule module = SyncModule.getInstance();
        assertFalse(module.isEnabled());
    }
    
    @Test
    void testGetConfig() {
        SyncModule module = SyncModule.getInstance();
        SyncConfig config = module.getSyncConfig();
        assertNotNull(config);
        assertFalse(config.enabled());
    }
}