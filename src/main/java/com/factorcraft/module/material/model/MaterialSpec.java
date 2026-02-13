package com.factorcraft.module.material.model;

import java.util.Set;

/**
 * 材料配置规格。
 */
public record MaterialSpec(
        String materialId,
        MaterialLevel level,
        int harvestTier,
        double toughness,
        double factorConductivity,
        int traitSlots,
        Set<String> dimensions
) {}
