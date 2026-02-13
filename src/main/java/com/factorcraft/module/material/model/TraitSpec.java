package com.factorcraft.module.material.model;

import java.util.Set;

/**
 * 词条配置规格。
 */
public record TraitSpec(
        String traitId,
        TraitCategory category,
        int minTier,
        int maxTier,
        double weight,
        Set<String> dimensions,
        String linkedStatusId,
        double durationMultiplier
) {}
