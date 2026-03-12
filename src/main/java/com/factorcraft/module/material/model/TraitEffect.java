package com.factorcraft.module.material.model;

import com.google.gson.annotations.SerializedName;
import java.util.Optional;

/**
 * 特性效果定义
 */
public record TraitEffect(
    String target,
    String operation,
    double value,
    Condition condition
) {
    public record Condition(
        String dimension,
        @SerializedName("concentration_below")
        Double concentrationBelow
    ) {
        public Optional<String> getDimension() {
            return Optional.ofNullable(dimension);
        }
        
        public Optional<Double> getConcentrationBelow() {
            return Optional.ofNullable(concentrationBelow);
        }
    }
    
    public Optional<Condition> getCondition() {
        return Optional.ofNullable(condition);
    }
}