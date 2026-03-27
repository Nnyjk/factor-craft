package com.factorcraft.module.profession.screen;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * 职业系统 Screen 注册
 */
public class ProfessionScreens {
    
    private static final String MOD_ID = FactorCraftMod.MOD_ID;
    
    // ==================== ScreenHandler Types ====================
    
    /**
     * 职业选择界面 ScreenHandlerType
     */
    public static final ScreenHandlerType<ProfessionSelectScreenHandler> PROFESSION_SELECT = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "profession_select"),
            new ScreenHandlerType<>(ProfessionSelectScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
        );
    
    /**
     * 注册所有 Screen（服务端调用）
     */
    public static void init() {
        // 已通过 Registry.register 完成
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] ScreenHandler 注册完成");
    }
    
    /**
     * 客户端初始化 - 在 FabricClientModInitializer 中调用
     */
    public static void initClient() {
        // 注册 HandledScreens
        HandledScreens.register(PROFESSION_SELECT, ProfessionSelectScreen::new);
        FactorCraftMod.LOGGER.info("[FactorCraft:Profession] 客户端 Screen 注册完成");
    }
}