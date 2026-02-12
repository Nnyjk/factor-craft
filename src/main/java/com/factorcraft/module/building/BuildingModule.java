package com.factorcraft.module.building;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.shared.ModuleMilestone;

public final class BuildingModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "building_furniture_decor";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[{}] 建筑/家具/装饰模块骨架加载完成", ModuleMilestone.M5_BUILDING_AND_SOCIAL);
    }
}
