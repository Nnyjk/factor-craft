package com.factorcraft.module.factor.model;

import java.util.Map;
import com.google.gson.annotations.SerializedName;

/**
 * 群系 Factor 浓度配置
 */
public record BiomeConcentration(
    String dimension,
    @SerializedName("baseline_multiplier")
    double baselineMultiplier,
    @SerializedName("base_concentration")
    ConcentrationRange baseConcentration,
    @SerializedName("biome_modifiers")
    Map<String, Integer> biomeModifiers,
    @SerializedName("structure_modifiers")
    Map<String, Integer> structureModifiers
) {
    public record ConcentrationRange(int min, int max) {}
}