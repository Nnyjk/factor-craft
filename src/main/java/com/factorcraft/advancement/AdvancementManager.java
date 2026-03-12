package com.factorcraft.advancement;

import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitService;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Factor Craft 成就系统
 * TODO: 完整实现成就触发机制
 */
public class AdvancementManager {
    
    // 成就 ID
    public static final Identifier ROOT = Identifier.of("factorcraft", "root");
    public static final Identifier FIRST_EXTRACTION = Identifier.of("factorcraft", "first_extraction");
    public static final Identifier TRAIT_MASTER = Identifier.of("factorcraft", "trait_master");
    public static final Identifier RESONANCE_DISCOVERER = Identifier.of("factorcraft", "resonance_discoverer");
    public static final Identifier HIGH_ENERGY_ZONE = Identifier.of("factorcraft", "high_energy_zone");
    public static final Identifier CULTIVATION_EXPERT = Identifier.of("factorcraft", "cultivation_expert");
    
    /**
     * 触发成就
     */
    public static void grantAdvancement(ServerPlayerEntity player, Identifier advancementId) {
        // TODO: 实现成就触发
        System.out.println("[FactorCraft] Advancement triggered: " + advancementId);
    }
    
    /**
     * 检查并触发 Factor 相关成就
     */
    public static void checkFactorAdvancements(ServerPlayerEntity player, double concentration) {
        if (concentration >= 100) {
            grantAdvancement(player, HIGH_ENERGY_ZONE);
        }
    }
    
    /**
     * 检查并触发特性相关成就
     */
    public static void checkTraitAdvancements(ServerPlayerEntity player, ItemStack stack) {
        List<TraitInstance> traits = TraitService.getTraits(stack);
        
        if (!traits.isEmpty()) {
            grantAdvancement(player, FIRST_EXTRACTION);
        }
        
        if (traits.size() >= 5) {
            grantAdvancement(player, TRAIT_MASTER);
        }
        
        // 检查共振
        double resonance = TraitService.calculateResonanceBonus(traits);
        if (resonance >= 2.5) {
            grantAdvancement(player, RESONANCE_DISCOVERER);
        }
    }
}