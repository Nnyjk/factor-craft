package com.factorcraft.module.core.init;

import com.factorcraft.module.ui.achievement.AchievementTreeScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

/**
 * 核心模块 ScreenHandler 注册表
 * 注册成就系统等核心功能的 ScreenHandler
 */
public class CoreScreenHandlers {
    
    public static final String MOD_ID = "factorcraft";
    
    // 成就树界面
    public static final ScreenHandlerType<AchievementTreeScreenHandler> ACHIEVEMENT_TREE = registerScreenHandler(
        "achievement_tree",
        AchievementTreeScreenHandler::new
    );
    
    /**
     * 注册 ScreenHandlerType
     */
    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(
        String name,
        ScreenHandlerType.Factory<T> factory
    ) {
        return Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(MOD_ID, name),
            new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
        );
    }
    
    /**
     * 初始化所有 ScreenHandler
     * 在 Mod 初始化时调用
     */
    public static void init() {
        // 触发静态字段初始化
    }
}
