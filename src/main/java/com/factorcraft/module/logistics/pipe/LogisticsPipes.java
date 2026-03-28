package com.factorcraft.module.logistics.pipe;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 物流管道方块注册表
 */
public class LogisticsPipes {
    
    public static Block ADVANCED_PIPE;
    public static Block PRIORITY_PIPE;
    public static Block FILTER_PIPE;
    public static Block ONE_WAY_PIPE;
    
    public static BlockEntityType<AdvancedFactorPipeBlockEntity> ADVANCED_PIPE_ENTITY;
    public static BlockEntityType<PriorityPipeBlockEntity> PRIORITY_PIPE_ENTITY;
    public static BlockEntityType<FilterPipeBlockEntity> FILTER_PIPE_ENTITY;
    public static BlockEntityType<OneWayPipeBlockEntity> ONE_WAY_PIPE_ENTITY;
    
    public static void register() {
        // 创建 RegistryKey
        RegistryKey<Block> advancedPipeKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "advanced_pipe"));
        RegistryKey<Block> priorityPipeKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "priority_pipe"));
        RegistryKey<Block> filterPipeKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "filter_pipe"));
        RegistryKey<Block> oneWayPipeKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", "one_way_pipe"));
        
        // 创建并注册方块（传入 RegistryKey）
        ADVANCED_PIPE = Registry.register(Registries.BLOCK, advancedPipeKey, new AdvancedFactorPipeBlock(AdvancedFactorPipeBlock.createSettings(advancedPipeKey)));
        PRIORITY_PIPE = Registry.register(Registries.BLOCK, priorityPipeKey, new PriorityPipeBlock(PriorityPipeBlock.createSettings(priorityPipeKey)));
        FILTER_PIPE = Registry.register(Registries.BLOCK, filterPipeKey, new FilterPipeBlock(FilterPipeBlock.createSettings(filterPipeKey)));
        ONE_WAY_PIPE = Registry.register(Registries.BLOCK, oneWayPipeKey, new OneWayPipeBlock(OneWayPipeBlock.createSettings(oneWayPipeKey)));
        
        // 注册 BlockEntity
        ADVANCED_PIPE_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "advanced_pipe"),
            FabricBlockEntityTypeBuilder.create(AdvancedFactorPipeBlockEntity::new, ADVANCED_PIPE).build()
        );
        
        PRIORITY_PIPE_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "priority_pipe"),
            FabricBlockEntityTypeBuilder.create(PriorityPipeBlockEntity::new, PRIORITY_PIPE).build()
        );
        
        FILTER_PIPE_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "filter_pipe"),
            FabricBlockEntityTypeBuilder.create(FilterPipeBlockEntity::new, FILTER_PIPE).build()
        );
        
        ONE_WAY_PIPE_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("factorcraft", "one_way_pipe"),
            FabricBlockEntityTypeBuilder.create(OneWayPipeBlockEntity::new, ONE_WAY_PIPE).build()
        );
    }
}
