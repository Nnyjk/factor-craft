package com.factorcraft.module.integration.model;

import java.util.Set;

public record ToolSpec(
        String contentId,
        String sourcePackId,
        String displayName,
        StandardTierWindow tierWindow,
        int conductivityCost,
        Set<String> tags,
        float harvestSpeed,
        int durability,
        int harvestTier
) implements NonCoreContentSpec {
    @Override
    public NonCoreCategory category() {
        return NonCoreCategory.TOOL;
    }
}
