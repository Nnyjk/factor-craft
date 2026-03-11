package com.factorcraft.module.material.model;

import com.google.gson.annotations.SerializedName;

/**
 * 共振规则定义
 */
public record ResonanceRule(
    String type,
    int threshold,
    @SerializedName("effect_multiplier")
    double effectMultiplier,
    String description
) {}