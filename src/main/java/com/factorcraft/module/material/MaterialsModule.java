package com.factorcraft.module.material;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.dynamic.DynamicBundle;
import com.factorcraft.dynamic.DynamicContentManager;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.material.api.MaterialApiProvider;
import com.factorcraft.module.material.config.MaterialsM2Config;
import com.factorcraft.module.material.config.MaterialsM2ConfigParser;
import com.factorcraft.module.material.config.MaterialsM2Defaults;
import com.factorcraft.module.material.service.MaterialRollResult;
import com.factorcraft.module.material.service.MaterialsM2Service;
import com.factorcraft.module.material.state.MaterialStatusState;
import com.factorcraft.module.material.validation.MaterialsM2SpecValidator;
import com.factorcraft.module.shared.ModuleMilestone;

import java.util.concurrent.atomic.AtomicBoolean;

public final class MaterialsModule implements FactorCraftModule {
    private static final AtomicBoolean BOOTSTRAPPED = new AtomicBoolean(false);

    private final MaterialTierSyncService syncService = new MaterialTierSyncService();

    private MaterialsM2Service service;
    private MaterialStatusState statusState;
    private MaterialRuntimeApi runtimeApi;

    @Override
    public String moduleId() {
        return "materials_traits_enchants_buffs";
    }

    @Override
    public void initialize() {
        MaterialsM2Config defaults = MaterialsM2Defaults.create();
        DynamicBundle bundle = DynamicContentManager.getInstance().current();
        MaterialsM2Config config = MaterialsM2ConfigParser.parseOrDefault(bundle.materialsM2(), defaults);
        MaterialsM2SpecValidator.validate(config);

        service = new MaterialsM2Service(config);
        statusState = new MaterialStatusState();

        if (runtimeApi == null) {
            runtimeApi = new MaterialRuntimeApi(() -> service, () -> statusState);
        }
        MaterialApiProvider.set(runtimeApi);
        syncService.bind(service, statusState);
        if (BOOTSTRAPPED.compareAndSet(false, true)) {
            SimpleFactorEventBus.getInstance().subscribe(FactorTierChangeEvent.class, EventPriority.NORMAL, syncService::onTierChanged);
        }

        MaterialRollResult demo = service.roll(20260214L, "minecraft:the_nether", 3);
        FactorCraftMod.LOGGER.info("[{}] M2 模块已加载，演示链路={}, trackedWorlds={}", ModuleMilestone.M2_MATERIALS_AND_STATUS, demo, statusState.worldCount());
    }

    @Override
    public void reload() {
        initialize();
    }

    @Override
    public void shutdown() {
        if (statusState != null) {
            statusState.clear();
        }
        service = null;
        statusState = null;
        syncService.bind(null, null);
        MaterialApiProvider.reset();
    }
}
