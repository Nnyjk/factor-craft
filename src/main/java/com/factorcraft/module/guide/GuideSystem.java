package com.factorcraft.module.guide;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * 引导书系统 - 客户端入口
 * 
 * 功能：
 * - 快捷键 (默认 G) 呼出引导 UI
 * - 游戏内文档查看
 * - 科技树展示
 */
public class GuideSystem {
    
    private static KeyBinding openGuideKey;
    private static GuideScreen currentScreen;
    
    public static void initialize() {
        // 注册快捷键
        openGuideKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.factorcraft.open_guide",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.factorcraft.general"
        ));
        
        // 监听按键
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuideKey.wasPressed()) {
                openGuide(client);
            }
        });
        
        FactorCraftMod.LOGGER.info("[GuideSystem] 引导系统已初始化，按 G 打开");
    }
    
    public static void openGuide(MinecraftClient client) {
        if (client.player == null) return;
        
        currentScreen = new GuideScreen();
        client.setScreen(currentScreen);
    }
}