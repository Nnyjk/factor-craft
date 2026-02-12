package com.factorcraft.module.integration.api;

import com.factorcraft.module.integration.model.ArmorSpec;
import com.factorcraft.module.integration.model.FurnitureSpec;
import com.factorcraft.module.integration.model.ToolSpec;
import com.factorcraft.module.integration.model.WeaponSpec;

public interface NonCoreRegistrar {
    void registerTool(ToolSpec spec);

    void registerWeapon(WeaponSpec spec);

    void registerArmor(ArmorSpec spec);

    void registerFurniture(FurnitureSpec spec);
}
