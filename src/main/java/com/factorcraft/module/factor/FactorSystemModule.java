package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.shared.ModuleMilestone;

public final class FactorSystemModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "factor_system";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[{}] 因子系统模块骨架加载完成（Tier/日切/阈值事件预留）", ModuleMilestone.M1_FACTOR_AND_TIER);
    }
}
