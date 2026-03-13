package com.factorcraft.module.material.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public record TraitInstance(
    String traitId,
    int level
) {
    public static final Codec<TraitInstance> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("id").forGetter(TraitInstance::traitId),
            Codec.INT.fieldOf("level").forGetter(TraitInstance::level)
        ).apply(instance, TraitInstance::new)
    );
    
    public TraitInstance(String traitId) {
        this(traitId, 1);
    }
    
    public TraitInstance withLevel(int newLevel) {
        TraitDefinition def = TraitRegistry.get(traitId).orElse(null);
        if (def == null) return this;
        int clampedLevel = Math.max(1, Math.min(newLevel, def.maxLevel()));
        return new TraitInstance(traitId, clampedLevel);
    }
    
    public Optional<TraitDefinition> getDefinition() {
        return TraitRegistry.get(traitId);
    }
    
    public boolean isValid() {
        return TraitRegistry.exists(traitId) && level >= 1;
    }
    
    public double getEffectMultiplier() {
        return getDefinition().map(def -> def.getEffectMultiplier(level)).orElse(1.0);
    }
}