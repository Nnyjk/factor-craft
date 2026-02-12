package com.factorcraft;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactorCraftMod implements ModInitializer {
    public static final String MOD_ID = "factor_craft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Factor Craft Fabric 开发环境已加载。");
    }
}
