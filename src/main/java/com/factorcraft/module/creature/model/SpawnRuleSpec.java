package com.factorcraft.module.creature.model;

import java.util.Set;

/**
 * 怪物刷新规则标准。
 */
public record SpawnRuleSpec(
        String ruleId,
        String entityId,
        Set<String> dimensions,
        int minTier,
        int maxTier,
        int weight,
        int minGroupSize,
        int maxGroupSize
) {
    public SpawnRuleSpec {
        if (minTier < 0 || maxTier < minTier) {
            throw new IllegalArgumentException("invalid tier range");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be > 0");
        }
        if (minGroupSize <= 0 || maxGroupSize < minGroupSize) {
            throw new IllegalArgumentException("invalid group size");
        }
    }
}
