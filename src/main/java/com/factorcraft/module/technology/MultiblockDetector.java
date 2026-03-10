package com.factorcraft.module.technology;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

/**
 * 多方块检测器
 * 
 * 支持 12 种结构蓝图检测
 */
public class MultiblockDetector {
    
    /**
     * 检测多方块结构
     * 
     * @param world 世界
     * @param origin 原点位置
     * @param pattern 结构蓝图
     * @return 是否形成完整结构
     */
    public static boolean detect(World world, BlockPos origin, MultiblockPattern pattern) {
        // 遍历蓝图中的所有位置
        for (Map.Entry<BlockPos, String> entry : pattern.getStructure().entrySet()) {
            BlockPos pos = origin.add(entry.getKey());
            String expectedBlock = entry.getValue();
            
            // 检查方块是否匹配
            if (!matchesBlock(world, pos, expectedBlock)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 检查方块是否匹配
     */
    private static boolean matchesBlock(World world, BlockPos pos, String expectedBlock) {
        // 获取实际方块状态
        var actualState = world.getBlockState(pos);
        var actualBlock = actualState.getBlock();
        
        // 获取实际方块的 Identifier
        var actualId = net.minecraft.registry.Registries.BLOCK.getId(actualBlock);
        if (actualId == null) {
            return false;
        }
        
        // 比较方块 ID
        String actualBlockId = actualId.toString();
        return actualBlockId.equals(expectedBlock);
    }
    
    /**
     * 多方块结构蓝图
     */
    public static class MultiblockPattern {
        private final String id;
        private final String name;
        private final int tier;
        private final Map<BlockPos, String> structure;
        private final List<String> materials;
        
        public MultiblockPattern(String id, String name, int tier,
                                Map<BlockPos, String> structure, List<String> materials) {
            this.id = id;
            this.name = name;
            this.tier = tier;
            this.structure = structure;
            this.materials = materials;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public int getTier() {
            return tier;
        }
        
        public Map<BlockPos, String> getStructure() {
            return structure;
        }
        
        public List<String> getMaterials() {
            return materials;
        }
    }
    
    /**
     * 创建基础共振炉蓝图 (T1)
     */
    public static MultiblockPattern createBasicResonanceFurnace() {
        // 3x3x3 结构
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_sink",
            new BlockPos(-1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(0, 0, -1), "factorcraft:resonance_coil",
            new BlockPos(0, 0, 1), "factorcraft:resonance_coil",
            new BlockPos(0, 1, 0), "factorcraft:factor_meter"
        );
        
        List<String> materials = List.of(
            "factorcraft:copper_ingot",
            "factorcraft:bronze_ingot",
            "factorcraft:resonance_coil"
        );
        
        return new MultiblockPattern(
            "basic_resonance_furnace",
            "基础共振炉",
            1,
            structure,
            materials
        );
    }
    
    /**
     * 创建进阶共振炉蓝图 (T2)
     */
    public static MultiblockPattern createAdvancedResonanceFurnace() {
        // 5x3x5 结构
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_sink"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(-2, 0, 0), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(2, 0, 0), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(0, 0, -2), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(0, 0, 2), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:factor_capacitor")
        );
        
        List<String> materials = List.of(
            "factorcraft:bronze_ingot",
            "factorcraft:resonance_coil",
            "factorcraft:factor_capacitor"
        );
        
        return new MultiblockPattern(
            "advanced_resonance_furnace",
            "进阶共振炉",
            2,
            structure,
            materials
        );
    }
    
    /**
     * 创建维度熔炉蓝图 (T3)
     */
    public static MultiblockPattern createDimensionalFurnace() {
        // 5x5x5 结构
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_transmitter"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(-2, 0, 0), "factorcraft:steel_block"),
            Map.entry(new BlockPos(2, 0, 0), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 0, -2), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 0, 2), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:dimension_stabilizer"),
            Map.entry(new BlockPos(0, 3, 0), "factorcraft:factor_capacitor"),
            Map.entry(new BlockPos(0, 4, 0), "factorcraft:dimension_anchor")
        );
        
        List<String> materials = List.of(
            "factorcraft:steel_ingot",
            "factorcraft:dimension_crystal",
            "factorcraft:dimension_stabilizer"
        );
        
        return new MultiblockPattern(
            "dimensional_furnace",
            "维度熔炉",
            3,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 转换器蓝图 (T1)
     */
    public static MultiblockPattern createFactorConverter() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_source",
            new BlockPos(-1, 0, 0), "factorcraft:copper_coil",
            new BlockPos(1, 0, 0), "factorcraft:copper_coil",
            new BlockPos(0, 0, -1), "factorcraft:copper_coil",
            new BlockPos(0, 0, 1), "factorcraft:copper_coil",
            new BlockPos(0, 1, 0), "factorcraft:factor_meter"
        );
        
        List<String> materials = List.of(
            "factorcraft:copper_ingot",
            "factorcraft:redstone"
        );
        
        return new MultiblockPattern(
            "factor_converter_t1",
            "Factor 转换器 T1",
            1,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 转换器蓝图 (T2)
     */
    public static MultiblockPattern createFactorConverterT2() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_source"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:factor_capacitor")
        );
        
        List<String> materials = List.of(
            "factorcraft:bronze_ingot",
            "factorcraft:redstone_block"
        );
        
        return new MultiblockPattern(
            "factor_converter_t2",
            "Factor 转换器 T2",
            2,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 转换器蓝图 (T3)
     */
    public static MultiblockPattern createFactorConverterT3() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_source"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:factor_capacitor"),
            Map.entry(new BlockPos(0, 3, 0), "factorcraft:dimension_crystal")
        );
        
        List<String> materials = List.of(
            "factorcraft:steel_ingot",
            "factorcraft:dimension_crystal"
        );
        
        return new MultiblockPattern(
            "factor_converter_t3",
            "Factor 转换器 T3",
            3,
            structure,
            materials
        );
    }
    
    /**
     * 创建共振线圈阵列蓝图 (T2)
     */
    public static MultiblockPattern createResonanceCoilArray() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:resonance_core"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(-1, 0, -1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(-1, 0, 1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(1, 0, -1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(1, 0, 1), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter")
        );
        
        List<String> materials = List.of(
            "factorcraft:resonance_coil",
            "factorcraft:resonance_core",
            "factorcraft:bronze_block"
        );
        
        return new MultiblockPattern(
            "resonance_coil_array",
            "共振线圈阵列",
            2,
            structure,
            materials
        );
    }
    
    /**
     * 创建维度稳定器蓝图 (T3)
     */
    public static MultiblockPattern createDimensionStabilizer() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:dimension_stabilizer"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(-1, 0, -1), "factorcraft:steel_block"),
            Map.entry(new BlockPos(-1, 0, 1), "factorcraft:steel_block"),
            Map.entry(new BlockPos(1, 0, -1), "factorcraft:steel_block"),
            Map.entry(new BlockPos(1, 0, 1), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:dimension_anchor")
        );
        
        List<String> materials = List.of(
            "factorcraft:dimension_crystal",
            "factorcraft:steel_block",
            "factorcraft:dimension_anchor"
        );
        
        return new MultiblockPattern(
            "dimension_stabilizer",
            "维度稳定器",
            3,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 储存单元蓝图 (T1)
     */
    public static MultiblockPattern createFactorStorageT1() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_tank",
            new BlockPos(-1, 0, 0), "factorcraft:copper_block",
            new BlockPos(1, 0, 0), "factorcraft:copper_block",
            new BlockPos(0, 0, -1), "factorcraft:copper_block",
            new BlockPos(0, 0, 1), "factorcraft:copper_block",
            new BlockPos(0, 1, 0), "factorcraft:factor_meter"
        );
        
        List<String> materials = List.of(
            "factorcraft:copper_ingot",
            "factorcraft:glass"
        );
        
        return new MultiblockPattern(
            "factor_storage_t1",
            "Factor 储存单元 T1",
            1,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 储存单元蓝图 (T2)
     */
    public static MultiblockPattern createFactorStorageT2() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_tank"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:bronze_block"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:factor_capacitor")
        );
        
        List<String> materials = List.of(
            "factorcraft:bronze_ingot",
            "factorcraft:factor_capacitor"
        );
        
        return new MultiblockPattern(
            "factor_storage_t2",
            "Factor 储存单元 T2",
            2,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 储存单元蓝图 (T3)
     */
    public static MultiblockPattern createFactorStorageT3() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_tank"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:steel_block"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:steel_block"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:factor_capacitor"),
            Map.entry(new BlockPos(0, 3, 0), "factorcraft:dimension_crystal")
        );
        
        List<String> materials = List.of(
            "factorcraft:steel_ingot",
            "factorcraft:dimension_crystal"
        );
        
        return new MultiblockPattern(
            "factor_storage_t3",
            "Factor 储存单元 T3",
            3,
            structure,
            materials
        );
    }
    
    /**
     * 创建共振工作台蓝图 (T1)
     */
    public static MultiblockPattern createResonanceWorkbench() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:resonance_workbench",
            new BlockPos(-1, 0, 0), "factorcraft:copper_coil",
            new BlockPos(1, 0, 0), "factorcraft:copper_coil",
            new BlockPos(0, 0, -1), "factorcraft:copper_coil",
            new BlockPos(0, 0, 1), "factorcraft:copper_coil",
            new BlockPos(0, 1, 0), "factorcraft:factor_meter"
        );
        
        List<String> materials = List.of(
            "factorcraft:copper_ingot",
            "factorcraft:crafting_table"
        );
        
        return new MultiblockPattern(
            "resonance_workbench",
            "共振工作台",
            1,
            structure,
            materials
        );
    }
    
    /**
     * 创建维度锻造台蓝图 (T2)
     */
    public static MultiblockPattern createDimensionalForge() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:dimensional_forge"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:bronze_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:anvil")
        );
        
        List<String> materials = List.of(
            "factorcraft:bronze_ingot",
            "factorcraft:anvil",
            "factorcraft:factor_capacitor"
        );
        
        return new MultiblockPattern(
            "dimensional_forge",
            "维度锻造台",
            2,
            structure,
            materials
        );
    }
    
    /**
     * 创建 Factor 注入器蓝图 (T3)
     */
    public static MultiblockPattern createFactorInjector() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:factor_injector"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:steel_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 3, 0), "factorcraft:factor_capacitor")
        );
        
        List<String> materials = List.of(
            "factorcraft:steel_ingot",
            "factorcraft:dimension_crystal",
            "factorcraft:factor_capacitor"
        );
        
        return new MultiblockPattern(
            "factor_injector",
            "Factor 注入器",
            3,
            structure,
            materials
        );
    }
    
    /**
     * 创建共鸣祭坛蓝图 (T4)
     */
    public static MultiblockPattern createResonanceAltar() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:resonance_altar"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(-2, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(2, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, -2), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, 2), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:dimension_anchor"),
            Map.entry(new BlockPos(0, 3, 0), "factorcraft:resonance_core"),
            Map.entry(new BlockPos(0, 4, 0), "factorcraft:factor_capacitor")
        );
        
        List<String> materials = List.of(
            "factorcraft:dimension_crystal",
            "factorcraft:resonance_coil",
            "factorcraft:resonance_core",
            "factorcraft:dimension_anchor"
        );
        
        return new MultiblockPattern(
            "resonance_altar",
            "共鸣祭坛",
            4,
            structure,
            materials
        );
    }
    
    /**
     * 创建维度之门蓝图 (T5)
     */
    public static MultiblockPattern createDimensionalGate() {
        Map<BlockPos, String> structure = Map.ofEntries(
            Map.entry(new BlockPos(0, 0, 0), "factorcraft:dimensional_gate"),
            Map.entry(new BlockPos(-1, 0, 0), "factorcraft:dimension_anchor"),
            Map.entry(new BlockPos(1, 0, 0), "factorcraft:dimension_anchor"),
            Map.entry(new BlockPos(0, 0, -1), "factorcraft:dimension_anchor"),
            Map.entry(new BlockPos(0, 0, 1), "factorcraft:dimension_anchor"),
            Map.entry(new BlockPos(-2, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(2, 0, 0), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, -2), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(0, 0, 2), "factorcraft:dimension_crystal"),
            Map.entry(new BlockPos(-3, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(3, 0, 0), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, -3), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 0, 3), "factorcraft:resonance_coil"),
            Map.entry(new BlockPos(0, 1, 0), "factorcraft:factor_meter"),
            Map.entry(new BlockPos(0, 2, 0), "factorcraft:factor_capacitor"),
            Map.entry(new BlockPos(0, 3, 0), "factorcraft:resonance_core"),
            Map.entry(new BlockPos(0, 4, 0), "factorcraft:dimension_stabilizer"),
            Map.entry(new BlockPos(0, 5, 0), "factorcraft:dimensional_frame")
        );
        
        List<String> materials = List.of(
            "factorcraft:dimension_anchor",
            "factorcraft:dimension_crystal",
            "factorcraft:resonance_coil",
            "factorcraft:resonance_core",
            "factorcraft:dimension_stabilizer",
            "factorcraft:dimensional_frame"
        );
        
        return new MultiblockPattern(
            "dimensional_gate",
            "维度之门",
            5,
            structure,
            materials
        );
    }
    
    /**
     * 获取所有 12 种结构蓝图
     */
    public static List<MultiblockPattern> getAllPatterns() {
        return List.of(
            createBasicResonanceFurnace(),
            createAdvancedResonanceFurnace(),
            createDimensionalFurnace(),
            createFactorConverter(),
            createFactorConverterT2(),
            createFactorConverterT3(),
            createResonanceCoilArray(),
            createDimensionStabilizer(),
            createFactorStorageT1(),
            createFactorStorageT2(),
            createFactorStorageT3(),
            createResonanceWorkbench(),
            createDimensionalForge(),
            createFactorInjector(),
            createResonanceAltar(),
            createDimensionalGate()
        );
    }
}
