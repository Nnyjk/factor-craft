package com.factorcraft.module.material.trait;

import java.util.*;

public class TraitService {
    private static final Map<net.minecraft.item.ItemStack, TraitData> ITEM_TRAITS = new WeakHashMap<>();
    
    public static boolean canAddTrait(net.minecraft.item.ItemStack stack, String traitId) {
        Optional<TraitDefinition> traitOpt = TraitRegistry.get(traitId);
        if (traitOpt.isEmpty()) return false;
        
        TraitDefinition trait = traitOpt.get();
        TraitData traitData = ITEM_TRAITS.get(stack);
        if (traitData == null) return true;
        
        if (traitData.hasTrait(traitId)) return false;
        
        for (TraitInstance existing : traitData.traits()) {
            Optional<TraitDefinition> existingDef = existing.getDefinition();
            if (existingDef.isPresent() && !existingDef.get().isCompatibleWith(traitId)) {
                return false;
            }
            if (!trait.isCompatibleWith(existing.traitId())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean addTrait(net.minecraft.item.ItemStack stack, String traitId, int level) {
        if (!canAddTrait(stack, traitId)) return false;
        
        TraitDefinition trait = TraitRegistry.get(traitId).orElseThrow();
        int clampedLevel = Math.max(1, Math.min(level, trait.maxLevel()));
        
        TraitData currentData = ITEM_TRAITS.getOrDefault(stack, TraitData.empty());
        TraitInstance instance = new TraitInstance(traitId, clampedLevel);
        ITEM_TRAITS.put(stack, currentData.addTrait(instance));
        return true;
    }
    
    public static List<TraitInstance> getTraits(net.minecraft.item.ItemStack stack) {
        TraitData traitData = ITEM_TRAITS.get(stack);
        return traitData != null ? traitData.traits() : Collections.emptyList();
    }
    
    public static int getTraitLevel(net.minecraft.item.ItemStack stack, String traitId) {
        TraitData traitData = ITEM_TRAITS.get(stack);
        return traitData != null ? traitData.getTraitLevel(traitId) : 0;
    }
    
    public static double calculateResonanceBonus(List<TraitInstance> traits) {
        Map<String, Long> counts = new HashMap<>();
        for (TraitInstance t : traits) {
            counts.merge(t.traitId(), 1L, Long::sum);
        }
        double bonus = 1.0;
        for (long count : counts.values()) {
            if (count >= 2) bonus *= 1.5;
            if (count >= 3) bonus *= (2.5 / 1.5);
        }
        return bonus;
    }
}

record TraitData(List<TraitInstance> traits) {
    static TraitData empty() { return new TraitData(Collections.emptyList()); }
    TraitData addTrait(TraitInstance t) {
        List<TraitInstance> n = new ArrayList<>(traits);
        n.add(t);
        return new TraitData(Collections.unmodifiableList(n));
    }
    boolean hasTrait(String id) { return traits.stream().anyMatch(t -> t.traitId().equals(id)); }
    int getTraitLevel(String id) {
        return traits.stream().filter(t -> t.traitId().equals(id)).findFirst().map(TraitInstance::level).orElse(0);
    }
}