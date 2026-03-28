package com.factorcraft.module.cycle.automation.endgame.init;

import com.factorcraft.module.cycle.automation.endgame.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 终局自动化 BlockEntity 注册表
 */
public class EndgameAutomationBlockEntities {
    
    public static final String MOD_ID = "factorcraft";
    
    // 自动提取器 MK-II
    public static final BlockEntityType<AutoExtractorMK2BlockEntity> AUTO_EXTRACTOR_MK2 = FabricBlockEntityTypeBuilder.create(
        AutoExtractorMK2BlockEntity::new,
        EndgameAutomationBlocks.AUTO_EXTRACTOR_MK2
    ).build(null);
    
    // Factor 泵 MK-II
    public static final BlockEntityType<FactorPumpMK2BlockEntity> FACTOR_PUMP_MK2 = FabricBlockEntityTypeBuilder.create(
        FactorPumpMK2BlockEntity::new,
        EndgameAutomationBlocks.FACTOR_PUMP_MK2
    ).build(null);
    
    // 高级合成器
    public static final BlockEntityType<AdvancedCrafterBlockEntity> ADVANCED_CRAFTER = FabricBlockEntityTypeBuilder.create(
        AdvancedCrafterBlockEntity::new,
        EndgameAutomationBlocks.ADVANCED_CRAFTER
    ).build(null);
    
    // 量子仓储单元
    public static final BlockEntityType<QuantumStorageBlockEntity> QUANTUM_STORAGE = FabricBlockEntityTypeBuilder.create(
        QuantumStorageBlockEntity::new,
        EndgameAutomationBlocks.QUANTUM_STORAGE
    ).build(null);
    
    public static void init() {
        // 注册 BlockEntity 类型
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "auto_extractor_mk2"), AUTO_EXTRACTOR_MK2);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "factor_pump_mk2"), FACTOR_PUMP_MK2);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "advanced_crafter"), ADVANCED_CRAFTER);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "quantum_storage"), QUANTUM_STORAGE);
    }
}
