package com.factorcraft;

import com.factorcraft.config.ConfigManager;
import com.factorcraft.module.ModuleBootstrap;
import net.fabricmc.api.ModInitializer;
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
        
        LOGGER.info("Factor Craft Mod initialized successfully!");
    }
}
