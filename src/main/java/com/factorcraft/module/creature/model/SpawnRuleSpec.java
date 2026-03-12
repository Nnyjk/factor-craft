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
        // Issue #7: 验证 dimensions 非 null
        if (dimensions == null) {
            dimensions = Set.of(); // 默认空集合
        }
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
