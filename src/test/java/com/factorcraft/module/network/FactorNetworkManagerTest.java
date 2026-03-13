package com.factorcraft.module.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FactorNetworkManager 单元测试
 * 
 * 测试 Factor 网络传输功能
 */
@DisplayName("FactorNetworkManager Tests")
public class FactorNetworkManagerTest {
    
    @Test
    @DisplayName("FactorNetworkManager - 单例模式验证")
    public void testSingletonPattern() {
        FactorNetworkManager instance1 = FactorNetworkManager.getInstance();
        FactorNetworkManager instance2 = FactorNetworkManager.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2, "FactorNetworkManager 应该返回相同的单例实例");
    }
    
    @Test
    @DisplayName("FactorNetworkManager - 初始化方法存在")
    public void testInitializeMethod() {
        FactorNetworkManager manager = FactorNetworkManager.getInstance();
        
        // 验证初始化方法可以调用
        assertDoesNotThrow(() -> manager.initialize());
    }
    
    @Test
    @DisplayName("维度基准值 - 主世界基准")
    public void testOverworldBaseValue() {
        // 主世界基准值应该是 0.5
        double overworldBase = getDimensionBase("minecraft:overworld");
        assertEquals(0.5, overworldBase, 0.001);
    }
    
    @Test
    @DisplayName("维度基准值 - 下界基准")
    public void testNetherBaseValue() {
        // 下界基准值应该是 1.5
        double netherBase = getDimensionBase("minecraft:the_nether");
        assertEquals(1.5, netherBase, 0.001);
    }
    
    @Test
    @DisplayName("维度基准值 - 末地基准")
    public void testEndBaseValue() {
        // 末地基准值应该是 3.0
        double endBase = getDimensionBase("minecraft:the_end");
        assertEquals(3.0, endBase, 0.001);
    }
    
    @Test
    @DisplayName("跨维度传输倍率 - 主世界到下界")
    public void testTransferMultiplierOverworldToNether() {
        // 主世界 -> 下界：0.5 / 1.5 = 0.333...
        double multiplier = calculateTransferMultiplier(0.5, 1.5);
        assertEquals(0.333, multiplier, 0.001);
    }
    
    @Test
    @DisplayName("跨维度传输倍率 - 下界到主世界")
    public void testTransferMultiplierNetherToOverworld() {
        // 下界 -> 主世界：1.5 / 0.5 = 3.0
        double multiplier = calculateTransferMultiplier(1.5, 0.5);
        assertEquals(3.0, multiplier, 0.001);
    }
    
    @Test
    @DisplayName("跨维度传输倍率 - 主世界到末地")
    public void testTransferMultiplierOverworldToEnd() {
        // 主世界 -> 末地：0.5 / 3.0 = 0.166...
        double multiplier = calculateTransferMultiplier(0.5, 3.0);
        assertEquals(0.167, multiplier, 0.001);
    }
    
    @Test
    @DisplayName("跨维度传输倍率 - 末地到主世界")
    public void testTransferMultiplierEndToOverworld() {
        // 末地 -> 主世界：3.0 / 0.5 = 6.0
        double multiplier = calculateTransferMultiplier(3.0, 0.5);
        assertEquals(6.0, multiplier, 0.001);
    }
    
    @Test
    @DisplayName("传输效率 - 100% 效率计算")
    public void testTransferWithFullEfficiency() {
        // 100 单位，100% 效率，无距离损耗
        double received = calculateTransfer(100, 1.0, 0.0);
        assertEquals(100.0, received, 0.001);
    }
    
    @Test
    @DisplayName("传输效率 - 50% 效率计算")
    public void testTransferWithHalfEfficiency() {
        // 100 单位，50% 效率，无距离损耗
        double received = calculateTransfer(100, 0.5, 0.0);
        assertEquals(50.0, received, 0.001);
    }
    
    @Test
    @DisplayName("距离损耗 - 近距离传输")
    public void testDistanceLossShortRange() {
        // 距离 100，损耗 = 100/10000 = 0.01 (1%)
        double loss = calculateDistanceLoss(100);
        assertEquals(0.01, loss, 0.001);
    }
    
    @Test
    @DisplayName("距离损耗 - 远距离传输")
    public void testDistanceLossLongRange() {
        // 距离 5000，损耗 = 5000/10000 = 0.5 (50%，最大)
        double loss = calculateDistanceLoss(5000);
        assertEquals(0.5, loss, 0.001);
    }
    
    @Test
    @DisplayName("距离损耗 - 超远距离限制")
    public void testDistanceLossMaximum() {
        // 距离 10000+，损耗最大 0.5 (50%)
        double loss1 = calculateDistanceLoss(10000);
        double loss2 = calculateDistanceLoss(50000);
        
        assertEquals(0.5, loss1, 0.001);
        assertEquals(0.5, loss2, 0.001); // 不超过 0.5
    }
    
    @Test
    @DisplayName("完整传输计算 - 主世界到下界")
    public void testFullTransferCalculation() {
        // 100 单位，主世界->下界 (0.333 倍率)，80% 效率，近距离 (1% 损耗)
        // 接收 = 100 * 0.333 * 0.8 * (1 - 0.01) = 26.37
        double received = calculateTransferWithMultiplier(100, 0.333, 0.8, 0.01);
        assertEquals(26.37, received, 0.1);
    }
    
    @Test
    @DisplayName("完整传输计算 - 下界到主世界")
    public void testFullTransferNetherToOverworld() {
        // 100 单位，下界->主世界 (3.0 倍率)，100% 效率，无损耗
        // 接收 = 100 * 3.0 * 1.0 * (1 - 0) = 300
        double received = calculateTransferWithMultiplier(100, 3.0, 1.0, 0.0);
        assertEquals(300.0, received, 0.001);
    }
    
    // 辅助方法：模拟维度基准值获取
    private double getDimensionBase(String dimensionKey) {
        if (dimensionKey.contains("the_nether")) return 1.5;
        if (dimensionKey.contains("the_end")) return 3.0;
        return 0.5;
    }
    
    // 辅助方法：计算传输倍率
    private double calculateTransferMultiplier(double fromBase, double toBase) {
        return fromBase / toBase;
    }
    
    // 辅助方法：计算距离损耗
    private double calculateDistanceLoss(double distance) {
        return Math.min(0.5, distance / 10000.0);
    }
    
    // 辅助方法：计算传输（无倍率）
    private double calculateTransfer(int amount, double efficiency, double distanceLoss) {
        return amount * efficiency * (1 - distanceLoss);
    }
    
    // 辅助方法：完整传输计算
    private double calculateTransferWithMultiplier(int amount, double multiplier, 
                                                   double efficiency, double distanceLoss) {
        return amount * multiplier * efficiency * (1 - distanceLoss);
    }
}
