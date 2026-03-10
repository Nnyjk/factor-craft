package com.factorcraft.module.technology.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

/**
 * 科技模块方块注册
 */
public class ModBlocks {
    
    // 核心机器方块
    public static final Block FACTOR_EXTRACTOR_CORE = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block FACTOR_EMITTER_CORE = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block FACTOR_UTILIZER_CORE = new Block(Settings.copy(Blocks.IRON_BLOCK));
    
    // 传输系统方块
    public static final Block FACTOR_CONDUIT_T1 = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block FACTOR_CONDUIT_T2 = new Block(Settings.copy(Blocks.GOLD_BLOCK));
    public static final Block FACTOR_CONDUIT_T3 = new Block(Settings.copy(Blocks.DIAMOND_BLOCK));
    public static final Block FACTOR_CONDUIT_T4 = new Block(Settings.copy(Blocks.NETHERITE_BLOCK));
    public static final Block FACTOR_CONDUIT_T5 = new Block(Settings.copy(Blocks.EMERALD_BLOCK));
    
    public static final Block FACTOR_TANK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block FACTOR_PUMP = new Block(Settings.copy(Blocks.IRON_BLOCK));
    
    // 特性方块
    public static final Block SHARP_BLOCK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block STURDY_BLOCK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block PROTECTIVE_BLOCK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block ENERGETIC_BLOCK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block CATALYTIC_BLOCK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block STABILIZING_BLOCK = new Block(Settings.copy(Blocks.IRON_BLOCK));
    
    // 建筑方块 T1-T5
    public static final Block BUILDING_BLOCK_T1 = new Block(Settings.copy(Blocks.STONE));
    public static final Block BUILDING_BLOCK_T2 = new Block(Settings.copy(Blocks.IRON_BLOCK));
    public static final Block BUILDING_BLOCK_T3 = new Block(Settings.copy(Blocks.GOLD_BLOCK));
    public static final Block BUILDING_BLOCK_T4 = new Block(Settings.copy(Blocks.DIAMOND_BLOCK));
    public static final Block BUILDING_BLOCK_T5 = new Block(Settings.copy(Blocks.NETHERITE_BLOCK));
    
    public static void register() {
        // TODO: 注册所有方块到 Registry
    }
}
