package com.factorcraft.module.creature.model;

/**
 * 掉落条目标准。
 */
public record LootEntrySpec(
        String itemId,
        double chance,
        int minCount,
        int maxCount,
        String conditionTag
) {
    public LootEntrySpec {
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("chance must be in [0,1]");
        }
        if (minCount <= 0 || maxCount < minCount) {
            throw new IllegalArgumentException("invalid drop count range");
        }
    }
}
