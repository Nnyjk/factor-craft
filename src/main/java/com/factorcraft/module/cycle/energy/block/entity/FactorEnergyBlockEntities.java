package com.factorcraft.module.cycle.energy.block.entity;

import com.factorcraft.FactorCraftMod;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Factor 能源模块 BlockEntity 类型注册
 * 
 * Fabric 1.21.4 最佳实践：
 * - 使用 FabricBlockEntityTypeBuilder 创建 BlockEntityType
 * - 使用 Registry.register 注册
 */
public class FactorEnergyBlockEntities {
    
    /**
     * 初始化并注册所有 BlockEntity 类型
     */
    public static void init() {
        FactorCrystalBlockEntity.init();
        FactorPumpBlockEntity.init();
        
        FactorCraftMod.LOGGER.info("Factor Energy BlockEntities registered");
    }
}
