package com.factorcraft;

import com.factorcraft.dynamic.DynamicBundle;
import com.factorcraft.dynamic.DynamicContentManager;
import com.factorcraft.module.ModuleBootstrap;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactorCraftMod implements ModInitializer {
    public static final String MOD_ID = "factor_craft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        DynamicContentManager.getInstance().bootstrap();

        ModuleBootstrap.initializeDefaults();

        DynamicBundle bundle = DynamicContentManager.getInstance().current();
        LOGGER.info("Factor Craft Fabric 开发环境已加载，统一动态加载系统已启用。configs={}", bundle.configs().size());
    }
}
