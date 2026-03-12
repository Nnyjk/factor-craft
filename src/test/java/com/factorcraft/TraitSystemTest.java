package com.factorcraft;

import com.factorcraft.module.material.model.*;
import com.factorcraft.module.material.trait.*;
import com.factorcraft.module.material.component.TraitData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TraitSystemTest {
    
    @BeforeEach
    void setup() {
        TraitRegistry.clear();
    }
    
    @Test
    void testTraitRegistration() {
        TraitDefinition def = new TraitDefinition(
            "test_trait",
            "Test Trait",
            "positive",
            TraitCategory.EXTRACTION,
            "A test trait for extraction",
            List.of(new TraitEffect("extraction_speed", "MULTIPLY", 0.1, null)),
            3,
            1.0,
            Map.of("threshold", 2, "multiplier", 1.5),
            Set.of("negative_trait"),
            1.0,
            new int[]{1, 3}
        );
        
        TraitRegistry.register(def);
        
        Optional<TraitDefinition> found = TraitRegistry.get("test_trait");
        assertTrue(found.isPresent());
        assertEquals("Test Trait", found.get().name());
        assertTrue(found.get().isPositive());
        assertEquals(3, found.get().maxLevel());
    }
    
    @Test
    void testTraitInstance() {
        TraitInstance instance = new TraitInstance("test_trait", 2);
        assertEquals("test_trait", instance.traitId());
        assertEquals(2, instance.level());
    }
    
    @Test
    void testTraitData() {
        TraitData data = TraitData.empty();
        
        TraitData data1 = data.addTrait(new TraitInstance("trait1", 1));
        TraitData data2 = data1.addTrait(new TraitInstance("trait2", 2));
        
        assertEquals(1, data2.getTraitLevel("trait1"));
        assertEquals(2, data2.getTraitLevel("trait2"));
        assertEquals(0, data2.getTraitLevel("non_existent"));
        
        assertEquals(2, data2.traitCount());
        assertTrue(data2.hasTrait("trait1"));
        assertTrue(data2.hasTrait("trait2"));
        assertFalse(data2.hasTrait("non_existent"));
    }
    
    @Test
    void testResonanceBonus() {
        List<TraitInstance> traits = List.of(
            new TraitInstance("t1", 1),
            new TraitInstance("t1", 1),
            new TraitInstance("t2", 1)
        );
        
        double bonus = TraitService.calculateResonanceBonus(traits);
        assertEquals(1.5, bonus, 0.01);
    }
    
    @Test
    void testTripleResonance() {
        List<TraitInstance> traits = List.of(
            new TraitInstance("t1", 1),
            new TraitInstance("t1", 1),
            new TraitInstance("t1", 1)
        );
        
        double bonus = TraitService.calculateResonanceBonus(traits);
        assertEquals(2.5, bonus, 0.01);
    }
    
    @Test
    void testNoResonance() {
        List<TraitInstance> traits = List.of(
            new TraitInstance("t1", 1),
            new TraitInstance("t2", 1),
            new TraitInstance("t3", 1)
        );
        
        double bonus = TraitService.calculateResonanceBonus(traits);
        assertEquals(1.0, bonus, 0.01);
    }
    
    @Test
    void testTraitCategory() {
        assertEquals("extraction", TraitCategory.EXTRACTION.getId());
        assertEquals("transfer", TraitCategory.TRANSFER.getId());
        assertEquals("production", TraitCategory.PRODUCTION.getId());
        assertEquals("environment", TraitCategory.ENVIRONMENT.getId());
        assertEquals("negative", TraitCategory.NEGATIVE.getId());
    }
}