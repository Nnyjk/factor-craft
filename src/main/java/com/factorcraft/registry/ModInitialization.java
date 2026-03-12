package com.factorcraft.registry;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ModInitialization {
    
    public static void initialize() {
        // 注册方块和物品
        ModBlocks.register();
        ModItems.register();
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