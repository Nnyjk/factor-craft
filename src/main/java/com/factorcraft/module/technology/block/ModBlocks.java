package com.factorcraft.module.technology.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 科技模块方块注册 - Fabric 1.21.4
 */
public class ModBlocks {
    
    // 核心机器方块
    public static final Block FACTOR_EXTRACTOR_CORE = registerBlock(
        "factor_extractor_core",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block FACTOR_EMITTER_CORE = registerBlock(
        "factor_emitter_core",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block FACTOR_UTILIZER_CORE = registerBlock(
        "factor_utilizer_core",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    
    // 传输系统方块
    public static final Block FACTOR_CONDUIT_T1 = registerBlock(
        "factor_conduit_t1",
        new Block(AbstractBlock.Settings.create().strength(2.0f))
    );
    public static final Block FACTOR_CONDUIT_T2 = registerBlock(
        "factor_conduit_t2",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block FACTOR_CONDUIT_T3 = registerBlock(
        "factor_conduit_t3",
        new Block(AbstractBlock.Settings.create().strength(4.0f))
    );
    public static final Block FACTOR_CONDUIT_T4 = registerBlock(
        "factor_conduit_t4",
        new Block(AbstractBlock.Settings.create().strength(5.0f))
    );
    public static final Block FACTOR_CONDUIT_T5 = registerBlock(
        "factor_conduit_t5",
        new Block(AbstractBlock.Settings.create().strength(6.0f))
    );
    
    public static final Block FACTOR_TANK = registerBlock(
        "factor_tank",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block FACTOR_PUMP = registerBlock(
        "factor_pump",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    
    // 特性方块
    public static final Block SHARP_BLOCK = registerBlock(
        "sharp_block",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block STURDY_BLOCK = registerBlock(
        "sturdy_block",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block PROTECTIVE_BLOCK = registerBlock(
        "protective_block",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block ENERGETIC_BLOCK = registerBlock(
        "energetic_block",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block CATALYTIC_BLOCK = registerBlock(
        "catalytic_block",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block STABILIZING_BLOCK = registerBlock(
        "stabilizing_block",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    
    // 建筑方块 T1-T5
    public static final Block BUILDING_BLOCK_T1 = registerBlock(
        "building_block_t1",
        new Block(AbstractBlock.Settings.create().strength(1.5f))
    );
    public static final Block BUILDING_BLOCK_T2 = registerBlock(
        "building_block_t2",
        new Block(AbstractBlock.Settings.create().strength(3.0f))
    );
    public static final Block BUILDING_BLOCK_T3 = registerBlock(
        "building_block_t3",
        new Block(AbstractBlock.Settings.create().strength(4.0f))
    );
    public static final Block BUILDING_BLOCK_T4 = registerBlock(
        "building_block_t4",
        new Block(AbstractBlock.Settings.create().strength(5.0f))
    );
    public static final Block BUILDING_BLOCK_T5 = registerBlock(
        "building_block_t5",
        new Block(AbstractBlock.Settings.create().strength(6.0f))
    );
    
    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of("factorcraft", name), block);
    }
    
    public static void register() {
        // 静态初始化时已注册
    }
}
