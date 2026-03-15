package com.factorcraft.module.technology.machine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 传递器系统测试
 */
public class TransmitterSystemTest {
    
    // ==================== 效率测试 ====================
    
    @Test
    @DisplayName("效率应随 Tier 提高")
    void testEfficiencyImproves() {
        assertTrue(TransmitterConfig.getEfficiency(1) < TransmitterConfig.getEfficiency(2));
        assertTrue(TransmitterConfig.getEfficiency(2) < TransmitterConfig.getEfficiency(3));
        assertTrue(TransmitterConfig.getEfficiency(3) < TransmitterConfig.getEfficiency(4));
    }
    
    @Test
    @DisplayName("T1 效率应为 80%")
    void testT1Efficiency() {
        assertEquals(0.80, TransmitterConfig.getEfficiency(1), 0.001);
    }
    
    @Test
    @DisplayName("T4 效率应为 95%")
    void testT4Efficiency() {
        assertEquals(0.95, TransmitterConfig.getEfficiency(4), 0.001);
    }
    
    // ==================== 距离损耗测试 ====================
    
    @Test
    @DisplayName("距离损耗应随 Tier 降低")
    void testDistanceLossDecreases() {
        assertTrue(TransmitterConfig.getDistanceLoss(1) > TransmitterConfig.getDistanceLoss(2));
        assertTrue(TransmitterConfig.getDistanceLoss(2) > TransmitterConfig.getDistanceLoss(3));
        assertTrue(TransmitterConfig.getDistanceLoss(3) > TransmitterConfig.getDistanceLoss(4));
    }
    
    @Test
    @DisplayName("T1 距离损耗应为 1%/百格")
    void testT1DistanceLoss() {
        assertEquals(0.010, TransmitterConfig.getDistanceLoss(1), 0.001);
    }
    
    @Test
    @DisplayName("T4 距离损耗应为 0.3%/百格")
    void testT4DistanceLoss() {
        assertEquals(0.003, TransmitterConfig.getDistanceLoss(4), 0.001);
    }
    
    // ==================== 最大传输量测试 ====================
    
    @Test
    @DisplayName("最大传输量应随 Tier 增加")
    void testMaxTransferIncreases() {
        assertTrue(TransmitterConfig.getMaxTransfer(1) < TransmitterConfig.getMaxTransfer(2));
        assertTrue(TransmitterConfig.getMaxTransfer(2) < TransmitterConfig.getMaxTransfer(3));
        assertTrue(TransmitterConfig.getMaxTransfer(3) < TransmitterConfig.getMaxTransfer(4));
    }
    
    @Test
    @DisplayName("T1 最大传输应为 1000")
    void testT1MaxTransfer() {
        assertEquals(1000.0, TransmitterConfig.getMaxTransfer(1), 0.001);
    }
    
    @Test
    @DisplayName("T4 最大传输应为 100000")
    void testT4MaxTransfer() {
        assertEquals(100000.0, TransmitterConfig.getMaxTransfer(4), 0.001);
    }
    
    // ==================== 冷却时间测试 ====================
    
    @Test
    @DisplayName("冷却时间应随 Tier 减少")
    void testCooldownDecreases() {
        assertTrue(TransmitterConfig.getCooldown(1) > TransmitterConfig.getCooldown(2));
        assertTrue(TransmitterConfig.getCooldown(2) > TransmitterConfig.getCooldown(3));
        assertTrue(TransmitterConfig.getCooldown(3) > TransmitterConfig.getCooldown(4));
    }
    
    @Test
    @DisplayName("T1 冷却应为 200 ticks (10秒)")
    void testT1Cooldown() {
        assertEquals(200, TransmitterConfig.getCooldown(1));
    }
    
    @Test
    @DisplayName("T4 冷却应为 60 ticks (3秒)")
    void testT4Cooldown() {
        assertEquals(60, TransmitterConfig.getCooldown(4));
    }
    
    // ==================== 缓冲区测试 ====================
    
    @Test
    @DisplayName("缓冲区应随 Tier 增加")
    void testBufferIncreases() {
        assertTrue(TransmitterConfig.getBuffer(1) < TransmitterConfig.getBuffer(2));
        assertTrue(TransmitterConfig.getBuffer(2) < TransmitterConfig.getBuffer(3));
        assertTrue(TransmitterConfig.getBuffer(3) < TransmitterConfig.getBuffer(4));
    }
    
    @Test
    @DisplayName("T1 缓冲区应为 2000")
    void testT1Buffer() {
        assertEquals(2000.0, TransmitterConfig.getBuffer(1), 0.001);
    }
    
    @Test
    @DisplayName("T4 缓冲区应为 200000")
    void testT4Buffer() {
        assertEquals(200000.0, TransmitterConfig.getBuffer(4), 0.001);
    }
    
    // ==================== 维度传输倍率测试 ====================
    
    @Test
    @DisplayName("下界→主世界倍率应为 3.0")
    void testNetherToOverworld() {
        assertEquals(3.0, TransmitterConfig.getDimensionMultiplier(
            "minecraft:the_nether", "minecraft:overworld"), 0.001);
    }
    
    @Test
    @DisplayName("末地→主世界倍率应为 6.0")
    void testEndToOverworld() {
        assertEquals(6.0, TransmitterConfig.getDimensionMultiplier(
            "minecraft:the_end", "minecraft:overworld"), 0.001);
    }
    
    @Test
    @DisplayName("末地→下界倍率应为 2.0")
    void testEndToNether() {
        assertEquals(2.0, TransmitterConfig.getDimensionMultiplier(
            "minecraft:the_end", "minecraft:the_nether"), 0.001);
    }
    
    @Test
    @DisplayName("主世界→下界倍率应为 0.33")
    void testOverworldToNether() {
        assertEquals(0.33, TransmitterConfig.getDimensionMultiplier(
            "minecraft:overworld", "minecraft:the_nether"), 0.001);
    }
    
    @Test
    @DisplayName("主世界→末地倍率应为 0.17")
    void testOverworldToEnd() {
        assertEquals(0.17, TransmitterConfig.getDimensionMultiplier(
            "minecraft:overworld", "minecraft:the_end"), 0.001);
    }
    
    @Test
    @DisplayName("同维度倍率应为 1.0")
    void testSameDimension() {
        assertEquals(1.0, TransmitterConfig.getDimensionMultiplier(
            "minecraft:overworld", "minecraft:overworld"), 0.001);
    }
    
    // ==================== 维度基准值测试 ====================
    
    @Test
    @DisplayName("主世界基准值应为 0.5")
    void testOverworldBase() {
        assertEquals(0.5, TransmitterConfig.getDimensionBase("minecraft:overworld"), 0.001);
    }
    
    @Test
    @DisplayName("下界基准值应为 1.5")
    void testNetherBase() {
        assertEquals(1.5, TransmitterConfig.getDimensionBase("minecraft:the_nether"), 0.001);
    }
    
    @Test
    @DisplayName("末地基准值应为 3.0")
    void testEndBase() {
        assertEquals(3.0, TransmitterConfig.getDimensionBase("minecraft:the_end"), 0.001);
    }
    
    // ==================== 完整传输计算测试 ====================
    
    @Test
    @DisplayName("完整计算: T1 同维度传输 1000")
    void testSameDimensionTransfer() {
        // T1 同维度传输 1000，距离 1000 格
        double received = TransmitterConfig.calculateSameDimensionTransfer(1000, 1, 1000);
        
        // 效率 80%, 距离损耗 10% (1000格 / 100 * 1%)
        // 1000 × 0.80 × 0.90 = 720
        assertEquals(720.0, received, 0.001);
    }
    
    @Test
    @DisplayName("完整计算: T2 下界→主世界传输")
    void testCrossDimensionTransfer_NetherToOverworld() {
        double received = TransmitterConfig.calculateTransfer(
            1000, "minecraft:the_nether", "minecraft:overworld", 2, 0);
        
        // 倍率 3.0, 效率 85%
        // 1000 × 3.0 × 0.85 = 2550
        assertEquals(2550.0, received, 0.001);
    }
    
    @Test
    @DisplayName("完整计算: T4 末地→主世界传输")
    void testCrossDimensionTransfer_EndToOverworld() {
        double received = TransmitterConfig.calculateTransfer(
            1000, "minecraft:the_end", "minecraft:overworld", 4, 0);
        
        // 倍率 6.0, 效率 95%
        // 1000 × 6.0 × 0.95 = 5700
        assertEquals(5700.0, received, 0.001);
    }
    
    @Test
    @DisplayName("完整计算: 主世界→下界传输（缩减）")
    void testCrossDimensionTransfer_OverworldToNether() {
        double received = TransmitterConfig.calculateTransfer(
            1000, "minecraft:overworld", "minecraft:the_nether", 2, 0);
        
        // 倍率 0.33, 效率 85%
        // 1000 × 0.33 × 0.85 = 280.5
        assertEquals(280.5, received, 0.1);
    }
    
    // ==================== 传输能力测试 ====================
    
    @Test
    @DisplayName("T1 只能同维度传输")
    void testT1SameDimensionOnly() {
        assertTrue(TransmitterConfig.canTransfer(1, "minecraft:overworld", "minecraft:overworld"));
        assertFalse(TransmitterConfig.canTransfer(1, "minecraft:overworld", "minecraft:the_nether"));
        assertFalse(TransmitterConfig.canTransfer(1, "minecraft:overworld", "minecraft:the_end"));
    }
    
    @Test
    @DisplayName("T2+ 可以跨维度传输")
    void testT2CrossDimension() {
        assertTrue(TransmitterConfig.canTransfer(2, "minecraft:overworld", "minecraft:the_nether"));
        assertTrue(TransmitterConfig.canTransfer(3, "minecraft:overworld", "minecraft:the_end"));
        assertTrue(TransmitterConfig.canTransfer(4, "minecraft:the_nether", "minecraft:the_end"));
    }
    
    // ==================== 材料需求测试 ====================
    
    @Test
    @DisplayName("传递器材料需求应正确")
    void testRequiredMaterials() {
        assertEquals("factorcraft:shadow_steel_ingot", TransmitterConfig.getRequiredMaterial(1));
        assertEquals("factorcraft:stardust_ingot", TransmitterConfig.getRequiredMaterial(2));
        assertEquals("factorcraft:ancient_alloy", TransmitterConfig.getRequiredMaterial(3));
        assertEquals("factorcraft:void_crystal", TransmitterConfig.getRequiredMaterial(4));
    }
    
    // ==================== 名称测试 ====================
    
    @Test
    @DisplayName("传递器名称应正确")
    void testTransmitterNames() {
        assertEquals("基础传递器", TransmitterConfig.getTransmitterName(1));
        assertEquals("维度传递器", TransmitterConfig.getTransmitterName(2));
        assertEquals("远古传递器", TransmitterConfig.getTransmitterName(3));
        assertEquals("仲裁传递器", TransmitterConfig.getTransmitterName(4));
    }
    
    // ==================== 传输描述测试 ====================
    
    @Test
    @DisplayName("传输描述应正确")
    void testTransferDescription() {
        String sameDim = TransmitterConfig.getTransferDescription(1, 
            "minecraft:overworld", "minecraft:overworld");
        assertTrue(sameDim.contains("80%"));
        
        String crossDim = TransmitterConfig.getTransferDescription(2, 
            "minecraft:the_nether", "minecraft:overworld");
        assertTrue(crossDim.contains("3.00x"));
        assertTrue(crossDim.contains("85%"));
    }
    
    @Test
    @DisplayName("T1 跨维度应提示限制")
    void testT1CrossDimensionRestriction() {
        String desc = TransmitterConfig.getTransferDescription(1, 
            "minecraft:overworld", "minecraft:the_nether");
        assertTrue(desc.contains("仅支持同维度"));
    }
    
    // ==================== 边界情况测试 ====================
    
    @Test
    @DisplayName("无效 Tier 应返回默认值")
    void testInvalidTier() {
        assertEquals(0.80, TransmitterConfig.getEfficiency(0), 0.001);
        assertEquals(0.80, TransmitterConfig.getEfficiency(99), 0.001);
        
        assertEquals(2000.0, TransmitterConfig.getBuffer(0), 0.001);
        assertEquals(200, TransmitterConfig.getCooldown(0));
    }
    
    @Test
    @DisplayName("未知维度基准值应为 1.0")
    void testUnknownDimensionBase() {
        assertEquals(1.0, TransmitterConfig.getDimensionBase("minecraft:unknown"), 0.001);
    }
    
    @Test
    @DisplayName("未知维度传输倍率应为 1.0")
    void testUnknownDimensionMultiplier() {
        assertEquals(1.0, TransmitterConfig.getDimensionMultiplier(
            "minecraft:unknown", "minecraft:the_nether"), 0.001);
    }
    
    @Test
    @DisplayName("长距离传输损耗有下限")
    void testLongDistanceLossCap() {
        // 10000 格距离
        double received = TransmitterConfig.calculateSameDimensionTransfer(1000, 1, 10000);
        
        // 损耗 100%, 但有下限保护
        // 实际计算: 1000 × 0.80 × max(0, 1 - 1.0) = 0
        assertEquals(0.0, received, 0.001);
    }
}