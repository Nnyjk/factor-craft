package com.factorcraft.module.machine.extractor;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 提取器相关方块实体类型注册
 */
public class ModBlockEntities {
    
    private static final String MOD_ID = FactorCraftMod.MOD_ID;
    
    // 提取器 T1-T3 (延迟初始化，在 register() 中赋值)
    public static BlockEntityType<ExtractorBlockEntity> EXTRACTOR_T1;
    public static BlockEntityType<ExtractorBlockEntity> EXTRACTOR_T2;
    public static BlockEntityType<ExtractorBlockEntity> EXTRACTOR_T3;
    
    /**
     * 注册所有方块实体类型
     * 注意：必须在 ModBlocks.register() 之后调用
     */
    public static void register() {
        EXTRACTOR_T1 = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "extractor_t1"),
            FabricBlockEntityTypeBuilder.create(
                (pos, state) -> new ExtractorBlockEntity(pos, state, 1),
                ModBlocks.EXTRACTOR_T1
            ).build()
        );
        
        EXTRACTOR_T2 = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "extractor_t2"),
            FabricBlockEntityTypeBuilder.create(
                (pos, state) -> new ExtractorBlockEntity(pos, state, 2),
                ModBlocks.EXTRACTOR_T2
            ).build()
        );
        
        EXTRACTOR_T3 = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "extractor_t3"),
            FabricBlockEntityTypeBuilder.create(
                (pos, state) -> new ExtractorBlockEntity(pos, state, 3),
                ModBlocks.EXTRACTOR_T3
            ).build()
        );
        
        FactorCraftMod.LOGGER.info("Registering extractor block entities for " + MOD_ID);
    }
}