package com.factorcraft.module.factor.state;

import java.util.Map;

/**
 * docs/06.1: 区块污染强度。
 */
public record PollutionHeatmap(Map<Long, Double> chunkIntensity) {}
