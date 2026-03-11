package com.factorcraft.module.material.model;

import java.util.List;
import java.util.Map;
import com.google.gson.annotations.SerializedName;

/**
 * 特性组合配方定义
 */
public record TraitCombination(
    String id,
    String name,
    List<String> traits,
    Map<String, Double> result,
    String description,
    @SerializedName("tier_required")
    int tierRequired
) {}