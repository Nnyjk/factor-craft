package com.factorcraft.module.technology.machine;

import com.factorcraft.module.technology.TechnologyModule;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 机器注册
 */
public class ModMachines {
    
    public static final BlockEntityType<FactorExtractorCoreBlockEntity> FACTOR_EXTRACTOR_CORE;
    public static final BlockEntityType<FactorEmitterCoreBlockEntity> FACTOR_EMITTER_CORE;
    public static final BlockEntityType<FactorUtilizerCoreBlockEntity> FACTOR_UTILIZER_CORE;
    
    static {
        // 使用 FabricBlockEntityTypeBuilder (Fabric 1.21.4 最佳实践)
        // TODO: 需要传入对应的 Block
        FACTOR_EXTRACTOR_CORE = null;
        FACTOR_EMITTER_CORE = null;
        FACTOR_UTILIZER_CORE = null;
    }
    
    public static void register() {
        // TODO: 使用 FabricBlockEntityTypeBuilder 创建并注册
        // 需要先创建对应的 Block
    }
}
