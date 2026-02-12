package com.factorcraft.module.integration.model;

import java.util.Set;

public record FurnitureSpec(
        String contentId,
        String sourcePackId,
        String displayName,
        StandardTierWindow tierWindow,
        int conductivityCost,
        Set<String> tags,
        String zoneTag,
        int comfortBonus,
        int utilityBonus
) implements NonCoreContentSpec {
    @Override
    public NonCoreCategory category() {
        return NonCoreCategory.FURNITURE;
    }
}
