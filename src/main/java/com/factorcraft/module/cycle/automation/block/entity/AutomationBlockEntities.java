package com.factorcraft.module.cycle.automation.block.entity;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cycle.automation.block.AutomationBlocks;
import com.factorcraft.module.cycle.automation.block.entity.crafter.AutoCrafterBlockEntity;
import com.factorcraft.module.cycle.automation.block.entity.distributor.AutoDistributorBlockEntity;
import com.factorcraft.module.cycle.automation.block.entity.harvester.AutoHarvesterBlockEntity;
import com.factorcraft.module.cycle.automation.block.entity.controller.SystemControllerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * 自动化模块 BlockEntity 类型注册
 */
public class AutomationBlockEntities {
    
    public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER;
    public static final BlockEntityType<AutoHarvesterBlockEntity> AUTO_HARVESTER;
    public static final BlockEntityType<AutoDistributorBlockEntity> AUTO_DISTRIBUTOR;
    public static final BlockEntityType<SystemControllerBlockEntity> SYSTEM_CONTROLLER;
    
    static {
        AUTO_CRAFTER = FabricBlockEntityTypeBuilder.create(AutoCrafterBlockEntity::new, AutomationBlocks.AUTO_CRAFTER).build();
        AUTO_HARVESTER = FabricBlockEntityTypeBuilder.create(AutoHarvesterBlockEntity::new, AutomationBlocks.AUTO_HARVESTER).build();
        AUTO_DISTRIBUTOR = FabricBlockEntityTypeBuilder.create(AutoDistributorBlockEntity::new, AutomationBlocks.AUTO_DISTRIBUTOR).build();
        SYSTEM_CONTROLLER = FabricBlockEntityTypeBuilder.create(SystemControllerBlockEntity::new, AutomationBlocks.SYSTEM_CONTROLLER).build();
    }
    
    /**
     * 初始化并注册所有 BlockEntity 类型
     */
    public static void init() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(FactorCraftMod.MOD_ID, "auto_crafter"), AUTO_CRAFTER);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(FactorCraftMod.MOD_ID, "auto_harvester"), AUTO_HARVESTER);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(FactorCraftMod.MOD_ID, "auto_distributor"), AUTO_DISTRIBUTOR);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(FactorCraftMod.MOD_ID, "system_controller"), SYSTEM_CONTROLLER);
        
        FactorCraftMod.LOGGER.info("Automation BlockEntities registered");
    }
}
