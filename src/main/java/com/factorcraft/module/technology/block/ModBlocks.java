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
 * 命名规范: factor_{group}_{name}_{tier}
 * - machine: 有 BlockEntity 的机器方块
 * - block: 静态方块
 * 
 * 注意：机器核心方块在 ModMachines 中注册
 */
public class ModBlocks {
    
    private static final String MOD_ID = "factorcraft";
    
    // ========== 传输系统 (machine) ==========
    // 在 ModMachines 中注册 BlockEntity
    
    public static final Block CONDUIT_T1 = registerMachine("factor_machine_conduit_t1", 2.0f);
    public static final Block CONDUIT_T2 = registerMachine("factor_machine_conduit_t2", 3.0f);
    public static final Block CONDUIT_T3 = registerMachine("factor_machine_conduit_t3", 4.0f);
    public static final Block CONDUIT_T4 = registerMachine("factor_machine_conduit_t4", 5.0f);
    public static final Block CONDUIT_T5 = registerMachine("factor_machine_conduit_t5", 6.0f);
    
    public static final Block TANK = registerMachine("factor_machine_tank", 3.0f);
    public static final Block PUMP = registerMachine("factor_machine_pump", 3.0f);
    
    // ========== 特性方块 (block) ==========
    
    public static final Block TRAIT_SHARP = registerBlock("factor_block_trait_sharp", 3.0f);
    public static final Block TRAIT_STURDY = registerBlock("factor_block_trait_sturdy", 3.0f);
    public static final Block TRAIT_PROTECTIVE = registerBlock("factor_block_trait_protective", 3.0f);
    public static final Block TRAIT_ENERGETIC = registerBlock("factor_block_trait_energetic", 3.0f);
    public static final Block TRAIT_CATALYTIC = registerBlock("factor_block_trait_catalytic", 3.0f);
    public static final Block TRAIT_STABILIZING = registerBlock("factor_block_trait_stabilizing", 3.0f);
    
    // ========== 建筑方块 (block) ==========
    
    public static final Block BUILDING_T1 = registerBlock("factor_block_building_t1", 1.5f);
    public static final Block BUILDING_T2 = registerBlock("factor_block_building_t2", 3.0f);
    public static final Block BUILDING_T3 = registerBlock("factor_block_building_t3", 4.0f);
    public static final Block BUILDING_T4 = registerBlock("factor_block_building_t4", 5.0f);
    public static final Block BUILDING_T5 = registerBlock("factor_block_building_t5", 6.0f);
    
    // ========== 其他方块 (block) ==========
    
    public static final Block ANCHOR = registerBlock("factor_block_anchor", 3.0f);
    
    /**
     * 注册静态方块
     */
    private static Block registerBlock(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new Block(AbstractBlock.Settings.create()
            .registryKey(key)
            .strength(hardness));
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册机器方块（仅方块，BlockEntity 在 ModMachines 中注册）
     */
    private static Block registerMachine(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new Block(AbstractBlock.Settings.create()
            .registryKey(key)
            .strength(hardness));
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    public static void register() {
        // 静态初始化时已完成注册
    }
}