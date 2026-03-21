package com.factorcraft.factor;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Factor 数据类测试
 */
class FactorTest {
    
    @Test
    void testBuilder() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "builder_test"),
            "构建器测试"
        )
            .type(FactorType.FIRE)
            .rarity(FactorRarity.RARE)
            .level(50)
            .tier(3)
            .basePower(100.0)
            .addTag("test_tag")
            .description("测试描述")
            .build();
        
        assertEquals(Identifier.of("test", "builder_test"), factor.getId());
        assertEquals("构建器测试", factor.getName());
        assertEquals(FactorType.FIRE, factor.getType());
        assertEquals(FactorRarity.RARE, factor.getRarity());
        assertEquals(50, factor.getLevel());
        assertEquals(3, factor.getTier());
        assertEquals(100.0, factor.getBasePower());
        assertTrue(factor.hasTag("test_tag"));
        assertTrue(factor.getDescription().isPresent());
        assertEquals("测试描述", factor.getDescription().get());
    }
    
    @Test
    void testDefaultValues() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "defaults"),
            "默认值测试"
        ).build();
        
        assertEquals(FactorType.ELEMENTAL, factor.getType());
        assertEquals(FactorRarity.COMMON, factor.getRarity());
        assertEquals(1, factor.getLevel());
        assertEquals(1, factor.getTier());
        assertEquals(1.0, factor.getBasePower());
        assertTrue(factor.getTags().isEmpty());
        assertTrue(factor.getDescription().isEmpty());
    }
    
    @Test
    void testLevelClamping() {
        Factor tooHigh = new Factor.Builder(
            Identifier.of("test", "high"),
            "高等级"
        ).level(200).build();
        
        assertEquals(100, tooHigh.getLevel());
        
        Factor tooLow = new Factor.Builder(
            Identifier.of("test", "low"),
            "低等级"
        ).level(0).build();
        
        assertEquals(1, tooLow.getLevel());
    }
    
    @Test
    void testTierClamping() {
        Factor tooHigh = new Factor.Builder(
            Identifier.of("test", "high_tier"),
            "高层级"
        ).tier(10).build();
        
        assertEquals(5, tooHigh.getTier());
        
        Factor tooLow = new Factor.Builder(
            Identifier.of("test", "low_tier"),
            "低层级"
        ).tier(0).build();
        
        assertEquals(1, tooLow.getTier());
    }
    
    @Test
    void testActualPowerCalculation() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "power"),
            "功率测试"
        )
            .level(10)  // 1.0 + (10-1) * 0.1 = 1.9
            .rarity(FactorRarity.RARE)  // 1.0 + 2 * 0.25 = 1.5
            .basePower(100.0)
            .build();
        
        double expectedPower = 100.0 * 1.9 * 1.5;
        assertEquals(expectedPower, factor.getActualPower(), 0.001);
    }
    
    @Test
    void testTags() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "tags"),
            "标签测试"
        )
            .addTag("tag1")
            .addTag("tag2")
            .addTag("tag3")
            .build();
        
        assertEquals(3, factor.getTags().size());
        assertTrue(factor.hasTag("tag1"));
        assertTrue(factor.hasTag("tag2"));
        assertTrue(factor.hasTag("tag3"));
        assertFalse(factor.hasTag("tag4"));
        
        // 测试标签集合不可修改
        Set<String> tags = factor.getTags();
        assertThrows(UnsupportedOperationException.class, () -> tags.add("new_tag"));
    }
    
    @Test
    void testNbtSerialization() {
        Factor original = new Factor.Builder(
            Identifier.of("test", "nbt_test"),
            "NBT序列化测试"
        )
            .type(FactorType.LIFE)
            .rarity(FactorRarity.EPIC)
            .level(75)
            .tier(4)
            .basePower(250.0)
            .addTag("nbt_tag1")
            .addTag("nbt_tag2")
            .description("NBT描述")
            .build();
        
        // 序列化到 NBT
        NbtCompound nbt = original.toNbt();
        
        // 从 NBT 反序列化
        Factor deserialized = Factor.fromNbt(nbt);
        
        assertEquals(original.getId(), deserialized.getId());
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getType(), deserialized.getType());
        assertEquals(original.getRarity(), deserialized.getRarity());
        assertEquals(original.getLevel(), deserialized.getLevel());
        assertEquals(original.getTier(), deserialized.getTier());
        assertEquals(original.getBasePower(), deserialized.getBasePower());
        assertEquals(original.getTags(), deserialized.getTags());
        assertEquals(original.getDescription(), deserialized.getDescription());
    }
    
    @Test
    void testNbtDeserializationWithMissingFields() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", "test:partial");
        nbt.putString("name", "部分数据");
        
        Factor factor = Factor.fromNbt(nbt);
        
        assertEquals(Identifier.of("test", "partial"), factor.getId());
        assertEquals("部分数据", factor.getName());
        // 默认值
        assertEquals(FactorType.ELEMENTAL, factor.getType());
        assertEquals(FactorRarity.COMMON, factor.getRarity());
        assertEquals(1, factor.getLevel());
        assertEquals(1, factor.getTier());
        assertEquals(1.0, factor.getBasePower());
    }
    
    @Test
    void testEquality() {
        Factor factor1 = new Factor.Builder(
            Identifier.of("test", "same_id"),
            "Factor 1"
        ).level(10).build();
        
        Factor factor2 = new Factor.Builder(
            Identifier.of("test", "same_id"),
            "Factor 2"
        ).level(50).build();
        
        Factor factor3 = new Factor.Builder(
            Identifier.of("test", "different_id"),
            "Factor 3"
        ).build();
        
        // 只有 ID 决定相等性
        assertEquals(factor1, factor2);
        assertNotEquals(factor1, factor3);
        assertEquals(factor1.hashCode(), factor2.hashCode());
    }
    
    @Test
    void testToString() {
        Factor factor = new Factor.Builder(
            Identifier.of("test", "string_test"),
            "字符串测试"
        )
            .type(FactorType.FIRE)
            .rarity(FactorRarity.RARE)
            .level(50)
            .build();
        
        String str = factor.toString();
        assertTrue(str.contains("string_test"));
        assertTrue(str.contains("FIRE"));
        assertTrue(str.contains("RARE"));
        assertTrue(str.contains("50"));
    }
}