package com.factorcraft.module.technology;

import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * 科技树结构蓝图
 * 
 * 定义与设计文档一致的 T1-T5 四大结构类型蓝图
 * 
 * 结构命名表：
 * | Tier | 提取 | 消耗 | 合成 | 培育 |
 * |------|------|------|------|------|
 * | T1   | 星辰收集器 | 灵魂燃烧器 | 远古合成阵 | 命运织机 |
 * | T2   | 星辰阵列 | 灵魂熔炉 | 远古锻造台 | 灵魂编织器 |
 * | T3   | 星云汲取器 | 深渊吞噬者 | 命运铸造炉 | 命运祭坛 |
 * | T4   | 宇宙共鸣器 | 混沌裂隙 | 创世熔炉 | 命运圣所 |
 * | T5   | 虚空漩涡 | 永恒炉心 | 本源祭坛 | 轮回之门 |
 */
public final class TechnologyPatterns {
    
    private TechnologyPatterns() {}
    
    // ==================== 核心方块 ID ====================
    
    // 提取结构核心
    public static final String EXTRACTOR_CORE_T1 = "factorcraft:extractor_core";
    public static final String EXTRACTOR_CORE_T2 = "factorcraft:extractor_core";
    public static final String EXTRACTOR_CORE_T3 = "factorcraft:extractor_core";
    public static final String EXTRACTOR_CORE_T4 = "factorcraft:extractor_core";
    public static final String EXTRACTOR_CORE_T5 = "factorcraft:extractor_core";
    
    // 消耗结构核心
    public static final String CONSUMER_CORE_T1 = "factorcraft:consumer_core";
    public static final String CONSUMER_CORE_T2 = "factorcraft:consumer_core";
    public static final String CONSUMER_CORE_T3 = "factorcraft:consumer_core";
    public static final String CONSUMER_CORE_T4 = "factorcraft:consumer_core";
    public static final String CONSUMER_CORE_T5 = "factorcraft:consumer_core";
    
    // 合成结构核心
    public static final String SYNTHESIZER_CORE_T1 = "factorcraft:synthesizer_core";
    public static final String SYNTHESIZER_CORE_T2 = "factorcraft:synthesizer_core";
    public static final String SYNTHESIZER_CORE_T3 = "factorcraft:synthesizer_core";
    public static final String SYNTHESIZER_CORE_T4 = "factorcraft:synthesizer_core";
    public static final String SYNTHESIZER_CORE_T5 = "factorcraft:synthesizer_core";
    
    // 培育结构核心
    public static final String BREEDER_CORE_T1 = "factorcraft:breeder_core";
    public static final String BREEDER_CORE_T2 = "factorcraft:breeder_core";
    public static final String BREEDER_CORE_T3 = "factorcraft:breeder_core";
    public static final String BREEDER_CORE_T4 = "factorcraft:breeder_core";
    public static final String BREEDER_CORE_T5 = "factorcraft:breeder_core";
    
    // 材料方块
    public static final String DUST_COPPER_BLOCK = "factorcraft:dust_copper_block";
    public static final String SHADOW_STEEL_BLOCK = "factorcraft:shadow_steel_block";
    public static final String STARDUST_BLOCK = "factorcraft:stardust_block";
    public static final String ANCIENT_ALLOY_BLOCK = "factorcraft:ancient_alloy_block";
    public static final String VOID_CRYSTAL_BLOCK = "factorcraft:void_crystal_block";
    
    // 基础方块
    public static final String FACTOR_METER = "factorcraft:factor_meter";
    public static final String FACTOR_CAPACITOR = "factorcraft:factor_capacitor";
    public static final String RESONANCE_COIL = "factorcraft:resonance_coil";
    public static final String DIMENSION_CRYSTAL = "factorcraft:dimension_crystal";
    public static final String DIMENSION_ANCHOR = "factorcraft:dimension_anchor";
    
    // ==================== 提取结构蓝图 ====================
    
    /**
     * T1 星辰收集器 (Star Collector)
     * 尺寸: 3×5×3 (45 方块)
     * 推荐维度: 主世界
     */
    public static MultiblockDetector.MultiblockPattern createStarCollector() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层 3×3
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                structure.put(new BlockPos(x, 0, z), DUST_COPPER_BLOCK);
            }
        }
        
        // 中层柱子
        structure.put(new BlockPos(0, 1, 0), EXTRACTOR_CORE_T1);
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "extractor_t1_star_collector",
            "星辰收集器",
            1,
            structure,
            List.of("factorcraft:dust_copper_ingot", RESONANCE_COIL)
        );
    }
    
    /**
     * T2 星辰阵列 (Star Array)
     * 尺寸: 5×7×5 (175 方块)
     * 推荐维度: 主世界
     */
    public static MultiblockDetector.MultiblockPattern createStarArray() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层 5×5
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                structure.put(new BlockPos(x, 0, z), SHADOW_STEEL_BLOCK);
            }
        }
        
        // 中层核心
        structure.put(new BlockPos(0, 1, 0), EXTRACTOR_CORE_T2);
        structure.put(new BlockPos(-1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 1, -1), RESONANCE_COIL);
        structure.put(new BlockPos(0, 1, 1), RESONANCE_COIL);
        
        // 柱子
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 6, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "extractor_t2_star_array",
            "星辰阵列",
            2,
            structure,
            List.of("factorcraft:shadow_steel_ingot", RESONANCE_COIL, FACTOR_CAPACITOR)
        );
    }
    
    /**
     * T3 星云汲取器 (Nebula Siphon)
     * 尺寸: 7×9×7 (441 方块)
     * 推荐维度: 下界
     */
    public static MultiblockDetector.MultiblockPattern createNebulaSiphon() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层 7×7
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                structure.put(new BlockPos(x, 0, z), STARDUST_BLOCK);
            }
        }
        
        // 中层环
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    structure.put(new BlockPos(x, 1, z), DIMENSION_CRYSTAL);
                }
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 1, 0), EXTRACTOR_CORE_T3);
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 6, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 7, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 8, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "extractor_t3_nebula_siphon",
            "星云汲取器",
            3,
            structure,
            List.of("factorcraft:stardust_ingot", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T4 宇宙共鸣器 (Cosmic Resonator)
     * 尺寸: 9×11×9 (891 方块)
     * 推荐维度: 末地
     */
    public static MultiblockDetector.MultiblockPattern createCosmicResonator() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层 9×9
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                structure.put(new BlockPos(x, 0, z), ANCIENT_ALLOY_BLOCK);
            }
        }
        
        // 中层双环
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                    structure.put(new BlockPos(x, 1, z), DIMENSION_CRYSTAL);
                    structure.put(new BlockPos(x, 2, z), DIMENSION_ANCHOR);
                }
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 1, 0), EXTRACTOR_CORE_T4);
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 7, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 10, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "extractor_t4_cosmic_resonator",
            "宇宙共鸣器",
            4,
            structure,
            List.of("factorcraft:ancient_alloy", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T5 虚空漩涡 (Void Vortex)
     * 尺寸: 11×13×11 (1573 方块)
     * 推荐维度: 末地
     */
    public static MultiblockDetector.MultiblockPattern createVoidVortex() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层 11×11
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                structure.put(new BlockPos(x, 0, z), VOID_CRYSTAL_BLOCK);
            }
        }
        
        // 多层环
        for (int y = 1; y <= 3; y++) {
            int radius = 4 - y;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 1, 0), EXTRACTOR_CORE_T5);
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 7, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 10, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 11, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 12, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "extractor_t5_void_vortex",
            "虚空漩涡",
            5,
            structure,
            List.of("factorcraft:void_crystal", DIMENSION_CRYSTAL, DIMENSION_ANCHOR, FACTOR_CAPACITOR)
        );
    }
    
    // ==================== 消耗结构蓝图 ====================
    
    /**
     * T1 灵魂燃烧器 (Soul Burner)
     * 尺寸: 3×5×3 (45 方块)
     */
    public static MultiblockDetector.MultiblockPattern createSoulBurner() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                structure.put(new BlockPos(x, 0, z), DUST_COPPER_BLOCK);
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 1, 0), CONSUMER_CORE_T1);
        structure.put(new BlockPos(-1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "consumer_t1_soul_burner",
            "灵魂燃烧器",
            1,
            structure,
            List.of("factorcraft:dust_copper_ingot", RESONANCE_COIL)
        );
    }
    
    /**
     * T2 灵魂熔炉 (Soul Furnace)
     * 尺寸: 5×7×5 (175 方块)
     */
    public static MultiblockDetector.MultiblockPattern createSoulFurnace() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                structure.put(new BlockPos(x, 0, z), SHADOW_STEEL_BLOCK);
            }
        }
        
        // 中层
        structure.put(new BlockPos(0, 1, 0), CONSUMER_CORE_T2);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    structure.put(new BlockPos(x, 1, z), RESONANCE_COIL);
                }
            }
        }
        
        // 柱子
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 6, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "consumer_t2_soul_furnace",
            "灵魂熔炉",
            2,
            structure,
            List.of("factorcraft:shadow_steel_ingot", RESONANCE_COIL, FACTOR_CAPACITOR)
        );
    }
    
    /**
     * T3 深渊吞噬者 (Abyss Devourer)
     * 尺寸: 7×9×7 (441 方块)
     */
    public static MultiblockDetector.MultiblockPattern createAbyssDevourer() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                structure.put(new BlockPos(x, 0, z), STARDUST_BLOCK);
            }
        }
        
        // 中层
        structure.put(new BlockPos(0, 1, 0), CONSUMER_CORE_T3);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    structure.put(new BlockPos(x, 1, z), DIMENSION_CRYSTAL);
                }
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 7, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 8, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "consumer_t3_abyss_devourer",
            "深渊吞噬者",
            3,
            structure,
            List.of("factorcraft:stardust_ingot", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T4 混沌裂隙 (Chaos Rift)
     * 尺寸: 9×11×9 (891 方块)
     */
    public static MultiblockDetector.MultiblockPattern createChaosRift() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                structure.put(new BlockPos(x, 0, z), ANCIENT_ALLOY_BLOCK);
            }
        }
        
        // 中层
        structure.put(new BlockPos(0, 1, 0), CONSUMER_CORE_T4);
        for (int y = 1; y <= 3; y++) {
            int radius = 3 - (y - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 7, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 10, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "consumer_t4_chaos_rift",
            "混沌裂隙",
            4,
            structure,
            List.of("factorcraft:ancient_alloy", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T5 永恒炉心 (Eternal Core)
     * 尺寸: 11×13×11 (1573 方块)
     */
    public static MultiblockDetector.MultiblockPattern createEternalCore() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        // 底层
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                structure.put(new BlockPos(x, 0, z), VOID_CRYSTAL_BLOCK);
            }
        }
        
        // 多层
        structure.put(new BlockPos(0, 1, 0), CONSUMER_CORE_T5);
        for (int y = 1; y <= 4; y++) {
            int radius = 4 - (y - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        // 核心
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 7, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 10, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 11, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 12, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "consumer_t5_eternal_core",
            "永恒炉心",
            5,
            structure,
            List.of("factorcraft:void_crystal", DIMENSION_CRYSTAL, DIMENSION_ANCHOR, FACTOR_CAPACITOR)
        );
    }
    
    // ==================== 合成结构蓝图 ====================
    
    /**
     * T1 远古合成阵 (Ancient Synthesis)
     */
    public static MultiblockDetector.MultiblockPattern createAncientSynthesis() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                structure.put(new BlockPos(x, 0, z), DUST_COPPER_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), SYNTHESIZER_CORE_T1);
        structure.put(new BlockPos(-1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 1, -1), RESONANCE_COIL);
        structure.put(new BlockPos(0, 1, 1), RESONANCE_COIL);
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "synthesizer_t1_ancient_synthesis",
            "远古合成阵",
            1,
            structure,
            List.of("factorcraft:dust_copper_ingot", RESONANCE_COIL)
        );
    }
    
    /**
     * T2 远古锻造台 (Ancient Forge)
     */
    public static MultiblockDetector.MultiblockPattern createAncientForge() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                structure.put(new BlockPos(x, 0, z), SHADOW_STEEL_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), SYNTHESIZER_CORE_T2);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    structure.put(new BlockPos(x, 1, z), RESONANCE_COIL);
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 6, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "synthesizer_t2_ancient_forge",
            "远古锻造台",
            2,
            structure,
            List.of("factorcraft:shadow_steel_ingot", RESONANCE_COIL, FACTOR_CAPACITOR)
        );
    }
    
    /**
     * T3 命运铸造炉 (Fate Foundry)
     */
    public static MultiblockDetector.MultiblockPattern createFateFoundry() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                structure.put(new BlockPos(x, 0, z), STARDUST_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), SYNTHESIZER_CORE_T3);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    structure.put(new BlockPos(x, 1, z), DIMENSION_CRYSTAL);
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 7, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 8, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "synthesizer_t3_fate_foundry",
            "命运铸造炉",
            3,
            structure,
            List.of("factorcraft:stardust_ingot", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T4 创世熔炉 (Creation Furnace)
     */
    public static MultiblockDetector.MultiblockPattern createCreationFurnace() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                structure.put(new BlockPos(x, 0, z), ANCIENT_ALLOY_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), SYNTHESIZER_CORE_T4);
        for (int y = 1; y <= 3; y++) {
            int radius = 3 - (y - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 7, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 10, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "synthesizer_t4_creation_furnace",
            "创世熔炉",
            4,
            structure,
            List.of("factorcraft:ancient_alloy", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T5 本源祭坛 (Origin Altar)
     */
    public static MultiblockDetector.MultiblockPattern createOriginAltar() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                structure.put(new BlockPos(x, 0, z), VOID_CRYSTAL_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), SYNTHESIZER_CORE_T5);
        for (int y = 1; y <= 4; y++) {
            int radius = 4 - (y - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 7, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 10, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 11, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 12, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "synthesizer_t5_origin_altar",
            "本源祭坛",
            5,
            structure,
            List.of("factorcraft:void_crystal", DIMENSION_CRYSTAL, DIMENSION_ANCHOR, FACTOR_CAPACITOR)
        );
    }
    
    // ==================== 培育结构蓝图 ====================
    
    /**
     * T1 命运织机 (Fate Loom)
     */
    public static MultiblockDetector.MultiblockPattern createFateLoom() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                structure.put(new BlockPos(x, 0, z), DUST_COPPER_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), BREEDER_CORE_T1);
        structure.put(new BlockPos(-1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(1, 1, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 1, -1), RESONANCE_COIL);
        structure.put(new BlockPos(0, 1, 1), RESONANCE_COIL);
        structure.put(new BlockPos(0, 2, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "breeder_t1_fate_loom",
            "命运织机",
            1,
            structure,
            List.of("factorcraft:dust_copper_ingot", RESONANCE_COIL)
        );
    }
    
    /**
     * T2 灵魂编织器 (Soul Weaver)
     */
    public static MultiblockDetector.MultiblockPattern createSoulWeaver() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                structure.put(new BlockPos(x, 0, z), SHADOW_STEEL_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), BREEDER_CORE_T2);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x != 0 || z != 0) {
                    structure.put(new BlockPos(x, 1, z), RESONANCE_COIL);
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 6, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "breeder_t2_soul_weaver",
            "灵魂编织器",
            2,
            structure,
            List.of("factorcraft:shadow_steel_ingot", RESONANCE_COIL, FACTOR_CAPACITOR)
        );
    }
    
    /**
     * T3 命运祭坛 (Fate Altar)
     */
    public static MultiblockDetector.MultiblockPattern createFateAltar() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                structure.put(new BlockPos(x, 0, z), STARDUST_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), BREEDER_CORE_T3);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    structure.put(new BlockPos(x, 1, z), DIMENSION_CRYSTAL);
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 7, 0), RESONANCE_COIL);
        structure.put(new BlockPos(0, 8, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "breeder_t3_fate_altar",
            "命运祭坛",
            3,
            structure,
            List.of("factorcraft:stardust_ingot", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T4 命运圣所 (Fate Sanctuary)
     */
    public static MultiblockDetector.MultiblockPattern createFateSanctuary() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                structure.put(new BlockPos(x, 0, z), ANCIENT_ALLOY_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), BREEDER_CORE_T4);
        for (int y = 1; y <= 3; y++) {
            int radius = 3 - (y - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 7, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 10, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "breeder_t4_fate_sanctuary",
            "命运圣所",
            4,
            structure,
            List.of("factorcraft:ancient_alloy", DIMENSION_CRYSTAL, DIMENSION_ANCHOR)
        );
    }
    
    /**
     * T5 轮回之门 (Reincarnation Gate)
     */
    public static MultiblockDetector.MultiblockPattern createReincarnationGate() {
        Map<BlockPos, String> structure = new LinkedHashMap<>();
        
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                structure.put(new BlockPos(x, 0, z), VOID_CRYSTAL_BLOCK);
            }
        }
        
        structure.put(new BlockPos(0, 1, 0), BREEDER_CORE_T5);
        for (int y = 1; y <= 4; y++) {
            int radius = 4 - (y - 1);
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        structure.put(new BlockPos(x, y, z), DIMENSION_ANCHOR);
                    }
                }
            }
        }
        
        structure.put(new BlockPos(0, 2, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 3, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 4, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 5, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 6, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 7, 0), FACTOR_CAPACITOR);
        structure.put(new BlockPos(0, 8, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 9, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 10, 0), DIMENSION_CRYSTAL);
        structure.put(new BlockPos(0, 11, 0), DIMENSION_ANCHOR);
        structure.put(new BlockPos(0, 12, 0), FACTOR_METER);
        
        return new MultiblockDetector.MultiblockPattern(
            "breeder_t5_reincarnation_gate",
            "轮回之门",
            5,
            structure,
            List.of("factorcraft:void_crystal", DIMENSION_CRYSTAL, DIMENSION_ANCHOR, FACTOR_CAPACITOR)
        );
    }
    
    // ==================== 获取所有蓝图 ====================
    
    /**
     * 获取所有 20 种科技树蓝图
     */
    public static List<MultiblockDetector.MultiblockPattern> getAllTechnologyPatterns() {
        return List.of(
            // 提取结构 T1-T5
            createStarCollector(),
            createStarArray(),
            createNebulaSiphon(),
            createCosmicResonator(),
            createVoidVortex(),
            
            // 消耗结构 T1-T5
            createSoulBurner(),
            createSoulFurnace(),
            createAbyssDevourer(),
            createChaosRift(),
            createEternalCore(),
            
            // 合成结构 T1-T5
            createAncientSynthesis(),
            createAncientForge(),
            createFateFoundry(),
            createCreationFurnace(),
            createOriginAltar(),
            
            // 培育结构 T1-T5
            createFateLoom(),
            createSoulWeaver(),
            createFateAltar(),
            createFateSanctuary(),
            createReincarnationGate()
        );
    }
    
    /**
     * 按类型获取蓝图
     */
    public static List<MultiblockDetector.MultiblockPattern> getPatternsByType(StructureType type) {
        return getAllTechnologyPatterns().stream()
            .filter(p -> StructureType.fromPatternId(p.getId()) == type)
            .toList();
    }
    
    /**
     * 按 Tier 获取蓝图
     */
    public static List<MultiblockDetector.MultiblockPattern> getPatternsByTier(int tier) {
        return getAllTechnologyPatterns().stream()
            .filter(p -> p.getTier() == tier)
            .toList();
    }
    
    /**
     * 根据类型和 Tier 获取蓝图
     */
    public static MultiblockDetector.MultiblockPattern getPattern(StructureType type, int tier) {
        return getAllTechnologyPatterns().stream()
            .filter(p -> StructureType.fromPatternId(p.getId()) == type && p.getTier() == tier)
            .findFirst()
            .orElse(null);
    }
}