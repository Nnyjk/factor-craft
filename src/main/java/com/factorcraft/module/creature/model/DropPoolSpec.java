package com.factorcraft.module.creature.model;

import java.util.List;

/**
 * 怪物凋落物池标准。
 */
public record DropPoolSpec(
        String poolId,
        String entityId,
        int minTier,
        int maxTier,
        List<LootEntrySpec> entries
) {
    public DropPoolSpec {
        if (minTier < 0 || maxTier < minTier) {
            throw new IllegalArgumentException("invalid tier range");
        }
        if (entries == null || entries.isEmpty()) {
            throw new IllegalArgumentException("entries must not be empty");
        }
    }
}
