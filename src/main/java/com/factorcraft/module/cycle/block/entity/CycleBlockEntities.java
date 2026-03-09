package com.factorcraft.module.cycle.block.entity;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.block.CycleBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Cycle 模块 BlockEntity 类型注册
 * 
 * Fabric 1.21.4 最佳实践：
 * - 使用 FabricBlockEntityTypeBuilder 创建 BlockEntityType
 * - 使用 Registry.register 注册
 */
public class CycleBlockEntities {
    
    public static final BlockEntityType<FactorSinkBlockEntity> FACTOR_SINK;
    public static final BlockEntityType<FactorSourceBlockEntity> FACTOR_SOURCE;
    public static final BlockEntityType<FactorTransmitterBlockEntity> FACTOR_TRANSMITTER;
    
    static {
        FACTOR_SINK = FabricBlockEntityTypeBuilder.create(
            FactorSinkBlockEntity::new,
            CycleBlocks.getFactorSink()
        ).build(null);
        
        FACTOR_SOURCE = FabricBlockEntityTypeBuilder.create(
            FactorSourceBlockEntity::new,
            CycleBlocks.getFactorSource()
        ).build(null);
        
        FACTOR_TRANSMITTER = FabricBlockEntityTypeBuilder.create(
            FactorTransmitterBlockEntity::new,
            CycleBlocks.getFactorTransmitter()
        ).build(null);
    }
    
    /**
     * 注册所有 BlockEntity 类型
     */
    public static void register() {
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_sink"),
            FACTOR_SINK
        );
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_source"),
            FACTOR_SOURCE
        );
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_transmitter"),
            FACTOR_TRANSMITTER
        );
    }
}
