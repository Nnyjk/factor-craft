package com.factorcraft.module;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.building.BuildingModule;
import com.factorcraft.module.command.CommandModule;
import com.factorcraft.module.creature.CreatureDropModule;
import com.factorcraft.module.factor.FactorSystemModule;
import com.factorcraft.module.gear.GearModule;
import com.factorcraft.module.integration.NonCoreIntegrationModule;
import com.factorcraft.module.material.MaterialsModule;
import com.factorcraft.module.technology.TechnologyModule;

import java.util.List;

/**
 * 统一管理模块注册与初始化。
 */
public final class ModuleBootstrap {
    private ModuleBootstrap() {}

    public static List<FactorCraftModule> createDefaultModules() {
        return List.of(
                new CommandModule(),
                new FactorSystemModule(),
                new CreatureDropModule(),
                new MaterialsModule(),
                new TechnologyModule(),
                new GearModule(),
                new BuildingModule(),
                new NonCoreIntegrationModule()
        );
    }

    public static void initializeDefaults() {
        for (FactorCraftModule module : createDefaultModules()) {
            module.initialize();
            FactorCraftMod.LOGGER.info("模块已初始化: {}", module.moduleId());
        }
    }
}
