package com.factorcraft.module.integration;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

/**
 * 扩展包/第三方包接入入口模块。
 */
public final class NonCoreIntegrationModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "non_core_integration";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("非核心模块接入标准已启用（tools/weapons/armor/furniture）");
    }
}
