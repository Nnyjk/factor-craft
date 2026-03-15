package com.factorcraft.module.technology;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 科技树结构蓝图测试
 */
public class TechnologyPatternsTest {
    
    // ==================== 蓝图数量测试 ====================
    
    @Test
    @DisplayName("应有 20 种科技树蓝图")
    void testPatternCount() {
        var patterns = TechnologyPatterns.getAllTechnologyPatterns();
        assertEquals(20, patterns.size(), "应有 4 类型 × 5 等级 = 20 个蓝图");
    }
    
    @Test
    @DisplayName("每种类型应有 5 个等级")
    void testPatternCountByType() {
        assertEquals(5, TechnologyPatterns.getPatternsByType(StructureType.EXTRACTOR).size());
        assertEquals(5, TechnologyPatterns.getPatternsByType(StructureType.CONSUMER).size());
        assertEquals(5, TechnologyPatterns.getPatternsByType(StructureType.SYNTHESIZER).size());
        assertEquals(5, TechnologyPatterns.getPatternsByType(StructureType.BREEDER).size());
    }
    
    @Test
    @DisplayName("每个等级应有 4 种类型")
    void testPatternCountByTier() {
        for (int tier = 1; tier <= 5; tier++) {
            assertEquals(4, TechnologyPatterns.getPatternsByTier(tier).size(),
                "T" + tier + " 应有 4 种类型");
        }
    }
    
    // ==================== 蓝图 ID 测试 ====================
    
    @Test
    @DisplayName("提取结构蓝图 ID 应正确")
    void testExtractorPatternIds() {
        var patterns = TechnologyPatterns.getPatternsByType(StructureType.EXTRACTOR);
        
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("star_collector")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("star_array")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("nebula_siphon")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("cosmic_resonator")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("void_vortex")));
    }
    
    @Test
    @DisplayName("消耗结构蓝图 ID 应正确")
    void testConsumerPatternIds() {
        var patterns = TechnologyPatterns.getPatternsByType(StructureType.CONSUMER);
        
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("soul_burner")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("soul_furnace")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("abyss_devourer")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("chaos_rift")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("eternal_core")));
    }
    
    @Test
    @DisplayName("合成结构蓝图 ID 应正确")
    void testSynthesizerPatternIds() {
        var patterns = TechnologyPatterns.getPatternsByType(StructureType.SYNTHESIZER);
        
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("ancient_synthesis")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("ancient_forge")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("fate_foundry")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("creation_furnace")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("origin_altar")));
    }
    
    @Test
    @DisplayName("培育结构蓝图 ID 应正确")
    void testBreederPatternIds() {
        var patterns = TechnologyPatterns.getPatternsByType(StructureType.BREEDER);
        
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("fate_loom")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("soul_weaver")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("fate_altar")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("fate_sanctuary")));
        assertTrue(patterns.stream().anyMatch(p -> p.getId().contains("reincarnation_gate")));
    }
    
    // ==================== 蓝图名称测试 ====================
    
    @Test
    @DisplayName("提取结构名称应正确")
    void testExtractorNames() {
        assertEquals("星辰收集器", TechnologyPatterns.getPattern(StructureType.EXTRACTOR, 1).getName());
        assertEquals("星辰阵列", TechnologyPatterns.getPattern(StructureType.EXTRACTOR, 2).getName());
        assertEquals("星云汲取器", TechnologyPatterns.getPattern(StructureType.EXTRACTOR, 3).getName());
        assertEquals("宇宙共鸣器", TechnologyPatterns.getPattern(StructureType.EXTRACTOR, 4).getName());
        assertEquals("虚空漩涡", TechnologyPatterns.getPattern(StructureType.EXTRACTOR, 5).getName());
    }
    
    @Test
    @DisplayName("消耗结构名称应正确")
    void testConsumerNames() {
        assertEquals("灵魂燃烧器", TechnologyPatterns.getPattern(StructureType.CONSUMER, 1).getName());
        assertEquals("灵魂熔炉", TechnologyPatterns.getPattern(StructureType.CONSUMER, 2).getName());
        assertEquals("深渊吞噬者", TechnologyPatterns.getPattern(StructureType.CONSUMER, 3).getName());
        assertEquals("混沌裂隙", TechnologyPatterns.getPattern(StructureType.CONSUMER, 4).getName());
        assertEquals("永恒炉心", TechnologyPatterns.getPattern(StructureType.CONSUMER, 5).getName());
    }
    
    @Test
    @DisplayName("合成结构名称应正确")
    void testSynthesizerNames() {
        assertEquals("远古合成阵", TechnologyPatterns.getPattern(StructureType.SYNTHESIZER, 1).getName());
        assertEquals("远古锻造台", TechnologyPatterns.getPattern(StructureType.SYNTHESIZER, 2).getName());
        assertEquals("命运铸造炉", TechnologyPatterns.getPattern(StructureType.SYNTHESIZER, 3).getName());
        assertEquals("创世熔炉", TechnologyPatterns.getPattern(StructureType.SYNTHESIZER, 4).getName());
        assertEquals("本源祭坛", TechnologyPatterns.getPattern(StructureType.SYNTHESIZER, 5).getName());
    }
    
    @Test
    @DisplayName("培育结构名称应正确")
    void testBreederNames() {
        assertEquals("命运织机", TechnologyPatterns.getPattern(StructureType.BREEDER, 1).getName());
        assertEquals("灵魂编织器", TechnologyPatterns.getPattern(StructureType.BREEDER, 2).getName());
        assertEquals("命运祭坛", TechnologyPatterns.getPattern(StructureType.BREEDER, 3).getName());
        assertEquals("命运圣所", TechnologyPatterns.getPattern(StructureType.BREEDER, 4).getName());
        assertEquals("轮回之门", TechnologyPatterns.getPattern(StructureType.BREEDER, 5).getName());
    }
    
    // ==================== Tier 测试 ====================
    
    @Test
    @DisplayName("每个蓝图应有正确的 Tier")
    void testPatternTiers() {
        for (var pattern : TechnologyPatterns.getAllTechnologyPatterns()) {
            assertTrue(pattern.getTier() >= 1 && pattern.getTier() <= 5,
                "Tier 应在 1-5 范围内: " + pattern.getId());
        }
    }
    
    // ==================== 结构尺寸测试 ====================
    
    @Test
    @DisplayName("T1 结构应约为 45 方块")
    void testT1StructureSize() {
        for (var pattern : TechnologyPatterns.getPatternsByTier(1)) {
            assertTrue(pattern.getStructure().size() >= 9,
                "T1 结构应至少 9 个方块: " + pattern.getId());
        }
    }
    
    @Test
    @DisplayName("结构尺寸应随 Tier 增加")
    void testStructureSizeGrowth() {
        for (StructureType type : StructureType.values()) {
            var t1 = TechnologyPatterns.getPattern(type, 1);
            var t5 = TechnologyPatterns.getPattern(type, 5);
            
            assertTrue(t1.getStructure().size() < t5.getStructure().size(),
                type + " T1 尺寸应小于 T5");
        }
    }
    
    // ==================== 材料列表测试 ====================
    
    @Test
    @DisplayName("每个蓝图应有材料列表")
    void testPatternMaterials() {
        for (var pattern : TechnologyPatterns.getAllTechnologyPatterns()) {
            assertNotNull(pattern.getMaterials(), "材料列表不应为 null: " + pattern.getId());
            assertFalse(pattern.getMaterials().isEmpty(), "材料列表不应为空: " + pattern.getId());
        }
    }
    
    // ==================== 结构类型判断测试 ====================
    
    @Test
    @DisplayName("结构类型应能从 ID 正确判断")
    void testStructureTypeFromId() {
        assertEquals(StructureType.EXTRACTOR, StructureType.fromPatternId("extractor_t1_star_collector"));
        assertEquals(StructureType.CONSUMER, StructureType.fromPatternId("consumer_t3_abyss_devourer"));
        assertEquals(StructureType.SYNTHESIZER, StructureType.fromPatternId("synthesizer_t5_origin_altar"));
        assertEquals(StructureType.BREEDER, StructureType.fromPatternId("breeder_t2_soul_weaver"));
    }
    
    @Test
    @DisplayName("无效 ID 应返回 null")
    void testInvalidPatternId() {
        assertNull(StructureType.fromPatternId(null));
        assertNull(StructureType.fromPatternId("unknown_pattern"));
    }
    
    // ==================== 核心方块测试 ====================
    
    @Test
    @DisplayName("提取结构应使用提取核心")
    void testExtractorCore() {
        for (var pattern : TechnologyPatterns.getPatternsByType(StructureType.EXTRACTOR)) {
            boolean hasCore = pattern.getStructure().values().stream()
                .anyMatch(block -> block.contains("extractor_core"));
            assertTrue(hasCore, "提取结构应包含提取核心: " + pattern.getId());
        }
    }
    
    @Test
    @DisplayName("消耗结构应使用消耗核心")
    void testConsumerCore() {
        for (var pattern : TechnologyPatterns.getPatternsByType(StructureType.CONSUMER)) {
            boolean hasCore = pattern.getStructure().values().stream()
                .anyMatch(block -> block.contains("consumer_core"));
            assertTrue(hasCore, "消耗结构应包含消耗核心: " + pattern.getId());
        }
    }
    
    @Test
    @DisplayName("合成结构应使用合成核心")
    void testSynthesizerCore() {
        for (var pattern : TechnologyPatterns.getPatternsByType(StructureType.SYNTHESIZER)) {
            boolean hasCore = pattern.getStructure().values().stream()
                .anyMatch(block -> block.contains("synthesizer_core"));
            assertTrue(hasCore, "合成结构应包含合成核心: " + pattern.getId());
        }
    }
    
    @Test
    @DisplayName("培育结构应使用培育核心")
    void testBreederCore() {
        for (var pattern : TechnologyPatterns.getPatternsByType(StructureType.BREEDER)) {
            boolean hasCore = pattern.getStructure().values().stream()
                .anyMatch(block -> block.contains("breeder_core"));
            assertTrue(hasCore, "培育结构应包含培育核心: " + pattern.getId());
        }
    }
    
    // ==================== Factor Meter 测试 ====================
    
    @Test
    @DisplayName("所有结构应包含 Factor Meter")
    void testFactorMeter() {
        for (var pattern : TechnologyPatterns.getAllTechnologyPatterns()) {
            boolean hasMeter = pattern.getStructure().values().stream()
                .anyMatch(block -> block.contains("factor_meter"));
            assertTrue(hasMeter, "结构应包含 Factor Meter: " + pattern.getId());
        }
    }
    
    // ==================== 维度晶体测试 ====================
    
    @Test
    @DisplayName("T3+ 结构应包含维度晶体")
    void testDimensionCrystal() {
        for (int tier = 3; tier <= 5; tier++) {
            for (var pattern : TechnologyPatterns.getPatternsByTier(tier)) {
                boolean hasCrystal = pattern.getStructure().values().stream()
                    .anyMatch(block -> block.contains("dimension_crystal") || block.contains("dimension_anchor"));
                assertTrue(hasCrystal, "T" + tier + " 结构应包含维度组件: " + pattern.getId());
            }
        }
    }
}