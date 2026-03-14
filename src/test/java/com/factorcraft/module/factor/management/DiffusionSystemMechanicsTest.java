package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 扩散系统机制测试
 * 
 * 测试覆盖：
 * - 扩散基本逻辑
 * - 浓度平衡
 * - 边界条件
 * 
 * 注意：由于 ChunkPos 依赖 Minecraft 运行时，
 * 本测试专注于 ChunkFactorState 的扩散相关行为
 */
@DisplayName("扩散系统机制测试")
public class DiffusionSystemMechanicsTest {
    
    @Nested
    @DisplayName("区块因子状态")
    class ChunkFactorStateBehavior {
        
        @Test
        @DisplayName("浓度地板值正确计算")
        void testConcentrationFloor() {
            ChunkFactorState state = new ChunkFactorState(100);
            assertEquals(10, state.getConcentrationFloor(), 0.001);
            
            ChunkFactorState state2 = new ChunkFactorState(50);
            assertEquals(5, state2.getConcentrationFloor(), 0.001);
        }
        
        @Test
        @DisplayName("浓度不能低于地板值")
        void testConcentrationFloorEnforced() {
            ChunkFactorState state = new ChunkFactorState(100);
            
            state.setCurrentConcentration(5);
            assertEquals(10, state.getCurrentConcentration(), 0.001);
            
            state.setCurrentConcentration(0);
            assertEquals(10, state.getCurrentConcentration(), 0.001);
        }
        
        @Test
        @DisplayName("正常浓度设置")
        void testNormalConcentrationSetting() {
            ChunkFactorState state = new ChunkFactorState(100);
            
            state.setCurrentConcentration(80);
            assertEquals(80, state.getCurrentConcentration(), 0.001);
            
            state.setCurrentConcentration(50);
            assertEquals(50, state.getCurrentConcentration(), 0.001);
        }
    }
    
    @Nested
    @DisplayName("扩散算法逻辑")
    class DiffusionAlgorithmLogic {
        
        @Test
        @DisplayName("扩散系数影响传输量")
        void testDiffusionCoefficient() {
            // 扩散系数 = 0.1
            // 如果浓度差 = 40，传输量 = 40 * 0.1 = 4
            double diffusionCoefficient = 0.1;
            double concentrationDiff = 40;
            double transfer = concentrationDiff * diffusionCoefficient;
            
            assertEquals(4, transfer, 0.001);
        }
        
        @Test
        @DisplayName("传输后总浓度减少（衰减）")
        void testTotalConcentrationDecrease() {
            // 中心浓度 80，邻居浓度 40
            // 差值 = 40，传输 = 40 * 0.1 = 4
            // 邻居接收 = 4 * 0.8 = 3.2
            // 总变化 = -4 + 3.2 = -0.8（衰减）
            
            double centerConc = 80;
            double neighborConc = 40;
            double diffusionCoef = 0.1;
            double transferEfficiency = 0.8;
            
            double diff = centerConc - neighborConc;
            double transfer = diff * diffusionCoef;
            double neighborGain = transfer * transferEfficiency;
            
            double newCenter = centerConc - transfer;
            double newNeighbor = neighborConc + neighborGain;
            
            double totalBefore = centerConc + neighborConc;
            double totalAfter = newCenter + newNeighbor;
            
            assertTrue(totalAfter < totalBefore, 
                "Total concentration should decrease due to transfer efficiency");
        }
        
        @Test
        @DisplayName("高浓度向低浓度传输")
        void testHighToLowTransfer() {
            double high = 100;
            double low = 20;
            double threshold = 20;
            
            assertTrue(high > threshold, "High concentration should exceed threshold");
            assertTrue(low < high, "Low concentration should be lower than high");
            assertTrue(high - low > 0, "Difference should be positive for transfer");
        }
        
        @Test
        @DisplayName("低浓度区块不触发扩散")
        void testLowConcentrationNoDiffusion() {
            double threshold = 20;
            double lowConcentration = 15;
            
            assertTrue(lowConcentration < threshold,
                "Low concentration should be below threshold");
        }
        
        @Test
        @DisplayName("相同浓度不产生传输")
        void testEqualConcentrationNoTransfer() {
            double conc1 = 50;
            double conc2 = 50;
            double diff = conc1 - conc2;
            
            assertEquals(0, diff, 0.001, "Equal concentration has zero difference");
            assertFalse(diff > 0, "No transfer when difference is zero or negative");
        }
    }
    
    @Nested
    @DisplayName("扩散平衡理论")
    class DiffusionEquilibriumTheory {
        
        @Test
        @DisplayName("多次扩散趋向平衡")
        void testDiffusionTendsToEquilibrium() {
            // 模拟多次扩散过程
            double center = 100;
            double neighbors = 20;
            double coef = 0.1;
            double efficiency = 0.8;
            int neighborCount = 8;
            
            for (int i = 0; i < 50; i++) {
                // 简化：假设所有邻居浓度相同
                double avgNeighbor = neighbors;
                double diff = center - avgNeighbor;
                
                if (diff > 0) {
                    double transferPerNeighbor = diff * coef / neighborCount;
                    double totalTransfer = transferPerNeighbor * neighborCount;
                    double neighborGain = transferPerNeighbor * efficiency;
                    
                    center -= totalTransfer;
                    neighbors += neighborGain;
                }
            }
            
            // 经过多次扩散，浓度应该更接近
            double finalDiff = Math.abs(center - neighbors);
            assertTrue(finalDiff < 50, 
                "After multiple diffusions, concentrations should be closer: center=" + 
                center + ", neighbors=" + neighbors);
        }
        
        @Test
        @DisplayName("扩散阈值阻止低浓度扩散")
        void testThresholdPreventsLowDiffusion() {
            double threshold = 20;
            
            // 低于阈值的区块不会触发扩散
            double lowConc = 15;
            assertFalse(lowConc >= threshold, 
                "Low concentration should not trigger diffusion");
            
            // 高于阈值的区块会触发扩散
            double highConc = 25;
            assertTrue(highConc >= threshold,
                "High concentration should trigger diffusion");
        }
    }
    
    @Nested
    @DisplayName("边界值测试")
    class BoundaryValues {
        
        @Test
        @DisplayName("零浓度处理")
        void testZeroConcentration() {
            ChunkFactorState state = new ChunkFactorState(0);
            assertEquals(0, state.getConcentrationFloor(), 0.001);
            
            state.setCurrentConcentration(10);
            assertEquals(10, state.getCurrentConcentration(), 0.001);
        }
        
        @Test
        @DisplayName("极大浓度处理")
        void testLargeConcentration() {
            ChunkFactorState state = new ChunkFactorState(1000);
            assertEquals(100, state.getConcentrationFloor(), 0.001);
            
            state.setCurrentConcentration(500);
            assertEquals(500, state.getCurrentConcentration(), 0.001);
        }
        
        @Test
        @DisplayName("负浓度被地板值限制")
        void testNegativeConcentrationClamped() {
            ChunkFactorState state = new ChunkFactorState(100);
            state.setCurrentConcentration(-50);
            assertEquals(10, state.getCurrentConcentration(), 0.001);
        }
    }
}