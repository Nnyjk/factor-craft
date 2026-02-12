package com.factorcraft.module.material;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.shared.ModuleMilestone;

public final class MaterialsModule implements FactorCraftModule {
    @Override
    public String moduleId() {
        return "materials_traits_enchants_buffs";
    }

    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[{}] 材料/词条/附魔/状态模块骨架加载完成", ModuleMilestone.M2_MATERIALS_AND_STATUS);
    }
}
