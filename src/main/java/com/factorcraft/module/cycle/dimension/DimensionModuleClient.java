package com.factorcraft.module.cycle.dimension;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 维度模块 - 客户端初始化
 */
@Environment(EnvType.CLIENT)
public class DimensionModuleClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(DimensionModule.MOD_ID);
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing FactorCraft Dimension Module Client");
        
        // 客户端渲染注册等
    }
}
