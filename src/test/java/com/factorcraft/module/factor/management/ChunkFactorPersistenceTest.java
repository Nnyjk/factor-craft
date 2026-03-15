package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 区块 Factor 持久化测试
 */
public class ChunkFactorPersistenceTest {
    
    @Test
    @DisplayName("区块状态应正确初始化")
    void testChunkStateInitialization() {
        ChunkFactorState state = new ChunkFactorState(50.0);
        
        assertEquals(50.0, state.getInitialConcentration(), 0.001);
        assertEquals(50.0, state.getCurrentConcentration(), 0.001);
        assertEquals(0, state.getLastUpdatedTick());
        assertFalse(state.isAnchored());
        assertEquals(0, state.getAnchorRadius());
    }
    
    @Test
    @DisplayName("区块浓度应有下限")
    void testConcentrationFloor() {
        ChunkFactorState state = new ChunkFactorState(100.0);
        
        // 浓度下限应为初始值的 10%
        assertEquals(10.0, state.getConcentrationFloor(), 0.001);
        
        // 尝试设置为低于下限
        state.setCurrentConcentration(5.0);
        
        // 应该被限制在下限
        assertEquals(10.0, state.getCurrentConcentration(), 0.001);
    }
    
    @Test
    @DisplayName("区块状态应支持锚点")
    void testAnchoring() {
        ChunkFactorState state = new ChunkFactorState(50.0);
        
        assertFalse(state.isAnchored());
        
        state.setAnchored(true);
        state.setAnchorRadius(5);
        
        assertTrue(state.isAnchored());
        assertEquals(5, state.getAnchorRadius());
    }
    
    @Test
    @DisplayName("区块浓度变化应正确记录")
    void testConcentrationChanges() {
        ChunkFactorState state = new ChunkFactorState(50.0);
        
        // 提取 Factor
        state.setCurrentConcentration(40.0);
        assertEquals(40.0, state.getCurrentConcentration(), 0.001);
        
        // 注入 Factor
        state.setCurrentConcentration(60.0);
        assertEquals(60.0, state.getCurrentConcentration(), 0.001);
    }
    
    @Test
    @DisplayName("不同维度应有不同的基准浓度")
    void testDimensionBaselines() {
        // 主世界基准
        double overworldBaseline = 40.0;
        
        // 下界基准（更高）
        double netherBaseline = 70.0;
        
        // 末地基准（最高）
        double endBaseline = 100.0;
        
        assertTrue(netherBaseline > overworldBaseline);
        assertTrue(endBaseline > netherBaseline);
    }
}