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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一管理模块注册与初始化。
 */
public final class ModuleBootstrap {
    private static final List<FactorCraftModule> DEFAULT_MODULES = List.of(
            new CommandModule(),
            new FactorSystemModule(),
            new CreatureDropModule(),
            new MaterialsModule(),
            new TechnologyModule(),
            new GearModule(),
            new BuildingModule(),
            new NonCoreIntegrationModule()
    );

    private ModuleBootstrap() {}

    public static List<FactorCraftModule> createDefaultModules() {
        return DEFAULT_MODULES;
    }

    public static void initializeDefaults() {
        validateDependencies(DEFAULT_MODULES);
        for (FactorCraftModule module : DEFAULT_MODULES) {
            module.initialize();
            FactorCraftMod.LOGGER.info("模块已初始化: {}", module.moduleId());
        }
    }

    public static void reloadDefaults() {
        for (FactorCraftModule module : DEFAULT_MODULES) {
            module.reload();
            FactorCraftMod.LOGGER.info("模块已重载: {}", module.moduleId());
        }
    }

    public static void shutdownDefaults() {
        for (int i = DEFAULT_MODULES.size() - 1; i >= 0; i--) {
            FactorCraftModule module = DEFAULT_MODULES.get(i);
            module.shutdown();
            FactorCraftMod.LOGGER.info("模块已关闭: {}", module.moduleId());
        }
    }

    private static void validateDependencies(List<FactorCraftModule> modules) {
        Map<String, FactorCraftModule> byId = new LinkedHashMap<>();
        for (FactorCraftModule module : modules) {
            String id = module.moduleId();
            if (byId.containsKey(id)) {
                throw new IllegalStateException("duplicate moduleId: " + id);
            }
            byId.put(id, module);
        }

        for (FactorCraftModule module : modules) {
            for (String dependency : module.dependencies()) {
                if (!byId.containsKey(dependency)) {
                    throw new IllegalStateException(
                            "module " + module.moduleId() + " depends on missing module " + dependency
                    );
                }
            }
        }
    }
}
