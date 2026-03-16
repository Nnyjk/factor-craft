package com.factorcraft.module.technology.screen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ScreenHandler 数据同步测试
 */
public class ScreenHandlerTest {
    
    @Test
    @DisplayName("SynthesizerCoreScreenHandler.SyncData 应正确创建")
    void testSyncDataCreation() {
        // 创建测试数据
        var data = new SynthesizerCoreScreenHandler.SyncData(
            3,              // tier
            5000.0,         // factorBuffer
            50000.0,        // maxBuffer
            true,           // structureValid
            1.5,            // efficiency
            "minecraft:the_nether", // dimension
            "t3_to_t4",     // currentRecipeId
            600,            // craftProgress
            3600,           // craftTimeTotal
            10000.0,        // factorNeeded
            1666.67         // factorConsumed
        );
        
        // 验证 getter
        assertEquals(3, data.tier());
        assertEquals(5000.0, data.factorBuffer());
        assertEquals(50000.0, data.maxBuffer());
        assertTrue(data.structureValid());
        assertEquals(1.5, data.efficiency());
        assertEquals("minecraft:the_nether", data.dimension());
        assertEquals("t3_to_t4", data.currentRecipeId());
        assertEquals(600, data.craftProgress());
        assertEquals(3600, data.craftTimeTotal());
        assertEquals(10000.0, data.factorNeeded());
        assertEquals(1666.67, data.factorConsumed());
    }
    
    @Test
    @DisplayName("ExtractorCoreSyncPayload 应正确创建")
    void testExtractorCoreSyncPayload() {
        var payload = new com.factorcraft.module.technology.network.ExtractorCoreSyncPayload(
            750.0,          // factorStorage
            1000.0,         // maxStorage
            1.2,            // efficiency
            1.0,            // dimensionEfficiency
            2.5,            // extractRate
            50.0,           // progress
            2,              // tier
            true,           // structureValid
            "minecraft:overworld", // dimension
            "主世界"        // recommendedDimension
        );
        
        assertEquals(750.0, payload.factorStorage());
        assertEquals(1000.0, payload.maxStorage());
        assertEquals(1.2, payload.efficiency());
        assertEquals(1.0, payload.dimensionEfficiency());
        assertEquals(2.5, payload.extractRate());
        assertEquals(50.0, payload.progress());
        assertEquals(2, payload.tier());
        assertTrue(payload.structureValid());
        assertEquals("minecraft:overworld", payload.dimension());
        assertEquals("主世界", payload.recommendedDimension());
    }
    
    @Test
    @DisplayName("存储百分比计算应正确")
    void testStoragePercentage() {
        // 测试边界值
        assertEquals(0.0, calculatePercentage(0, 1000), 0.01);
        assertEquals(50.0, calculatePercentage(500, 1000), 0.01);
        assertEquals(100.0, calculatePercentage(1000, 1000), 0.01);
        assertEquals(0.0, calculatePercentage(500, 0), 0.01); // 防止除零
    }
    
    @Test
    @DisplayName("进度百分比计算应正确")
    void testProgressPercentage() {
        assertEquals(0.0, calculateProgress(0, 100), 0.01);
        assertEquals(50.0, calculateProgress(50, 100), 0.01);
        assertEquals(100.0, calculateProgress(100, 100), 0.01);
        assertEquals(0.0, calculateProgress(50, 0), 0.01); // 防止除零
    }
    
    // 辅助方法
    private double calculatePercentage(double current, double max) {
        return max > 0 ? (current / max) * 100 : 0;
    }
    
    private double calculateProgress(int current, int total) {
        return total > 0 ? (current * 100.0) / total : 0;
    }
}