package com.factorcraft.module.material.model;

/**
 * 附魔配置规格。
 */
public record EnchantSpec(
        String enchantId,
        EnchantCategory category,
        int minTier,
        int maxTier,
        double potency
) {}
