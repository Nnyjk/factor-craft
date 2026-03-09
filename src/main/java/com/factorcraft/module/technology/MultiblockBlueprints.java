package com.factorcraft.module.technology;

import net.minecraft.util.math.BlockPos;
import java.util.Map;
import java.util.List;

/**
 * 12 种多方块结构蓝图
 */
public class MultiblockBlueprints {
    
    /**
     * T1 吸收结构：基础共振炉
     */
    public static MultiblockDetector.MultiblockPattern basicResonanceFurnace() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_sink",
            new BlockPos(-1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(0, 0, -1), "factorcraft:resonance_coil",
            new BlockPos(0, 0, 1), "factorcraft:resonance_coil",
            new BlockPos(0, 1, 0), "factorcraft:factor_meter"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "basic_resonance_furnace", "基础共振炉", 1, structure,
            List.of("factorcraft:copper_ingot", "factorcraft:bronze_ingot")
        );
    }
    
    /**
     * T2 吸收结构：维度结晶器
     */
    public static MultiblockDetector.MultiblockPattern dimensionCrystallizer() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_sink_t2",
            new BlockPos(-1, 0, 0), "factorcraft:dimension_module",
            new BlockPos(1, 0, 0), "factorcraft:dimension_module",
            new BlockPos(0, 0, -1), "factorcraft:dimension_module",
            new BlockPos(0, 0, 1), "factorcraft:dimension_module",
            new BlockPos(0, 1, 0), "factorcraft:fissure_projector"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "dimension_crystallizer", "维度结晶器", 2, structure,
            List.of("factorcraft:iron_ingot", "factorcraft:nether_steel")
        );
    }
    
    /**
     * T3 吸收结构：远古合成阵
     */
    public static MultiblockDetector.MultiblockPattern ancientSynthesizer() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_sink_t3",
            new BlockPos(-1, 0, 0), "factorcraft:cross_dimension_core",
            new BlockPos(1, 0, 0), "factorcraft:cross_dimension_core",
            new BlockPos(0, 0, -1), "factorcraft:cross_dimension_core",
            new BlockPos(0, 0, 1), "factorcraft:cross_dimension_core",
            new BlockPos(0, 1, 0), "factorcraft:world_stabilizer"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "ancient_synthesizer", "远古合成阵", 3, structure,
            List.of("factorcraft:cobalt_ingot", "factorcraft:ardite_ingot")
        );
    }
    
    /**
     * T4 吸收结构：仲裁反应堆
     */
    public static MultiblockDetector.MultiblockPattern arbiterReactor() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_sink_t4",
            new BlockPos(-1, 0, 0), "factorcraft:arbiter_core",
            new BlockPos(1, 0, 0), "factorcraft:arbiter_core",
            new BlockPos(0, 0, -1), "factorcraft:arbiter_core",
            new BlockPos(0, 0, 1), "factorcraft:arbiter_core",
            new BlockPos(0, 1, 0), "factorcraft:tidereactor"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "arbiter_reactor", "仲裁反应堆", 4, structure,
            List.of("factorcraft:ancient_alloy", "factorcraft:starcite")
        );
    }
    
    /**
     * T1 释放结构：基础共振器
     */
    public static MultiblockDetector.MultiblockPattern basicResonator() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_source",
            new BlockPos(-1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(0, 0, -1), "factorcraft:resonance_coil",
            new BlockPos(0, 0, 1), "factorcraft:resonance_coil"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "basic_resonator", "基础共振器", 1, structure,
            List.of("factorcraft:copper_ingot", "minecraft:coal")
        );
    }
    
    /**
     * T2 释放结构：能量分解机
     */
    public static MultiblockDetector.MultiblockPattern energyDecomposer() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_source_t2",
            new BlockPos(-1, 0, 0), "factorcraft:energy_conduit",
            new BlockPos(1, 0, 0), "factorcraft:energy_conduit",
            new BlockPos(0, 0, -1), "factorcraft:energy_conduit",
            new BlockPos(0, 0, 1), "factorcraft:energy_conduit",
            new BlockPos(0, 1, 0), "factorcraft:resonator"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "energy_decomposer", "能量分解机", 2, structure,
            List.of("factorcraft:iron_ingot", "minecraft:redstone")
        );
    }
    
    /**
     * T3 释放结构：物质转化炉
     */
    public static MultiblockDetector.MultiblockPattern matterTransmuter() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_source_t3",
            new BlockPos(-1, 0, 0), "factorcraft:cross_dimension_core",
            new BlockPos(1, 0, 0), "factorcraft:cross_dimension_core",
            new BlockPos(0, 0, -1), "factorcraft:cross_dimension_core",
            new BlockPos(0, 0, 1), "factorcraft:cross_dimension_core",
            new BlockPos(0, 1, 0), "factorcraft:energy_decomposer"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "matter_transmuter", "物质转化炉", 3, structure,
            List.of("factorcraft:cobalt_ingot", "minecraft:diamond")
        );
    }
    
    /**
     * T4 释放结构：维度裂变器
     */
    public static MultiblockDetector.MultiblockPattern dimensionFissurer() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_source_t4",
            new BlockPos(-1, 0, 0), "factorcraft:arbiter_core",
            new BlockPos(1, 0, 0), "factorcraft:arbiter_core",
            new BlockPos(0, 0, -1), "factorcraft:arbiter_core",
            new BlockPos(0, 0, 1), "factorcraft:arbiter_core",
            new BlockPos(0, 1, 0), "factorcraft:matter_transmuter"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "dimension_fissurer", "维度裂变器", 4, structure,
            List.of("factorcraft:ancient_alloy", "factorcraft:starcite")
        );
    }
    
    /**
     * T1 传递器：基础传递器
     */
    public static MultiblockDetector.MultiblockPattern basicTransmitter() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_transmitter",
            new BlockPos(-1, 0, 0), "factorcraft:resonance_coil",
            new BlockPos(1, 0, 0), "factorcraft:resonance_coil"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "basic_transmitter", "基础传递器", 1, structure,
            List.of("factorcraft:bronze_ingot")
        );
    }
    
    /**
     * T2 传递器：维度传递器
     */
    public static MultiblockDetector.MultiblockPattern dimensionTransmitter() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_transmitter_t2",
            new BlockPos(-1, 0, 0), "factorcraft:dimension_module",
            new BlockPos(1, 0, 0), "factorcraft:dimension_module"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "dimension_transmitter", "维度传递器", 2, structure,
            List.of("factorcraft:nether_steel")
        );
    }
    
    /**
     * T3 传递器：远古传递器
     */
    public static MultiblockDetector.MultiblockPattern ancientTransmitter() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_transmitter_t3",
            new BlockPos(-1, 0, 0), "factorcraft:cross_dimension_core",
            new BlockPos(1, 0, 0), "factorcraft:cross_dimension_core"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "ancient_transmitter", "远古传递器", 3, structure,
            List.of("factorcraft:cobalt_ingot")
        );
    }
    
    /**
     * T4 传递器：仲裁传递器
     */
    public static MultiblockDetector.MultiblockPattern arbiterTransmitter() {
        Map<BlockPos, String> structure = Map.of(
            new BlockPos(0, 0, 0), "factorcraft:factor_transmitter_t4",
            new BlockPos(-1, 0, 0), "factorcraft:arbiter_core",
            new BlockPos(1, 0, 0), "factorcraft:arbiter_core"
        );
        
        return new MultiblockDetector.MultiblockPattern(
            "arbiter_transmitter", "仲裁传递器", 4, structure,
            List.of("factorcraft:ancient_alloy")
        );
    }
    
    /**
     * 获取所有 12 种蓝图
     */
    public static List<MultiblockDetector.MultiblockPattern> getAllBlueprints() {
        return List.of(
            basicResonanceFurnace(),
            dimensionCrystallizer(),
            ancientSynthesizer(),
            arbiterReactor(),
            basicResonator(),
            energyDecomposer(),
            matterTransmuter(),
            dimensionFissurer(),
            basicTransmitter(),
            dimensionTransmitter(),
            ancientTransmitter(),
            arbiterTransmitter()
        );
    }
}
