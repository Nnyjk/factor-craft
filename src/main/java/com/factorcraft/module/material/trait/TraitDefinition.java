package com.factorcraft.module.material.trait;

import com.factorcraft.module.material.model.TraitCategory;
import com.factorcraft.module.material.model.TraitEffect;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record TraitDefinition(
    String id,
    String name,
    String type,
    TraitCategory category,
    String description,
    List<TraitEffect> effects,
    int maxLevel,
    double levelScaling,
    Map<String, Object> resonance,
    Set<String> incompatible,
    double weight,
    int[] tierRange
) {
    public boolean isPositive() {
        return "positive".equals(type);
    }
    
    public boolean isCompatibleWith(String traitId) {
        return !incompatible.contains(traitId);
    }
    
    public double getEffectMultiplier(int level) {
        if (level <= 1) return 1.0;
        return 1.0 + (level - 1) * levelScaling;
    }
}