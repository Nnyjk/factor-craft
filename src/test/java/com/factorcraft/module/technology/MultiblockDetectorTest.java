package com.factorcraft.module.technology;

import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MultiblockDetector 单元测试
 * 
 * 测试多方块结构检测功能
 */
@DisplayName("MultiblockDetector Tests")
public class MultiblockDetectorTest {
    
    @Test
    @DisplayName("MultiblockPattern - 创建基础共振炉蓝图")
    public void testCreateBasicResonanceFurnace() {
        MultiblockDetector.MultiblockPattern pattern = 
            MultiblockDetector.createBasicResonanceFurnace();
        
        assertNotNull(pattern);
        assertEquals("basic_resonance_furnace", pattern.getId());
        assertEquals("基础共振炉", pattern.getName());
        assertEquals(1, pattern.getTier());
        
        // 验证结构大小
        Map<BlockPos, String> structure = pattern.getStructure();
        assertNotNull(structure);
        assertEquals(6, structure.size()); // 6 个方块位置
        
        // 验证材料列表
        List<String> materials = pattern.getMaterials();
        assertNotNull(materials);
        assertEquals(3, materials.size());
        assertTrue(materials.contains("factorcraft:copper_ingot"));
        assertTrue(materials.contains("factorcraft:bronze_ingot"));
        assertTrue(materials.contains("factorcraft:resonance_coil"));
    }
    
    @Test
    @DisplayName("MultiblockPattern - 验证结构位置")
    public void testPatternStructurePositions() {
        MultiblockDetector.MultiblockPattern pattern = 
            MultiblockDetector.createBasicResonanceFurnace();
        
        Map<BlockPos, String> structure = pattern.getStructure();
        
        // 验证中心位置 (Factor Sink)
        BlockPos center = new BlockPos(0, 0, 0);
        assertTrue(structure.containsKey(center));
        assertEquals("factorcraft:factor_sink", structure.get(center));
        
        // 验证四个方向的 Resonance Coil
        assertTrue(structure.containsKey(new BlockPos(-1, 0, 0)));
        assertTrue(structure.containsKey(new BlockPos(1, 0, 0)));
        assertTrue(structure.containsKey(new BlockPos(0, 0, -1)));
        assertTrue(structure.containsKey(new BlockPos(0, 0, 1)));
        
        // 验证顶部的 Factor Meter
        assertTrue(structure.containsKey(new BlockPos(0, 1, 0)));
        assertEquals("factorcraft:factor_meter", structure.get(new BlockPos(0, 1, 0)));
    }
    
    @Test
    @DisplayName("MultiblockPattern - 自定义蓝图创建")
    public void testCustomPatternCreation() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:test_block"
        );
        
        List<String> materials = List.of("minecraft:stone");
        
        MultiblockDetector.MultiblockPattern pattern = 
            new MultiblockDetector.MultiblockPattern(
                "test_pattern",
                "测试蓝图",
                2,
                structure,
                materials
            );
        
        assertEquals("test_pattern", pattern.getId());
        assertEquals("测试蓝图", pattern.getName());
        assertEquals(2, pattern.getTier());
        assertEquals(1, pattern.getStructure().size());
        assertEquals(1, pattern.getMaterials().size());
    }
    
    @Test
    @DisplayName("BlockPos - 验证位置偏移计算")
    public void testBlockPosOffset() {
        BlockPos origin = new BlockPos(100, 64, 200);
        
        // 测试相对位置计算
        BlockPos offset1 = origin.add(-1, 0, 0);
        assertEquals(99, offset1.getX());
        assertEquals(64, offset1.getY());
        assertEquals(200, offset1.getZ());
        
        BlockPos offset2 = origin.add(1, 0, 0);
        assertEquals(101, offset2.getX());
        
        BlockPos offset3 = origin.add(0, 1, 0);
        assertEquals(65, offset3.getY());
        
        BlockPos offset4 = origin.add(0, 0, -1);
        assertEquals(199, offset4.getZ());
    }
    
    @Test
    @DisplayName("MultiblockPattern - 空结构验证")
    public void testEmptyPattern() {
        Map<BlockPos, String> emptyStructure = Map.of();
        List<String> emptyMaterials = List.of();
        
        MultiblockDetector.MultiblockPattern pattern = 
            new MultiblockDetector.MultiblockPattern(
                "empty_pattern",
                "空蓝图",
                0,
                emptyStructure,
                emptyMaterials
            );
        
        assertTrue(pattern.getStructure().isEmpty());
        assertTrue(pattern.getMaterials().isEmpty());
    }
}
