package com.factorcraft.factor.synthesis;

import com.factorcraft.factor.Factor;
import com.factorcraft.factor.FactorRarity;
import com.factorcraft.factor.FactorType;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Factor 合成系统测试
 */
class FactorSynthesisTest {
    
    private Factor testFactor1;
    private Factor testFactor2;
    private FactorSynthesisRecipe testRecipe;
    
    @BeforeEach
    void setUp() {
        // 创建测试 Factor
        testFactor1 = new Factor.Builder(
            Identifier.of("test", "fire_essence"),
            "火焰精华"
        )
            .type(FactorType.FIRE)
            .rarity(FactorRarity.COMMON)
            .level(10)
            .tier(1)
            .basePower(50.0)
            .build();
        
        testFactor2 = new Factor.Builder(
            Identifier.of("test", "water_essence"),
            "水之精华"
        )
            .type(FactorType.WATER)
            .rarity(FactorRarity.COMMON)
            .level(10)
            .tier(1)
            .basePower(50.0)
            .build();
        
        // 创建测试配方
        testRecipe = FactorSynthesisRecipe.builder(Identifier.of("test", "fusion_recipe"))
            .addInput(new FactorIngredient(Identifier.of("test", "fire_essence"), 2))
            .addInput(new FactorIngredient(Identifier.of("test", "water_essence"), 1))
            .output(new FactorIngredient(Identifier.of("test", "fused_essence"), 1))
            .successRate(0.8)
            .failureBehavior(FactorSynthesisRecipe.FailureBehavior.RETURN_ALL)
            .synthesisTime(100)
            .build();
    }
    
    // ========== FactorIngredient 测试 ==========
    
    @Test
    void testIngredientMatches() {
        FactorIngredient ingredient = new FactorIngredient(
            Identifier.of("test", "fire_essence"),
            java.util.Optional.of(5),    // min level
            java.util.Optional.of(20),   // max level
            java.util.Optional.empty(),
            java.util.Optional.empty(),
            1
        );
        
        // 应该匹配（level 10 在 5-20 范围内）
        assertTrue(ingredient.matches(testFactor1));
        
        // 不应该匹配（ID 不同）
        assertFalse(ingredient.matches(testFactor2));
    }
    
    @Test
    void testIngredientBuilder() {
        FactorIngredient ingredient = FactorIngredient.builder(Identifier.of("test", "fire_essence"))
            .levelRange(5, 20)
            .tierRange(1, 3)
            .count(2)
            .build();
        
        assertEquals(Identifier.of("test", "fire_essence"), ingredient.getFactorId());
        assertEquals(2, ingredient.getCount());
        assertTrue(ingredient.getMinLevel().isPresent());
        assertEquals(5, ingredient.getMinLevel().get());
    }
    
    // ========== FactorSynthesisRecipe 测试 ==========
    
    @Test
    void testRecipeBuilder() {
        assertEquals(Identifier.of("test", "fusion_recipe"), testRecipe.getId());
        assertEquals(2, testRecipe.getInputs().size());
        assertEquals(Identifier.of("test", "fused_essence"), testRecipe.getOutputFactorId());
        assertEquals(0.8, testRecipe.getSuccessRate(), 0.001);
        assertEquals(FactorSynthesisRecipe.FailureBehavior.RETURN_ALL, testRecipe.getFailureBehavior());
        assertEquals(100, testRecipe.getSynthesisTime());
    }
    
    @Test
    void testRecipeDefaults() {
        FactorSynthesisRecipe simpleRecipe = new FactorSynthesisRecipe(
            Identifier.of("test", "simple"),
            List.of(new FactorIngredient(Identifier.of("test", "input"))),
            new FactorIngredient(Identifier.of("test", "output"))
        );
        
        assertEquals(1.0, simpleRecipe.getSuccessRate(), 0.001);
        assertEquals(FactorSynthesisRecipe.FailureBehavior.DESTROY, simpleRecipe.getFailureBehavior());
        assertEquals(200, simpleRecipe.getSynthesisTime());
    }
    
    @Test
    void testRecipeBuilderValidation() {
        // 没有输出应该抛出异常
        assertThrows(IllegalStateException.class, () -> {
            FactorSynthesisRecipe.builder(Identifier.of("test", "invalid"))
                .addInput(new FactorIngredient(Identifier.of("test", "input")))
                .build();
        });
        
        // 没有输入应该抛出异常
        assertThrows(IllegalStateException.class, () -> {
            FactorSynthesisRecipe.builder(Identifier.of("test", "invalid"))
                .output(new FactorIngredient(Identifier.of("test", "output")))
                .build();
        });
    }
    
    // ========== FactorSynthesizer 测试 ==========
    
    @Test
    void testValidateInputsNullRecipe() {
        List<Factor> inputs = List.of(testFactor1);
        FactorSynthesizer.SynthesisOutput result = FactorSynthesizer.validateInputs(null, inputs);
        
        assertEquals(FactorSynthesizer.SynthesisResult.INVALID_RECIPE, result.getResult());
    }
    
    @Test
    void testValidateInputsEmptyInputs() {
        FactorSynthesizer.SynthesisOutput result = FactorSynthesizer.validateInputs(testRecipe, new ArrayList<>());
        
        assertEquals(FactorSynthesizer.SynthesisResult.MISSING_INPUT, result.getResult());
    }
    
    @Test
    void testValidateInputsMissingInput() {
        // 只提供一个输入，但配方需要 2 个 fire_essence + 1 个 ice_essence
        List<Factor> inputs = List.of(testFactor1);
        FactorSynthesizer.SynthesisOutput result = FactorSynthesizer.validateInputs(testRecipe, inputs);
        
        assertEquals(FactorSynthesizer.SynthesisResult.INVALID_INPUT, result.getResult());
    }
    
    @Test
    void testSuccessRateClamping() {
        // 成功率应该被限制在 0-1 范围内
        FactorSynthesisRecipe highRate = FactorSynthesisRecipe.builder(Identifier.of("test", "high_rate"))
            .addInput(new FactorIngredient(Identifier.of("test", "input")))
            .output(new FactorIngredient(Identifier.of("test", "output")))
            .successRate(2.0) // 超过 1.0
            .build();
        
        assertTrue(highRate.getSuccessRate() <= 1.0);
        
        FactorSynthesisRecipe lowRate = FactorSynthesisRecipe.builder(Identifier.of("test", "low_rate"))
            .addInput(new FactorIngredient(Identifier.of("test", "input")))
            .output(new FactorIngredient(Identifier.of("test", "output")))
            .successRate(-0.5) // 小于 0
            .build();
        
        assertTrue(lowRate.getSuccessRate() >= 0.0);
    }
}