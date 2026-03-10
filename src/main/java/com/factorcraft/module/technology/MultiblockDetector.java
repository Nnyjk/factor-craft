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
}
