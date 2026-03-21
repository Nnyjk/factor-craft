package com.factorcraft.factor.synthesis;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Factor 合成配方注册器测试
 */
class FactorSynthesisRegistryTest {
    
    private FactorSynthesisRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = FactorSynthesisRegistry.getInstance();
        registry.clear();
    }
    
    @Test
    void testRegisterRecipe() {
        FactorSynthesisRecipe recipe = createTestRecipe("test_recipe");
        
        registry.register(recipe);
        
        assertTrue(registry.hasRecipe(Identifier.of("test", "test_recipe")));
        assertEquals(1, registry.getRecipeCount());
    }
    
    @Test
    void testGetRecipe() {
        FactorSynthesisRecipe recipe = createTestRecipe("test_recipe");
        registry.register(recipe);
        
        Optional<FactorSynthesisRecipe> retrieved = registry.getRecipe(Identifier.of("test", "test_recipe"));
        
        assertTrue(retrieved.isPresent());
        assertEquals(recipe.getId(), retrieved.get().getId());
    }
    
    @Test
    void testGetNonExistentRecipe() {
        Optional<FactorSynthesisRecipe> retrieved = registry.getRecipe(Identifier.of("test", "nonexistent"));
        
        assertFalse(retrieved.isPresent());
    }
    
    @Test
    void testUnregisterRecipe() {
        FactorSynthesisRecipe recipe = createTestRecipe("test_recipe");
        registry.register(recipe);
        
        registry.unregister(Identifier.of("test", "test_recipe"));
        
        assertFalse(registry.hasRecipe(Identifier.of("test", "test_recipe")));
        assertEquals(0, registry.getRecipeCount());
    }
    
    @Test
    void testClearRecipes() {
        registry.register(createTestRecipe("recipe1"));
        registry.register(createTestRecipe("recipe2"));
        registry.register(createTestRecipe("recipe3"));
        
        registry.clear();
        
        assertEquals(0, registry.getRecipeCount());
    }
    
    @Test
    void testGetRecipesForOutput() {
        FactorSynthesisRecipe recipe1 = FactorSynthesisRecipe.builder(Identifier.of("test", "recipe1"))
            .addInput(new FactorIngredient(Identifier.of("test", "input_a")))
            .output(new FactorIngredient(Identifier.of("test", "output_x")))
            .build();
        
        FactorSynthesisRecipe recipe2 = FactorSynthesisRecipe.builder(Identifier.of("test", "recipe2"))
            .addInput(new FactorIngredient(Identifier.of("test", "input_b")))
            .output(new FactorIngredient(Identifier.of("test", "output_x")))
            .build();
        
        FactorSynthesisRecipe recipe3 = FactorSynthesisRecipe.builder(Identifier.of("test", "recipe3"))
            .addInput(new FactorIngredient(Identifier.of("test", "input_c")))
            .output(new FactorIngredient(Identifier.of("test", "output_y")))
            .build();
        
        registry.register(recipe1);
        registry.register(recipe2);
        registry.register(recipe3);
        
        List<FactorSynthesisRecipe> recipesForX = registry.getRecipesForOutput(Identifier.of("test", "output_x"));
        
        assertEquals(2, recipesForX.size());
        
        List<FactorSynthesisRecipe> recipesForY = registry.getRecipesForOutput(Identifier.of("test", "output_y"));
        
        assertEquals(1, recipesForY.size());
    }
    
    @Test
    void testOverwriteRecipe() {
        FactorSynthesisRecipe recipe1 = FactorSynthesisRecipe.builder(Identifier.of("test", "same_id"))
            .addInput(new FactorIngredient(Identifier.of("test", "input1")))
            .output(new FactorIngredient(Identifier.of("test", "output1")))
            .successRate(0.5)
            .build();
        
        FactorSynthesisRecipe recipe2 = FactorSynthesisRecipe.builder(Identifier.of("test", "same_id"))
            .addInput(new FactorIngredient(Identifier.of("test", "input2")))
            .output(new FactorIngredient(Identifier.of("test", "output2")))
            .successRate(0.9)
            .build();
        
        registry.register(recipe1);
        registry.register(recipe2);
        
        // 应该被覆盖
        assertEquals(1, registry.getRecipeCount());
        
        Optional<FactorSynthesisRecipe> retrieved = registry.getRecipe(Identifier.of("test", "same_id"));
        assertTrue(retrieved.isPresent());
        assertEquals(0.9, retrieved.get().getSuccessRate(), 0.001);
    }
    
    @Test
    void testGetAllRecipes() {
        registry.register(createTestRecipe("recipe1"));
        registry.register(createTestRecipe("recipe2"));
        
        assertEquals(2, registry.getAllRecipes().size());
    }
    
    @Test
    void testGetAllRecipeIds() {
        registry.register(createTestRecipe("recipe1"));
        registry.register(createTestRecipe("recipe2"));
        
        assertTrue(registry.getAllRecipeIds().contains(Identifier.of("test", "recipe1")));
        assertTrue(registry.getAllRecipeIds().contains(Identifier.of("test", "recipe2")));
    }
    
    // ========== 辅助方法 ==========
    
    private FactorSynthesisRecipe createTestRecipe(String name) {
        return FactorSynthesisRecipe.builder(Identifier.of("test", name))
            .addInput(new FactorIngredient(Identifier.of("test", "input")))
            .output(new FactorIngredient(Identifier.of("test", "output")))
            .build();
    }
}