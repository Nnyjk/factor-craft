package com.factorcraft.module.technology.machine;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.technology.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 机器核心方块与 BlockEntity 注册
 * 
 * 命名规范: factor_machine_{type}_core_{tier}
 */
public class ModMachines {
    
    private static final String MOD_ID = "factorcraft";
    
    // ========== 提取核心 T1-T5 ==========
    public static final Block EXTRACTOR_CORE_T1 = registerExtractorCore("factor_machine_extractor_core_t1", 3.0f);
    public static final Block EXTRACTOR_CORE_T2 = registerExtractorCore("factor_machine_extractor_core_t2", 3.5f);
    public static final Block EXTRACTOR_CORE_T3 = registerExtractorCore("factor_machine_extractor_core_t3", 4.0f);
    public static final Block EXTRACTOR_CORE_T4 = registerExtractorCore("factor_machine_extractor_core_t4", 4.5f);
    public static final Block EXTRACTOR_CORE_T5 = registerExtractorCore("factor_machine_extractor_core_t5", 5.0f);
    
    // ========== 消耗核心 T1-T5 ==========
    public static final Block CONSUMER_CORE_T1 = registerConsumerCore("factor_machine_consumer_core_t1", 3.0f);
    public static final Block CONSUMER_CORE_T2 = registerConsumerCore("factor_machine_consumer_core_t2", 3.5f);
    public static final Block CONSUMER_CORE_T3 = registerConsumerCore("factor_machine_consumer_core_t3", 4.0f);
    public static final Block CONSUMER_CORE_T4 = registerConsumerCore("factor_machine_consumer_core_t4", 4.5f);
    public static final Block CONSUMER_CORE_T5 = registerConsumerCore("factor_machine_consumer_core_t5", 5.0f);
    
    // ========== 合成核心 T1-T5 ==========
    public static final Block SYNTHESIZER_CORE_T1 = registerSynthesizerCore("factor_machine_synthesizer_core_t1", 3.0f);
    public static final Block SYNTHESIZER_CORE_T2 = registerSynthesizerCore("factor_machine_synthesizer_core_t2", 3.5f);
    public static final Block SYNTHESIZER_CORE_T3 = registerSynthesizerCore("factor_machine_synthesizer_core_t3", 4.0f);
    public static final Block SYNTHESIZER_CORE_T4 = registerSynthesizerCore("factor_machine_synthesizer_core_t4", 4.5f);
    public static final Block SYNTHESIZER_CORE_T5 = registerSynthesizerCore("factor_machine_synthesizer_core_t5", 5.0f);
    
    // ========== 培育核心 T1-T5 ==========
    public static final Block CULTIVATOR_CORE_T1 = registerCultivatorCore("factor_machine_cultivator_core_t1", 3.0f);
    public static final Block CULTIVATOR_CORE_T2 = registerCultivatorCore("factor_machine_cultivator_core_t2", 3.5f);
    public static final Block CULTIVATOR_CORE_T3 = registerCultivatorCore("factor_machine_cultivator_core_t3", 4.0f);
    public static final Block CULTIVATOR_CORE_T4 = registerCultivatorCore("factor_machine_cultivator_core_t4", 4.5f);
    public static final Block CULTIVATOR_CORE_T5 = registerCultivatorCore("factor_machine_cultivator_core_t5", 5.0f);
    
    // ========== 传递器 T1-T4 ==========
    public static final Block TRANSMITTER_T1 = registerMachineBlock("factor_machine_transmitter_t1", 3.0f);
    public static final Block TRANSMITTER_T2 = registerMachineBlock("factor_machine_transmitter_t2", 3.5f);
    public static final Block TRANSMITTER_T3 = registerMachineBlock("factor_machine_transmitter_t3", 4.0f);
    public static final Block TRANSMITTER_T4 = registerMachineBlock("factor_machine_transmitter_t4", 4.5f);
    
    // ========== BlockEntity 类型 ==========
    public static BlockEntityType<ExtractorCoreBlockEntity> EXTRACTOR_CORE;
    public static BlockEntityType<ConsumerCoreBlockEntity> CONSUMER_CORE;
    public static BlockEntityType<SynthesizerCoreBlockEntity> SYNTHESIZER_CORE;
    public static BlockEntityType<CultivatorCoreBlockEntity> CULTIVATOR_CORE;
    public static BlockEntityType<TransmitterBlockEntity> TRANSMITTER;
    
    /**
     * 注册提取核心方块（带 GUI）
     */
    private static Block registerExtractorCore(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new ExtractorCoreBlock(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(hardness)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册消耗核心方块（带 GUI）
     */
    private static Block registerConsumerCore(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new ConsumerCoreBlock(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(hardness)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册合成核心方块（带 GUI）
     */
    private static Block registerSynthesizerCore(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new SynthesizerCoreBlock(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(hardness)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册培育核心方块（带 GUI）
     */
    private static Block registerCultivatorCore(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new CultivatorCoreBlock(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(hardness)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册普通机器方块（无 GUI，用于传递器等）
     */
    @SuppressWarnings("deprecation")
    private static Block registerMachineBlock(String name, float hardness) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        Block block = new net.minecraft.block.Block(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(hardness)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        return block;
    }
    
    /**
     * 注册所有 BlockEntity
     */
    public static void register() {
        // 提取核心 BlockEntity
        EXTRACTOR_CORE = FabricBlockEntityTypeBuilder.create(
            ExtractorCoreBlockEntity::new,
            EXTRACTOR_CORE_T1, EXTRACTOR_CORE_T2, EXTRACTOR_CORE_T3, EXTRACTOR_CORE_T4, EXTRACTOR_CORE_T5
        ).build(null);
        
        // 消耗核心 BlockEntity
        CONSUMER_CORE = FabricBlockEntityTypeBuilder.create(
            ConsumerCoreBlockEntity::new,
            CONSUMER_CORE_T1, CONSUMER_CORE_T2, CONSUMER_CORE_T3, CONSUMER_CORE_T4, CONSUMER_CORE_T5
        ).build(null);
        
        // 合成核心 BlockEntity
        SYNTHESIZER_CORE = FabricBlockEntityTypeBuilder.create(
            SynthesizerCoreBlockEntity::new,
            SYNTHESIZER_CORE_T1, SYNTHESIZER_CORE_T2, SYNTHESIZER_CORE_T3, SYNTHESIZER_CORE_T4, SYNTHESIZER_CORE_T5
        ).build(null);
        
        // 培育核心 BlockEntity
        CULTIVATOR_CORE = FabricBlockEntityTypeBuilder.create(
            CultivatorCoreBlockEntity::new,
            CULTIVATOR_CORE_T1, CULTIVATOR_CORE_T2, CULTIVATOR_CORE_T3, CULTIVATOR_CORE_T4, CULTIVATOR_CORE_T5
        ).build(null);
        
        // 传递器 BlockEntity
        TRANSMITTER = FabricBlockEntityTypeBuilder.create(
            TransmitterBlockEntity::new,
            TRANSMITTER_T1, TRANSMITTER_T2, TRANSMITTER_T3, TRANSMITTER_T4
        ).build(null);
        
        // 注册 BlockEntity 类型
        Registry.register(Registries.BLOCK_ENTITY_TYPE, 
            Identifier.of(MOD_ID, "extractor_core"), EXTRACTOR_CORE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, 
            Identifier.of(MOD_ID, "consumer_core"), CONSUMER_CORE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, 
            Identifier.of(MOD_ID, "synthesizer_core"), SYNTHESIZER_CORE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, 
            Identifier.of(MOD_ID, "cultivator_core"), CULTIVATOR_CORE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, 
            Identifier.of(MOD_ID, "transmitter"), TRANSMITTER);
        
        FactorCraftMod.LOGGER.info("[ModMachines] 已注册 24 个核心方块, 5 个 BlockEntity 类型");
    }
}