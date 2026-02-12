package com.factorcraft.module.technology;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.shared.ModuleMilestone;

public final class TechnologyModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "technology_multiblock";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[{}] 科技树/多方块/模块强化骨架加载完成", ModuleMilestone.M3_MULTIBLOCK_AND_BUDGET);
    }
}
