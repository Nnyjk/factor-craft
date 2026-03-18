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
    
    public static Block CULTIVATION_CORE;
    
    /**
     * 注册方块
     */
    private static Block registerCultivationCore(String name) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        
        // 创建带有 registryKey 的 Block
        Block block = new CultivationCoreBlock(
            AbstractBlock.Settings.create()
                .registryKey(blockKey)
                .strength(3.0f)
        );
        
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()
            .registryKey(itemKey)));
        
        FactorCraftMod.LOGGER.debug("[ModCultivation] 注册方块：{}", name);
        return block;
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
    
    public static ScreenHandlerType<CultivationScreenHandler> CULTIVATION_CORE_SCREEN;
    
    /**
     * 注册 ScreenHandler
     */
    public static void registerScreenHandlers() {
        CULTIVATION_CORE_SCREEN = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "cultivation_core_screen"),
            new ScreenHandlerType<>(CultivationScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
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
        // 注册方块
        CULTIVATION_CORE = registerCultivationCore("cultivation_core");
        
        // 注册 BlockEntity
        registerBlockEntities();
        
        // 注册 ScreenHandler
        registerScreenHandlers();
        
        FactorCraftMod.LOGGER.info("[ModCultivation] 培育系统注册完成");
    }
}