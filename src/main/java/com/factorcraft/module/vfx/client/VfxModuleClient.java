package com.factorcraft.module.vfx.client;

import com.factorcraft.FactorCraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * VFX 客户端初始化
 * 
 * 负责注册客户端 VFX 相关的事件监听器
 */
public final class VfxModuleClient {
    private static boolean initialized = false;
    
    public static void initialize() {
        if (initialized) return;
        initialized = true;
        
        // 注册客户端 tick 处理
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FactorVisualEffectManagerClient.getInstance().tickClient(client);
        });
        
        FactorCraftClient.LOGGER.info("[FactorCraft:VFX:Client] 客户端视觉效果系统已启用");
    }
}