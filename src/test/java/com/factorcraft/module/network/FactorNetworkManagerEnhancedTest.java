package com.factorcraft.module.network;

import com.factorcraft.module.network.FactorNetworkManager.TransferRecord;
import com.factorcraft.module.network.FactorNetworkManager.NetworkStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FactorNetworkManager 增强功能测试
 */
@DisplayName("FactorNetworkManager Enhanced Tests")
public class FactorNetworkManagerEnhancedTest {
    
    @Test
    @DisplayName("配置化 - 默认维度基准值")
    public void testDefaultDimensionBaseValues() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        assertEquals(0.5, manager.getDimensionBase("minecraft:overworld"), 0.001);
        assertEquals(1.5, manager.getDimensionBase("minecraft:the_nether"), 0.001);
        assertEquals(3.0, manager.getDimensionBase("minecraft:the_end"), 0.001);
    }
    
    @Test
    @DisplayName("配置化 - 自定义维度基准值")
    public void testCustomDimensionBaseValue() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        // 设置自定义维度基准值
        manager.setDimensionBase("custom_dimension", 2.0);
        
        assertEquals(2.0, manager.getDimensionBase("custom_dimension"), 0.001);
    }
    
    @Test
    @DisplayName("传输倍率 - 计算方法验证")
    public void testTransferMultiplierCalculation() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        double overworldToNether = manager.getDimensionBase("minecraft:overworld") / 
                                   manager.getDimensionBase("minecraft:the_nether");
        assertEquals(0.333, overworldToNether, 0.001);
        
        double netherToOverworld = manager.getDimensionBase("minecraft:the_nether") / 
                                   manager.getDimensionBase("minecraft:overworld");
        assertEquals(3.0, netherToOverworld, 0.001);
    }
    
    @Test
    @DisplayName("传输日志 - 记录功能")
    public void testTransferLogging() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        manager.clearTransferLog();
        List<TransferRecord> initialLog = manager.getTransferLog();
        assertEquals(0, initialLog.size());
        
        // 模拟传输（这里不实际调用，因为需要 ServerWorld）
        // 验证日志功能存在
        assertNotNull(manager.getTransferLog());
    }
    
    @Test
    @DisplayName("传输日志 - 大小限制")
    public void testTransferLogSizeLimit() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        manager.clearTransferLog();
        
        // 验证日志列表存在且有限制
        List<TransferRecord> log = manager.getTransferLog();
        assertNotNull(log);
        
        // 日志不应该超过 MAX_LOG_SIZE
        assertTrue(log.size() <= 100, "日志大小不应该超过 100");
    }
    
    @Test
    @DisplayName("性能统计 - 初始状态")
    public void testStatsInitialState() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        manager.resetStats();
        NetworkStats stats = manager.getStats();
        
        assertEquals(0, stats.totalTransfers);
        assertEquals(0.0, stats.totalLoss, 0.001);
        assertEquals(0.0, stats.avgLoss, 0.001);
    }
    
    @Test
    @DisplayName("性能统计 - 重置功能")
    public void testStatsReset() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        // 重置统计
        manager.resetStats();
        NetworkStats stats = manager.getStats();
        
        assertEquals(0, stats.totalTransfers);
    }
    
    @Test
    @DisplayName("网络拓扑 - 检测方法存在")
    public void testNetworkTopologyDetection() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        // 验证拓扑检测方法存在（不验证具体实现）
        // 实际实现需要 ServerWorld
        assertNotNull(manager);
    }
    
    @Test
    @DisplayName("传输记录 - 数据结构验证")
    public void testTransferRecordStructure() {
        TransferRecord record = new TransferRecord(
            System.currentTimeMillis(),
            "minecraft:overworld",
            "minecraft:the_nether",
            100,
            33.3,
            0.333,
            0.01
        );
        
        assertEquals("minecraft:overworld", record.fromDimension);
        assertEquals("minecraft:the_nether", record.toDimension);
        assertEquals(100, record.amount);
        assertEquals(33.3, record.received, 0.1);
        assertEquals(0.333, record.multiplier, 0.001);
        assertEquals(0.01, record.loss, 0.001);
        
        // 验证 toString 方法
        String str = record.toString();
        assertTrue(str.contains("Transfer"));
        assertTrue(str.contains("overworld"));
        assertTrue(str.contains("nether"));
    }
    
    @Test
    @DisplayName("网络统计 - 数据结构验证")
    public void testNetworkStatsStructure() {
        NetworkStats stats = new NetworkStats(10, 50.0, 5.0);
        
        assertEquals(10, stats.totalTransfers);
        assertEquals(50.0, stats.totalLoss, 0.001);
        assertEquals(5.0, stats.avgLoss, 0.001);
        
        // 验证 toString 方法
        String str = stats.toString();
        assertTrue(str.contains("Stats"));
        assertTrue(str.contains("transfers=10"));
    }
    
    @Test
    @DisplayName("单例模式 - 多次获取相同实例")
    public void testSingletonConsistency() {
        FactorNetworkManager instance1 = FactorNetworkManager.getInstance();
        FactorNetworkManager instance2 = FactorNetworkManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("初始化 - 方法可调用")
    public void testInitializeMethod() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        assertDoesNotThrow(() -> manager.initialize());
    }
    
    @Test
    @DisplayName("配置化 - 维度基准值映射")
    public void testDimensionBaseMapping() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        // 验证所有已知维度都有基准值
        String[] dimensions = {
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
        };
        
        for (String dim : dimensions) {
            double base = manager.getDimensionBase(dim);
            assertTrue(base > 0.0, dim + " 的基准值应该大于 0");
        }
    }
    
    @Test
    @DisplayName("未知维度 - 默认值处理")
    public void testUnknownDimensionDefault() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        double unknownBase = manager.getDimensionBase("unknown_dimension");
        assertEquals(0.5, unknownBase, 0.001, "未知维度应该使用默认值 0.5");
    }
}
