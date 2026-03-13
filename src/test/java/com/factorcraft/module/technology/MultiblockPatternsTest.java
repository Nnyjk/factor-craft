package com.factorcraft.module.technology;

import com.factorcraft.module.technology.MultiblockDetector.MultiblockPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MultiblockDetector 完整蓝图测试
 * 
 * 测试所有 12 种多方块结构
 */
@DisplayName("MultiblockDetector All Patterns Tests")
public class MultiblockPatternsTest {
    
    @Test
    @DisplayName("所有蓝图 - 获取 12 种结构")
    public void testAllPatternsCount() {
        List<MultiblockPattern> patterns = MultiblockDetector.getAllPatterns();
        
        assertNotNull(patterns);
        // 实际有 16 种 (包含 T1/T2/T3 变种)
        assertTrue(patterns.size() >= 12, "应该至少有 12 种结构蓝图");
    }
    
    @Test
    @DisplayName("基础共振炉 T1 - 结构验证")
    public void testBasicResonanceFurnace() {
        MultiblockPattern pattern = MultiblockDetector.createBasicResonanceFurnace();
        
        assertEquals("basic_resonance_furnace", pattern.getId());
        assertEquals("基础共振炉", pattern.getName());
        assertEquals(1, pattern.getTier());
        assertEquals(6, pattern.getStructure().size());
        assertTrue(pattern.getMaterials().size() >= 3);
    }
    
    @Test
    @DisplayName("进阶共振炉 T2 - 结构验证")
    public void testAdvancedResonanceFurnace() {
        MultiblockPattern pattern = MultiblockDetector.createAdvancedResonanceFurnace();
        
        assertEquals("advanced_resonance_furnace", pattern.getId());
        assertEquals("进阶共振炉", pattern.getName());
        assertEquals(2, pattern.getTier());
        assertTrue(pattern.getStructure().size() > 6, "T2 结构应该比 T1 复杂");
    }
    
    @Test
    @DisplayName("维度熔炉 T3 - 结构验证")
    public void testDimensionalFurnace() {
        MultiblockPattern pattern = MultiblockDetector.createDimensionalFurnace();
        
        assertEquals("dimensional_furnace", pattern.getId());
        assertEquals("维度熔炉", pattern.getName());
        assertEquals(3, pattern.getTier());
        assertTrue(pattern.getStructure().size() > 10, "T3 结构应该更复杂");
    }
    
    @Test
    @DisplayName("Factor 转换器 T1 - 结构验证")
    public void testFactorConverterT1() {
        MultiblockPattern pattern = MultiblockDetector.createFactorConverter();
        
        assertEquals("factor_converter_t1", pattern.getId());
        assertEquals("Factor 转换器 T1", pattern.getName());
        assertEquals(1, pattern.getTier());
        assertEquals(6, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("Factor 转换器 T2 - 结构验证")
    public void testFactorConverterT2() {
        MultiblockPattern pattern = MultiblockDetector.createFactorConverterT2();
        
        assertEquals("factor_converter_t2", pattern.getId());
        assertEquals("Factor 转换器 T2", pattern.getName());
        assertEquals(2, pattern.getTier());
        assertEquals(7, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("Factor 转换器 T3 - 结构验证")
    public void testFactorConverterT3() {
        MultiblockPattern pattern = MultiblockDetector.createFactorConverterT3();
        
        assertEquals("factor_converter_t3", pattern.getId());
        assertEquals("Factor 转换器 T3", pattern.getName());
        assertEquals(3, pattern.getTier());
        assertEquals(8, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("共振线圈阵列 T2 - 结构验证")
    public void testResonanceCoilArray() {
        MultiblockPattern pattern = MultiblockDetector.createResonanceCoilArray();
        
        assertEquals("resonance_coil_array", pattern.getId());
        assertEquals("共振线圈阵列", pattern.getName());
        assertEquals(2, pattern.getTier());
        assertEquals(10, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("维度稳定器 T3 - 结构验证")
    public void testDimensionStabilizer() {
        MultiblockPattern pattern = MultiblockDetector.createDimensionStabilizer();
        
        assertEquals("dimension_stabilizer", pattern.getId());
        assertEquals("维度稳定器", pattern.getName());
        assertEquals(3, pattern.getTier());
        assertEquals(11, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("Factor 储存单元 T1 - 结构验证")
    public void testFactorStorageT1() {
        MultiblockPattern pattern = MultiblockDetector.createFactorStorageT1();
        
        assertEquals("factor_storage_t1", pattern.getId());
        assertEquals("Factor 储存单元 T1", pattern.getName());
        assertEquals(1, pattern.getTier());
        assertEquals(6, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("Factor 储存单元 T2 - 结构验证")
    public void testFactorStorageT2() {
        MultiblockPattern pattern = MultiblockDetector.createFactorStorageT2();
        
        assertEquals("factor_storage_t2", pattern.getId());
        assertEquals("Factor 储存单元 T2", pattern.getName());
        assertEquals(2, pattern.getTier());
        assertEquals(7, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("Factor 储存单元 T3 - 结构验证")
    public void testFactorStorageT3() {
        MultiblockPattern pattern = MultiblockDetector.createFactorStorageT3();
        
        assertEquals("factor_storage_t3", pattern.getId());
        assertEquals("Factor 储存单元 T3", pattern.getName());
        assertEquals(3, pattern.getTier());
        assertEquals(8, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("共振工作台 T1 - 结构验证")
    public void testResonanceWorkbench() {
        MultiblockPattern pattern = MultiblockDetector.createResonanceWorkbench();
        
        assertEquals("resonance_workbench", pattern.getId());
        assertEquals("共振工作台", pattern.getName());
        assertEquals(1, pattern.getTier());
        assertEquals(6, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("维度锻造台 T2 - 结构验证")
    public void testDimensionalForge() {
        MultiblockPattern pattern = MultiblockDetector.createDimensionalForge();
        
        assertEquals("dimensional_forge", pattern.getId());
        assertEquals("维度锻造台", pattern.getName());
        assertEquals(2, pattern.getTier());
        assertEquals(7, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("Factor 注入器 T3 - 结构验证")
    public void testFactorInjector() {
        MultiblockPattern pattern = MultiblockDetector.createFactorInjector();
        
        assertEquals("factor_injector", pattern.getId());
        assertEquals("Factor 注入器", pattern.getName());
        assertEquals(3, pattern.getTier());
        assertEquals(8, pattern.getStructure().size());
    }
    
    @Test
    @DisplayName("共鸣祭坛 T4 - 结构验证")
    public void testResonanceAltar() {
        MultiblockPattern pattern = MultiblockDetector.createResonanceAltar();
        
        assertEquals("resonance_altar", pattern.getId());
        assertEquals("共鸣祭坛", pattern.getName());
        assertEquals(4, pattern.getTier());
        assertTrue(pattern.getStructure().size() > 10, "T4 结构应该很复杂");
    }
    
    @Test
    @DisplayName("维度之门 T5 - 结构验证")
    public void testDimensionalGate() {
        MultiblockPattern pattern = MultiblockDetector.createDimensionalGate();
        
        assertEquals("dimensional_gate", pattern.getId());
        assertEquals("维度之门", pattern.getName());
        assertEquals(5, pattern.getTier());
        assertTrue(pattern.getStructure().size() > 15, "T5 结构应该最复杂");
    }
    
    @Test
    @DisplayName("结构复杂度 - 等级递增验证")
    public void testComplexityProgression() {
        // T1 结构
        MultiblockPattern t1 = MultiblockDetector.createBasicResonanceFurnace();
        // T3 结构
        MultiblockPattern t3 = MultiblockDetector.createDimensionalFurnace();
        // T5 结构
        MultiblockPattern t5 = MultiblockDetector.createDimensionalGate();
        
        // 验证复杂度递增
        assertTrue(t3.getStructure().size() > t1.getStructure().size(), 
            "T3 结构应该比 T1 复杂");
        assertTrue(t5.getStructure().size() > t3.getStructure().size(), 
            "T5 结构应该比 T3 复杂");
    }
    
    @Test
    @DisplayName("材料列表 - 非空验证")
    public void testMaterialsNonEmpty() {
        List<MultiblockPattern> patterns = MultiblockDetector.getAllPatterns();
        
        for (MultiblockPattern pattern : patterns) {
            assertNotNull(pattern.getMaterials(), 
                pattern.getId() + " 的材料列表不应为空");
            assertFalse(pattern.getMaterials().isEmpty(), 
                pattern.getId() + " 应该至少有一种材料");
        }
    }
    
    @Test
    @DisplayName("结构 ID - 唯一性验证")
    public void testPatternIdUniqueness() {
        List<MultiblockPattern> patterns = MultiblockDetector.getAllPatterns();
        
        for (int i = 0; i < patterns.size(); i++) {
            for (int j = i + 1; j < patterns.size(); j++) {
                assertNotEquals(patterns.get(i).getId(), patterns.get(j).getId(),
                    "结构 ID 应该唯一：" + patterns.get(i).getId());
            }
        }
    }
}
