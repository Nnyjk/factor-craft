package com.factorcraft.module.factor.state;

/**
 * docs/06.1: 维度实时 Factor。
 */
public record FactorWorldState(
        String dimensionKey,
        double currentFactor,
        double baseFactor,
        double dayAverage,
        double trend,
        int currentTier,
        long lastUpdatedTick
) {}
