package com.factorcraft.module.technology.machine;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.technology.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 机器 BlockEntity 注册
 */
public class ModMachines {
    
    public static BlockEntityType<FactorExtractorCoreBlockEntity> FACTOR_EXTRACTOR_CORE;
    public static BlockEntityType<FactorEmitterCoreBlockEntity> FACTOR_EMITTER_CORE;
    public static BlockEntityType<FactorUtilizerCoreBlockEntity> FACTOR_UTILIZER_CORE;
    
    /**
     * 注册所有 BlockEntity
     * 注意：必须在 ModBlocks.register() 之后调用
     */
    public static void register() {
        // 使用 FabricBlockEntityTypeBuilder 创建 BlockEntityType
        FACTOR_EXTRACTOR_CORE = FabricBlockEntityTypeBuilder.create(
            FactorExtractorCoreBlockEntity::new,
            ModBlocks.FACTOR_EXTRACTOR_CORE
        ).build(null);
        
        FACTOR_EMITTER_CORE = FabricBlockEntityTypeBuilder.create(
            FactorEmitterCoreBlockEntity::new,
            ModBlocks.FACTOR_EMITTER_CORE
        ).build(null);
        
        FACTOR_UTILIZER_CORE = FabricBlockEntityTypeBuilder.create(
            FactorUtilizerCoreBlockEntity::new,
            ModBlocks.FACTOR_UTILIZER_CORE
        ).build(null);
        
        // 注册到 Registry
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_extractor_core"),
            FACTOR_EXTRACTOR_CORE
        );
        
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_emitter_core"),
            FACTOR_EMITTER_CORE
        );
        
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_utilizer_core"),
            FACTOR_UTILIZER_CORE
        );
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Machine] 已注册 3 个 BlockEntity 类型");
    }
}