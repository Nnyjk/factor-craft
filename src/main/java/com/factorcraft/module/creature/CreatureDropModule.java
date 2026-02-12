package com.factorcraft.module.creature;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.shared.ModuleMilestone;

/**
 * 怪物生态与凋落物模块。
 */
public final class CreatureDropModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "creature_drop";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[{}] 怪物与凋落物模块骨架加载完成（生态刷新/掉落池/日切联动预留）", ModuleMilestone.M1B_CREATURE_AND_DROPS);
    }
}
