package com.factorcraft;

import com.factorcraft.module.material.model.*;
import com.factorcraft.module.factor.model.BiomeConcentration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class ConfigParserTest {
    
    @Test
    void testTraitCategory() {
        assertEquals("extraction", TraitCategory.EXTRACTION.getId());
        assertEquals("transfer", TraitCategory.TRANSFER.getId());
        assertEquals("production", TraitCategory.PRODUCTION.getId());
        assertEquals("environment", TraitCategory.ENVIRONMENT.getId());
        assertEquals("negative", TraitCategory.NEGATIVE.getId());
    }
    
    @Test
    void testTraitEffect() {
        TraitEffect.Condition condition = new TraitEffect.Condition(
            "minecraft:the_nether",
            null
        );
        
        TraitEffect effect = new TraitEffect(
            "extraction_speed",
            "MULTIPLY",
            0.15,
            condition
        );
        
        assertEquals("extraction_speed", effect.target());
        assertEquals("MULTIPLY", effect.operation());
        assertEquals(0.15, effect.value(), 0.01);
        assertNotNull(effect.condition());
        assertEquals("minecraft:the_nether", effect.condition().dimension());
        assertNull(effect.condition().concentrationBelow());
    }
    
    @Test
    void testTraitEffectWithoutCondition() {
        TraitEffect effect = new TraitEffect(
            "production_rate",
            "ADD",
            10.0,
            null
        );
        
        assertEquals("production_rate", effect.target());
        assertEquals("ADD", effect.operation());
        assertEquals(10.0, effect.value(), 0.01);
        assertNull(effect.condition());
    }
    
    @Test
    void testResonanceRule() {
        ResonanceRule rule = new ResonanceRule(
            "double_resonance",
            2,
            1.5,
            "Two same traits create resonance"
        );
        
        assertEquals("double_resonance", rule.type());
        assertEquals(2, rule.threshold());
        assertEquals(1.5, rule.effectMultiplier(), 0.01);
        assertEquals("Two same traits create resonance", rule.description());
    }
    
    @Test
    void testBiomeConcentration() {
        BiomeConcentration.ConcentrationRange range = 
            new BiomeConcentration.ConcentrationRange(40, 60);
        
        BiomeConcentration biome = new BiomeConcentration(
            "minecraft:overworld",
            1.0,
            range,
            Map.of("minecraft:plains", 10, "minecraft:desert", -5),
            Map.of("minecraft:village", 20)
        );
        
        assertEquals("minecraft:overworld", biome.dimension());
        assertEquals(1.0, biome.baselineMultiplier(), 0.01);
        assertEquals(40, biome.baseConcentration().min());
        assertEquals(60, biome.baseConcentration().max());
        assertEquals(10, biome.biomeModifiers().get("minecraft:plains"));
        assertEquals(20, biome.structureModifiers().get("minecraft:village"));
    }
    
    @Test
    void testTraitCombination() {
        TraitCombination combo = new TraitCombination(
            "steam_power",
            "Steam Power",
            List.of("fire_boost", "water_boost"),
            Map.of("steam_power", 0.8),
            "Combine fire and water to create steam",
            2
        );
        
        assertEquals("steam_power", combo.id());
        assertEquals("Steam Power", combo.name());
        assertEquals(2, combo.traits().size());
        assertEquals(0.8, combo.result().get("steam_power"), 0.01);
        assertEquals(2, combo.tierRequired());
    }
}