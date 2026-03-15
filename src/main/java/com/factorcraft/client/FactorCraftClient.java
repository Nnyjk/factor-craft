package com.factorcraft.client;

import com.factorcraft.module.guide.GuideSystem;
import net.fabricmc.api.ClientModInitializer;

/**
 * 客户端初始化
 */
public class FactorCraftClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // 初始化引导系统
        GuideSystem.initialize();
    }
}