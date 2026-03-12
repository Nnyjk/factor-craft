package com.factorcraft.module.factor.state;

/**
 * 区块 Factor 状态
 */
public class ChunkFactorState {
    private final double initialConcentration;
    private double currentConcentration;
    private long lastUpdatedTick;
    private boolean anchored;
    private int anchorRadius;
    
    public ChunkFactorState(double initialConcentration) {
        this.initialConcentration = initialConcentration;
        this.currentConcentration = initialConcentration;
        this.lastUpdatedTick = 0;
        this.anchored = false;
        this.anchorRadius = 0;
    }
    
    public double getConcentrationFloor() {
        return initialConcentration * 0.1;
    }
    
    // Getters and setters
    public double getInitialConcentration() { return initialConcentration; }
    public double getCurrentConcentration() { return currentConcentration; }
    public void setCurrentConcentration(double value) { 
        this.currentConcentration = Math.max(getConcentrationFloor(), value);
    }
    public long getLastUpdatedTick() { return lastUpdatedTick; }
    public void setLastUpdatedTick(long tick) { this.lastUpdatedTick = tick; }
    public boolean isAnchored() { return anchored; }
    public void setAnchored(boolean anchored) { this.anchored = anchored; }
    public int getAnchorRadius() { return anchorRadius; }
    public void setAnchorRadius(int radius) { this.anchorRadius = radius; }
}