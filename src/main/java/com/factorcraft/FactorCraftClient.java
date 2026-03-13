package com.factorcraft;

import com.factorcraft.module.technology.block.ModBlocks;
import com.factorcraft.module.technology.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor Craft 客户端初始化
 */
public class FactorCraftClient implements ClientModInitializer {
    
    public static final Logger LOGGER = LoggerFactory.getLogger("FactorCraftClient");
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("[FactorCraftClient] 客户端初始化开始...");
        
        // 客户端资源加载完成
        LOGGER.info("[FactorCraftClient] 客户端初始化完成");
    }
}