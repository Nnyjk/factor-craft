package com.factorcraft.module.material.trait;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.*;

public final class TraitHelper {
    private TraitHelper() {}
    
    public static boolean hasTraits(ItemStack stack) {
        return !TraitService.getTraits(stack).isEmpty();
    }
    
    public static int getTraitCount(ItemStack stack) {
        return TraitService.getTraits(stack).size();
    }
    
    public static Map<String, Double> calculateTotalEffects(ItemStack stack, World world, BlockPos pos) {
        Map<String, Double> effects = new HashMap<>();
        List<TraitInstance> traits = TraitService.getTraits(stack);
        double resonanceBonus = TraitService.calculateResonanceBonus(traits);
        
        for (TraitInstance instance : traits) {
            Optional<TraitDefinition> defOpt = instance.getDefinition();
            if (defOpt.isEmpty()) continue;
            
            TraitDefinition def = defOpt.get();
            double levelMultiplier = instance.getEffectMultiplier();
            
            for (com.factorcraft.module.material.model.TraitEffect effect : def.effects()) {
                if (effect.getCondition().isPresent()) {
                    var condition = effect.getCondition().get();
                    if (condition.getDimension().isPresent()) {
                        String required = condition.getDimension().get();
                        String current = world.getRegistryKey().getValue().toString();
                        if (!required.equals(current)) continue;
                    }
                }
                
                double finalValue = effect.value() * levelMultiplier * resonanceBonus;
                String target = effect.target();
                
                if ("multiply".equals(effect.operation())) {
                    effects.merge(target, finalValue, (old, newVal) -> old * (1 + newVal));
                } else {
                    effects.merge(target, finalValue, Double::sum);
                }
            }
        }
        return effects;
    }
    
    public static double getEffectValue(ItemStack stack, String target, World world, BlockPos pos) {
        return calculateTotalEffects(stack, world, pos).getOrDefault(target, 0.0);
    }
}