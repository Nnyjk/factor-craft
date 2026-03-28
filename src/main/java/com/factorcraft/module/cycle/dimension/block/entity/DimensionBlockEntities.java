package com.factorcraft.module.cycle.dimension.block.entity;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.dimension.block.DimensionBlocks;
import com.factorcraft.module.cycle.dimension.block.entity.gate.DimensionalGateBlockEntity;
import com.factorcraft.module.cycle.dimension.block.entity.gate.GateControllerBlockEntity;
import com.factorcraft.module.cycle.dimension.block.entity.nether.NetherFactorVentBlockEntity;
import com.factorcraft.module.cycle.dimension.block.entity.end.EndFactorBeaconBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 维度 BlockEntity 注册表
 */
public class DimensionBlockEntities {
    // 下界 BlockEntity
    public static final BlockEntityType<NetherFactorVentBlockEntity> NETHER_FACTOR_VENT;
    
    // 末地 BlockEntity
    public static final BlockEntityType<EndFactorBeaconBlockEntity> END_FACTOR_BEACON;
    
    // 维度传送 BlockEntity
    public static final BlockEntityType<DimensionalGateBlockEntity> DIMENSIONAL_GATE;
    public static final BlockEntityType<GateControllerBlockEntity> GATE_CONTROLLER;
    
    static {
        NETHER_FACTOR_VENT = FabricBlockEntityTypeBuilder.create(
            NetherFactorVentBlockEntity::new,
            DimensionBlocks.NETHER_FACTOR_VENT
        ).build(null);
        
        END_FACTOR_BEACON = FabricBlockEntityTypeBuilder.create(
            EndFactorBeaconBlockEntity::new,
            DimensionBlocks.END_FACTOR_BEACON
        ).build(null);
        
        DIMENSIONAL_GATE = FabricBlockEntityTypeBuilder.create(
            DimensionalGateBlockEntity::new,
            DimensionBlocks.DIMENSIONAL_GATE
        ).build(null);
        
        GATE_CONTROLLER = FabricBlockEntityTypeBuilder.create(
            GateControllerBlockEntity::new,
            DimensionBlocks.GATE_CONTROLLER
        ).build(null);
    }
    
    /**
     * 注册所有 BlockEntity 类型
     */
    public static void register() {
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "nether_factor_vent"),
            NETHER_FACTOR_VENT
        );
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "end_factor_beacon"),
            END_FACTOR_BEACON
        );
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "dimensional_gate"),
            DIMENSIONAL_GATE
        );
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "gate_controller"),
            GATE_CONTROLLER
        );
    }
}
