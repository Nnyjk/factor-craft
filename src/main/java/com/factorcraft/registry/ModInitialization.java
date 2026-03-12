package com.factorcraft.registry;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Mod 初始化
 * 注意：方块和物品注册在各自模块中完成
 */
public class ModInitialization {
    
    public static void initialize() {
        // 注册创造模式标签页
        ModItemGroups.register();
        
        // 注册配方类型
        ModRecipes.register();
        
        // 服务器启动时的初始化
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            FactorCraftMod.LOGGER.info("[FactorCraft] Server starting, initializing systems...");
        });
        
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            FactorCraftMod.LOGGER.info("[FactorCraft] Server started successfully");
        });
        
        FactorCraftMod.LOGGER.info("[FactorCraft] Mod content initialized");
    }
}