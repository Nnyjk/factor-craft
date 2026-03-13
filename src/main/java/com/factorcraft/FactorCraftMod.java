package com.factorcraft;

import com.factorcraft.command.FactorCraftCommands;
import com.factorcraft.config.ConfigManager;
import com.factorcraft.datapack.DataPackManager;
import com.factorcraft.module.ModuleBootstrap;
import com.factorcraft.registry.ModInitialization;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor Craft 主 Mod 类
 */
public class FactorCraftMod implements ModInitializer {
    
    public static final String MOD_ID = "factorcraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Factor Craft Mod...");
        
        // 初始化配置系统
        ConfigManager.initialize();
        LOGGER.info("配置系统初始化完成");
        
        // 使用 ModuleBootstrap 初始化所有模块
        ModuleBootstrap.initializeDefaults();
        
        // 注册游戏内容
        ModInitialization.initialize();
        
        // 注册命令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FactorCraftCommands.register(dispatcher);
        });
        LOGGER.info("命令系统注册完成");
        
        // 初始化数据包系统
        DataPackManager.initialize();
        LOGGER.info("数据包系统初始化完成");
        
        LOGGER.info("Factor Craft Mod initialized successfully!");
    }
}