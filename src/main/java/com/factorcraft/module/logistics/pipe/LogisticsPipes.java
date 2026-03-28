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
    
    public static final Block ADVANCED_PIPE = new AdvancedFactorPipeBlock();
    public static final Block PRIORITY_PIPE = new PriorityPipeBlock();
    public static final Block FILTER_PIPE = new FilterPipeBlock();
    public static final Block ONE_WAY_PIPE = new OneWayPipeBlock();
    
    public static BlockEntityType<AdvancedFactorPipeBlockEntity> ADVANCED_PIPE_ENTITY;
    public static BlockEntityType<PriorityPipeBlockEntity> PRIORITY_PIPE_ENTITY;
    public static BlockEntityType<FilterPipeBlockEntity> FILTER_PIPE_ENTITY;
    public static BlockEntityType<OneWayPipeBlockEntity> ONE_WAY_PIPE_ENTITY;
    
    public static void register() {
        // 注册方块
        registerBlock("advanced_pipe", ADVANCED_PIPE);
        registerBlock("priority_pipe", PRIORITY_PIPE);
        registerBlock("filter_pipe", FILTER_PIPE);
        registerBlock("one_way_pipe", ONE_WAY_PIPE);
        
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
    
    private static void registerBlock(String name, Block block) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("factorcraft", name));
        Registry.register(Registries.BLOCK, key, block);
    }
}
