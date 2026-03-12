package com.factorcraft.module.material.trait;

import java.util.*;
import java.util.stream.Collectors;

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
    
    /**
     * 移除物品上的指定特性
     */
    public static boolean removeTrait(net.minecraft.item.ItemStack stack, String traitId) {
        TraitData traitData = ITEM_TRAITS.get(stack);
        if (traitData == null || !traitData.hasTrait(traitId)) {
            return false;
        }
        ITEM_TRAITS.put(stack, traitData.removeTrait(traitId));
        return true;
    }
    
    /**
     * 清除物品上的所有特性
     */
    public static void clearTraits(net.minecraft.item.ItemStack stack) {
        ITEM_TRAITS.remove(stack);
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
    
    public static List<TraitInstance> generateRandomTraits(int tier, int count, net.minecraft.util.math.random.Random random, double positiveChance) {
        List<TraitInstance> result = new ArrayList<>();
        Set<String> added = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            boolean wantPositive = random.nextDouble() < positiveChance;
            
            List<TraitDefinition> candidates = TraitRegistry.getTraitsForTier(tier).stream()
                .filter(t -> !added.contains(t.id()))
                .filter(t -> wantPositive == t.isPositive())
                .filter(t -> added.stream().allMatch(addedId -> {
                    TraitDefinition addedDef = TraitRegistry.get(addedId).orElse(null);
                    return addedDef == null || (addedDef.isCompatibleWith(t.id()) && t.isCompatibleWith(addedId));
                }))
                .toList();
            
            if (!candidates.isEmpty()) {
                double totalWeight = candidates.stream().mapToDouble(TraitDefinition::weight).sum();
                double value = random.nextDouble() * totalWeight;
                double cumulative = 0.0;
                
                TraitDefinition selected = candidates.get(0);
                for (TraitDefinition t : candidates) {
                    cumulative += t.weight();
                    if (value <= cumulative) {
                        selected = t;
                        break;
                    }
                }
                
                result.add(new TraitInstance(selected.id(), 1));
                added.add(selected.id());
            }
        }
        
        return result;
    }
}

record TraitData(List<TraitInstance> traits) {
    static TraitData empty() { return new TraitData(Collections.emptyList()); }
    TraitData addTrait(TraitInstance t) {
        List<TraitInstance> n = new ArrayList<>(traits);
        n.add(t);
        return new TraitData(Collections.unmodifiableList(n));
    }
    TraitData removeTrait(String id) {
        return new TraitData(traits.stream().filter(t -> !t.traitId().equals(id)).toList());
    }
    boolean hasTrait(String id) { return traits.stream().anyMatch(t -> t.traitId().equals(id)); }
    int getTraitLevel(String id) {
        return traits.stream().filter(t -> t.traitId().equals(id)).findFirst().map(TraitInstance::level).orElse(0);
    }
}