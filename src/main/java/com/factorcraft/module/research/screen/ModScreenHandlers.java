package com.factorcraft.module.research.screen;

import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * 研究系统 ScreenHandler 注册
 */
public class ModScreenHandlers {
    
    private static final String MOD_ID = "factorcraft";
    
    public static final ScreenHandlerType<ResearchTreeScreenHandler> RESEARCH_TREE = 
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, "research_tree"),
            new ScreenHandlerType<>(
                ResearchTreeScreenHandler::new,
                FeatureFlags.VANILLA_FEATURES
            )
        );
    
    public static void register() {
        // 注册在模块初始化时调用
    }
}
