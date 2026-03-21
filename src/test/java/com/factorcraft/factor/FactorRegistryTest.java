package com.factorcraft.factor;

import net.minecraft.util.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Factor 注册系统测试
 */
class FactorRegistryTest {
    
    @BeforeEach
    void setUp() {
        // 重置注册表
        FactorRegistry.getInstance().reset();
    }
    
    @Test
    void testRegisterFactor() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "test_factor"),
            "测试 Factor"
        )
            .type(FactorType.FIRE)
            .rarity(FactorRarity.COMMON)
            .level(1)
            .tier(1)
            .basePower(10.0)
            .build();
        
        assertTrue(FactorRegistry.getInstance().register(factor));
        assertEquals(1, FactorRegistry.getInstance().size());
    }
    
    @Test
    void testRegisterDuplicateId() {
        Factor factor1 = new Factor.Builder(
            Identifier.of("test", "duplicate"),
            "Factor 1"
        ).build();
        
        Factor factor2 = new Factor.Builder(
            Identifier.of("test", "duplicate"),
            "Factor 2"
        ).build();
        
        assertTrue(FactorRegistry.getInstance().register(factor1));
        assertFalse(FactorRegistry.getInstance().register(factor2));
        assertEquals(1, FactorRegistry.getInstance().size());
    }
    
    @Test
    void testGetById() {
        Identifier id = Identifier.of("test", "get_test");
        Factor factor = new Factor.Builder(id, "获取测试").build();
        
        FactorRegistry.getInstance().register(factor);
        
        assertTrue(FactorRegistry.getInstance().get(id).isPresent());
        assertEquals(factor, FactorRegistry.getInstance().get(id).get());
    }
    
    @Test
    void testGetByString() {
        Identifier id = Identifier.of("test", "string_test");
        Factor factor = new Factor.Builder(id, "字符串测试").build();
        
        FactorRegistry.getInstance().register(factor);
        
        assertTrue(FactorRegistry.getInstance().get("test:string_test").isPresent());
    }
    
    @Test
    void testGetByType() {
        Factor fireFactor = new Factor.Builder(
            Identifier.of("test", "fire_factor"),
            "火因子"
        ).type(FactorType.FIRE).build();
        
        Factor waterFactor = new Factor.Builder(
            Identifier.of("test", "water_factor"),
            "水因子"
        ).type(FactorType.WATER).build();
        
        FactorRegistry.getInstance().register(fireFactor);
        FactorRegistry.getInstance().register(waterFactor);
        
        Set<Factor> fireFactors = FactorRegistry.getInstance().getByType(FactorType.FIRE);
        assertEquals(1, fireFactors.size());
        assertTrue(fireFactors.contains(fireFactor));
        
        Set<Factor> waterFactors = FactorRegistry.getInstance().getByType(FactorType.WATER);
        assertEquals(1, waterFactors.size());
        assertTrue(waterFactors.contains(waterFactor));
        
        Set<Factor> earthFactors = FactorRegistry.getInstance().getByType(FactorType.EARTH);
        assertTrue(earthFactors.isEmpty());
    }
    
    @Test
    void testGetByRarity() {
        Factor commonFactor = new Factor.Builder(
            Identifier.of("test", "common"),
            "普通"
        ).rarity(FactorRarity.COMMON).build();
        
        Factor rareFactor = new Factor.Builder(
            Identifier.of("test", "rare"),
            "稀有"
        ).rarity(FactorRarity.RARE).build();
        
        FactorRegistry.getInstance().register(commonFactor);
        FactorRegistry.getInstance().register(rareFactor);
        
        Set<Factor> commonFactors = FactorRegistry.getInstance().getByRarity(FactorRarity.COMMON);
        assertEquals(1, commonFactors.size());
        
        Set<Factor> rareFactors = FactorRegistry.getInstance().getByRarity(FactorRarity.RARE);
        assertEquals(1, rareFactors.size());
    }
    
    @Test
    void testGetByTag() {
        Factor factor1 = new Factor.Builder(
            Identifier.of("test", "tagged1"),
            "标签1"
        ).addTag("craftable").addTag("basic").build();
        
        Factor factor2 = new Factor.Builder(
            Identifier.of("test", "tagged2"),
            "标签2"
        ).addTag("craftable").addTag("advanced").build();
        
        FactorRegistry.getInstance().register(factor1);
        FactorRegistry.getInstance().register(factor2);
        
        Set<Factor> craftable = FactorRegistry.getInstance().getByTag("craftable");
        assertEquals(2, craftable.size());
        
        Set<Factor> basic = FactorRegistry.getInstance().getByTag("basic");
        assertEquals(1, basic.size());
        assertTrue(basic.contains(factor1));
    }
    
    @Test
    void testGetByAllTags() {
        Factor factor1 = new Factor.Builder(
            Identifier.of("test", "all1"),
            "All 1"
        ).addTag("a").addTag("b").build();
        
        Factor factor2 = new Factor.Builder(
            Identifier.of("test", "all2"),
            "All 2"
        ).addTag("a").addTag("c").build();
        
        FactorRegistry.getInstance().register(factor1);
        FactorRegistry.getInstance().register(factor2);
        
        Set<Factor> withAandB = FactorRegistry.getInstance().getByAllTags(Set.of("a", "b"));
        assertEquals(1, withAandB.size());
        assertTrue(withAandB.contains(factor1));
        
        Set<Factor> withAandC = FactorRegistry.getInstance().getByAllTags(Set.of("a", "c"));
        assertEquals(1, withAandC.size());
        assertTrue(withAandC.contains(factor2));
        
        Set<Factor> withAandBAndC = FactorRegistry.getInstance().getByAllTags(Set.of("a", "b", "c"));
        assertTrue(withAandBAndC.isEmpty());
    }
    
    @Test
    void testFreezeRegistry() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "freeze"),
            "冻结测试"
        ).build();
        
        FactorRegistry.getInstance().freeze();
        
        assertThrows(IllegalStateException.class, () -> 
            FactorRegistry.getInstance().register(factor)
        );
    }
    
    @Test
    void testForEach() {
        for (int i = 0; i < 5; i++) {
            Factor factor = new Factor.Builder(
                Identifier.of("test", "foreach_" + i),
                "遍历 " + i
            ).build();
            FactorRegistry.getInstance().register(factor);
        }
        
        final int[] count = {0};
        FactorRegistry.getInstance().forEach(f -> count[0]++);
        assertEquals(5, count[0]);
    }
    
    @Test
    void testStream() {
        for (int i = 0; i < 3; i++) {
            Factor factor = new Factor.Builder(
                Identifier.of("test", "stream_" + i),
                "流 " + i
            ).level(i + 1).build();
            FactorRegistry.getInstance().register(factor);
        }
        
        long highLevelCount = FactorRegistry.getInstance().stream()
            .filter(f -> f.getLevel() >= 2)
            .count();
        assertEquals(2, highLevelCount);
    }
}