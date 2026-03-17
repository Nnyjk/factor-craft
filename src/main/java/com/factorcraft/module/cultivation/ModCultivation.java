package com.factorcraft.module.cultivation;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.cultivation.block.CultivationCoreBlock;
import com.factorcraft.module.cultivation.blockentity.CultivationCoreBlockEntity;
import com.factorcraft.module.ui.handler.CultivationScreenHandler;
import com.factorcraft.module.ui.screen.CultivationScreen;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * 培育系统注册
 * 
 * 注册 CultivationCore 方块、BlockEntity 和 ScreenHandler
 */
public class ModCultivation {
    
    private static final String MOD_ID = "factorcraft";
    
    // ==================== 方块 ====================
    
    public static final Block CULTIVATION_CORE = registerBlock(
        "cultivation_core",
        new CultivationCoreBlock(
            AbstractBlock.Settings.create()
                .strength(3.0f)
        )
    );
    
    /**
     * 注册方块
     */
    private static Block registerBlock(String name, Block block) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        
        // 重新创建带有 registryKey 的 Block 设置
        Block registeredBlock = new CultivationCoreBlock(
            AbstractBlock.Settings.create()
                .registryKey(key)
                .strength(3.0f)
        );
        
        Registry.register(Registries.BLOCK, id, registeredBlock);
        Registry.register(Registries.ITEM, id, new BlockItem(registeredBlock, new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, id))));
        
        FactorCraftMod.LOGGER.debug("[ModCultivation] 注册方块：{}", name);
        return registeredBlock;
    }
    
    // ==================== BlockEntity ====================
    
    public static BlockEntityType<CultivationCoreBlockEntity> CULTIVATION_CORE_BLOCK_ENTITY;
    
    /**
     * 注册 BlockEntity
     */
    public static void registerBlockEntities() {
        CULTIVATION_CORE_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(
            CultivationCoreBlockEntity::new,
            CULTIVATION_CORE
        ).build(null);
        
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(MOD_ID, "cultivation_core"),
            CULTIVATION_CORE_BLOCK_ENTITY
        );
        
        FactorCraftMod.LOGGER.info("[ModCultivation] 已注册 CultivationCore BlockEntity");
    }
    
    // ==================== ScreenHandler ====================
    
    public static final ScreenHandlerType<CultivationScreenHandler> CULTIVATION_CORE_SCREEN =
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "cultivation_core_screen"),
            new ScreenHandlerType<>(CultivationScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    /**
     * 注册 Screen（服务端调用）
     */
    public static void registerScreens() {
        FactorCraftMod.LOGGER.debug("[ModCultivation] ScreenHandler 已注册");
    }
    
    /**
     * 客户端初始化
     */
    public static void initClient() {
        HandledScreens.register(CULTIVATION_CORE_SCREEN, CultivationScreen::new);
        FactorCraftMod.LOGGER.debug("[ModCultivation] 客户端 Screen 已注册");
    }
    
    /**
     * 完整注册流程（服务端）
     */
    public static void register() {
        registerBlockEntities();
        registerScreens();
        FactorCraftMod.LOGGER.info("[ModCultivation] 培育系统注册完成");
    }
}
