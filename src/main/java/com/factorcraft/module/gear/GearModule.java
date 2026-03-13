package com.factorcraft.module.gear;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

public final class GearModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "combat_gear_trinkets";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Gear] 战斗/装备/饰品/能力模块骨架加载完成");
    }
}