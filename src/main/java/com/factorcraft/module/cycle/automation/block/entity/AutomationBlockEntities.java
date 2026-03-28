package com.factorcraft.module.cycle.automation.block.entity;

import com.factorcraft.module.cycle.automation.block.AutomationBlocks;
import com.factorcraft.module.cycle.automation.block.entity.crafter.AutoCrafterBlockEntity;
import com.factorcraft.module.cycle.automation.block.entity.distributor.AutoDistributorBlockEntity;
import com.factorcraft.module.cycle.automation.block.entity.harvester.AutoHarvesterBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * 自动化模块 BlockEntity 注册
 */
public class AutomationBlockEntities {
    
    // 自动合成器 BlockEntity
    public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER = registerBlockEntity(
        "auto_crafter",
        AutoCrafterBlockEntity::new,
        AutomationBlocks.AUTO_CRAFTER
    );
    
    // 自动收割机 BlockEntity
    public static final BlockEntityType<AutoHarvesterBlockEntity> AUTO_HARVESTER = registerBlockEntity(
        "auto_harvester",
        AutoHarvesterBlockEntity::new,
        AutomationBlocks.AUTO_HARVESTER
    );
    
    // 自动分配器 BlockEntity
    public static final BlockEntityType<AutoDistributorBlockEntity> AUTO_DISTRIBUTOR = registerBlockEntity(
        "auto_distributor",
        AutoDistributorBlockEntity::new,
        AutomationBlocks.AUTO_DISTRIBUTOR
    );
    
    /**
     * 注册 BlockEntity 类型
     */
    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(
        String name,
        BlockEntityType.BlockEntityFactory<T> factory,
        net.minecraft.block.Block... blocks
    ) {
        Identifier id = Identifier.of("factorcraft", name);
        RegistryKey<BlockEntityType<?>> key = RegistryKey.of(RegistryKeys.BLOCK_ENTITY_TYPE, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, key, BlockEntityType.Builder.create(factory, blocks).build());
    }
    
    /**
     * 初始化所有 BlockEntity 注册
     */
    public static void init() {
        // 类加载时自动注册
    }
}
