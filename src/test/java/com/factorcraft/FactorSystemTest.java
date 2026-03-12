package com.factorcraft;

import com.factorcraft.module.factor.state.ChunkFactorState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FactorSystemTest {
    
    @Test
    void testChunkFactorStateCreation() {
        ChunkFactorState state = new ChunkFactorState(50.0);
        assertEquals(50.0, state.getCurrentConcentration(), 0.01);
        assertEquals(50.0, state.getInitialConcentration(), 0.01);
        assertFalse(state.isAnchored());
        assertEquals(0, state.getAnchorRadius());
        assertEquals(0, state.getLastUpdatedTick());
    }
    
    @Test
    void testAnchorState() {
        ChunkFactorState state = new ChunkFactorState(50.0);
        state.setAnchored(true);
        state.setAnchorRadius(5);
        
        assertTrue(state.isAnchored());
        assertEquals(5, state.getAnchorRadius());
    }
    
    @Test
    void testConcentrationModification() {
        ChunkFactorState state = new ChunkFactorState(100.0);
        
        state.setCurrentConcentration(80.0);
        assertEquals(80.0, state.getCurrentConcentration(), 0.01);
        
        state.setCurrentConcentration(90.0);
        assertEquals(90.0, state.getCurrentConcentration(), 0.01);
    }
    
    @Test
    void testConcentrationFloor() {
        ChunkFactorState state = new ChunkFactorState(100.0);
        
        // 浓度不能低于初始值的 10%
        double floor = state.getConcentrationFloor();
        assertEquals(10.0, floor, 0.01);
        
        state.setCurrentConcentration(5.0);
        assertEquals(floor, state.getCurrentConcentration(), 0.01);
    }
    
    @Test
    void testLastUpdatedTick() {
        ChunkFactorState state = new ChunkFactorState(50.0);
        
        state.setLastUpdatedTick(100);
        assertEquals(100, state.getLastUpdatedTick());
    }
}