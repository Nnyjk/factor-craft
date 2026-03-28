package com.factorcraft.module.cycle.automation.block.entity;

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
    
    /**
     * 初始化并注册所有 BlockEntity 类型
     */
    public static void init() {
        AutoCrafterBlockEntity.init();
        AutoHarvesterBlockEntity.init();
        AutoDistributorBlockEntity.init();
        SystemControllerBlockEntity.init();
    }
}
