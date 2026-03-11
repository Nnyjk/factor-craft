package com.factorcraft.module.material.component;

import com.factorcraft.module.material.trait.TraitInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;

public record TraitData(
    List<TraitInstance> traits
) {
    public static final Codec<TraitData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            TraitInstance.CODEC.listOf().fieldOf("traits").forGetter(TraitData::traits)
        ).apply(instance, TraitData::new)
    );
    
    public static TraitData empty() {
        return new TraitData(Collections.emptyList());
    }
    
    public static TraitData of(TraitInstance... traits) {
        return new TraitData(List.of(traits));
    }
    
    public TraitData addTrait(TraitInstance trait) {
        List<TraitInstance> newTraits = new ArrayList<>(this.traits);
        newTraits.add(trait);
        return new TraitData(Collections.unmodifiableList(newTraits));
    }
    
    public TraitData removeTrait(String traitId) {
        List<TraitInstance> newTraits = traits.stream()
            .filter(t -> !t.traitId().equals(traitId))
            .toList();
        return new TraitData(newTraits);
    }
    
    public TraitData updateTrait(String traitId, int newLevel) {
        List<TraitInstance> newTraits = new ArrayList<>();
        for (TraitInstance trait : traits) {
            if (trait.traitId().equals(traitId)) {
                newTraits.add(trait.withLevel(newLevel));
            } else {
                newTraits.add(trait);
            }
        }
        return new TraitData(newTraits);
    }
    
    public boolean hasTrait(String traitId) {
        return traits.stream().anyMatch(t -> t.traitId().equals(traitId));
    }
    
    public int getTraitLevel(String traitId) {
        return traits.stream()
            .filter(t -> t.traitId().equals(traitId))
            .findFirst()
            .map(TraitInstance::level)
            .orElse(0);
    }
    
    public int traitCount() {
        return traits.size();
    }
    
    public boolean isEmpty() {
        return traits.isEmpty();
    }
}