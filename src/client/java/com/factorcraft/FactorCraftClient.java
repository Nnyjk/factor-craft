package com.factorcraft;

import net.fabricmc.api.ClientModInitializer;

public class FactorCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FactorCraftMod.LOGGER.info("Factor Craft 客户端初始化完成。");
    }
}
