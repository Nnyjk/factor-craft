package com.factorcraft.module.creature.model;

import java.util.Set;

/**
 * 怪物阶段能力标准。
 */
public record CreatureAbilitySpec(
        String abilityId,
        String entityId,
        int minTier,
        int maxTier,
        Set<String> effects,
        int cooldownTicks
) {
    public CreatureAbilitySpec {
        if (minTier < 0 || maxTier < minTier) {
            throw new IllegalArgumentException("invalid tier range");
        }
        if (cooldownTicks < 0) {
            throw new IllegalArgumentException("cooldownTicks must be >= 0");
        }
    }
}
