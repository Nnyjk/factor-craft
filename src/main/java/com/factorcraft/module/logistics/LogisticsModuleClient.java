package com.factorcraft.module.logistics;

import net.fabricmc.api.ClientModInitializer;

/**
 * Factor 物流系统客户端模块
 */
public class LogisticsModuleClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        LogisticsModule.LOGGER.info("FactorCraft Logistics Client Module initialized");
    }
}
