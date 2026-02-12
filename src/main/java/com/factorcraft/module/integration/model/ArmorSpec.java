package com.factorcraft.module.integration.model;

import java.util.Set;

public record ArmorSpec(
        String contentId,
        String sourcePackId,
        String displayName,
        StandardTierWindow tierWindow,
        int conductivityCost,
        Set<String> tags,
        int helmetArmor,
        int chestArmor,
        int leggingsArmor,
        int bootsArmor,
        float knockbackResistance
) implements NonCoreContentSpec {
    @Override
    public NonCoreCategory category() {
        return NonCoreCategory.ARMOR;
    }
}
