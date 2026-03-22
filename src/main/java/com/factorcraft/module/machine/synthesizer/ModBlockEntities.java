package com.factorcraft.module.machine.synthesizer;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor 合成器 BlockEntity 注册
 */
public class ModBlockEntities {
    
    // ========== BlockEntity 类型 ==========
    
    public static final BlockEntityType<SynthesizerBlockEntity> FACTOR_SYNTHESIZER = 
        FabricBlockEntityTypeBuilder.create(SynthesizerBlockEntity::new, ModBlocks.FACTOR_SYNTHESIZER)
            .build();
    
    // ========== 注册方法 ==========
    
    public static void init() {
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(FactorCraftMod.MOD_ID, "factor_synthesizer"),
            FACTOR_SYNTHESIZER
        );
    }
}