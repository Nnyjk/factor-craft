package com.factorcraft.module.material.model;

/**
 * 状态池配置规格。
 */
public record StatusEffectSpec(
        String statusId,
        StatusType type,
        int baseDurationSeconds,
        int maxStacks,
        StatusStackRule stackRule,
        int minTier,
        int maxTier
) {}
