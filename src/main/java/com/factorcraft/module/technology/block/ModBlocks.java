package com.factorcraft.module.technology.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 科技模块方块注册 - Fabric 1.21.4
 * 
 * 注意：Fabric 1.21.4 要求方块在创建时通过 RegistryKey 关联注册表
 */
public class ModBlocks {
    
    private static final String MOD_ID = "factorcraft";
    
    // 核心机器方块
    public static final Block FACTOR_EXTRACTOR_CORE = register("factor_extractor_core", 3.0f);
    public static final Block FACTOR_EMITTER_CORE = register("factor_emitter_core", 3.0f);
    public static final Block FACTOR_UTILIZER_CORE = register("factor_utilizer_core", 3.0f);
    
    // 传输系统方块
    public static final Block FACTOR_CONDUIT_T1 = register("factor_conduit_t1", 2.0f);
    public static final Block FACTOR_CONDUIT_T2 = register("factor_conduit_t2", 3.0f);
    public static final Block FACTOR_CONDUIT_T3 = register("factor_conduit_t3", 4.0f);
    public static final Block FACTOR_CONDUIT_T4 = register("factor_conduit_t4", 5.0f);
    public static final Block FACTOR_CONDUIT_T5 = register("factor_conduit_t5", 6.0f);
    
    public static final Block FACTOR_TANK = register("factor_tank", 3.0f);
    public static final Block FACTOR_PUMP = register("factor_pump", 3.0f);
    
    // 特性方块
    public static final Block SHARP_BLOCK = register("sharp_block", 3.0f);
    public static final Block STURDY_BLOCK = register("sturdy_block", 3.0f);
    public static final Block PROTECTIVE_BLOCK = register("protective_block", 3.0f);
    public static final Block ENERGETIC_BLOCK = register("energetic_block", 3.0f);
    public static final Block CATALYTIC_BLOCK = register("catalytic_block", 3.0f);
    public static final Block STABILIZING_BLOCK = register("stabilizing_block", 3.0f);
    
    // 建筑方块 T1-T5
    public static final Block BUILDING_BLOCK_T1 = register("building_block_t1", 1.5f);
    public static final Block BUILDING_BLOCK_T2 = register("building_block_t2", 3.0f);
    public static final Block BUILDING_BLOCK_T3 = register("building_block_t3", 4.0f);
    public static final Block BUILDING_BLOCK_T4 = register("building_block_t4", 5.0f);
    public static final Block BUILDING_BLOCK_T5 = register("building_block_t5", 6.0f);
    
    /**
     * 注册方块（使用 RegistryKey 模式，符合 Fabric 1.21.4 要求）
     */
    private static Block register(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        // 创建方块时传入 RegistryKey
        Block block = new Block(AbstractBlock.Settings.create()
            .registryKey(key)
            .strength(hardness));
        
        // 注册方块
        Registry.register(Registries.BLOCK, id, block);
        
        // 注册 BlockItem
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    public static void register() {
        // 静态初始化时已完成注册
    }
}