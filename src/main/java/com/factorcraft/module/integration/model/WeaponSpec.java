package com.factorcraft.module.integration.model;

import java.util.Set;

public record WeaponSpec(
        String contentId,
        String sourcePackId,
        String displayName,
        StandardTierWindow tierWindow,
        int conductivityCost,
        Set<String> tags,
        float attackDamage,
        float attackSpeed,
        float armorPierce
) implements NonCoreContentSpec {
    @Override
    public NonCoreCategory category() {
        return NonCoreCategory.WEAPON;
    }
}
