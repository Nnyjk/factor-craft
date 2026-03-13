package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.factor.api.FactorApiProvider;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;

public final class FactorSystemModule implements FactorCraftModule {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static final FactorService SERVICE = new FactorService();
    private static FactorSystemModule instance;
    
    public static FactorSystemModule getInstance() {
        if (instance == null) instance = new FactorSystemModule();
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "factor_system";
    }

    @Override
    public void initialize() {
        FactorApiProvider.set(SERVICE);
        if (REGISTERED.compareAndSet(false, true)) {
            ServerTickEvents.END_WORLD_TICK.register(world -> {
                SERVICE.tick(world);
                if (world.getTime() % 1200 == 0) {
                    FactorCraftMod.LOGGER.debug("[FactorCraft:Factor] {} {}", world.getRegistryKey().getValue(), SERVICE.debugHudLine(world));
                }
            });

            SimpleFactorEventBus.getInstance().subscribe(FactorTierChangeEvent.class, EventPriority.NORMAL, event ->
                    FactorCraftMod.LOGGER.info(
                            "[FactorCraft:Factor] 日切 Tier 变更: world={}, day={}, {} -> {}",
                            event.world().getRegistryKey().getValue(),
                            event.dayIndex(),
                            event.previousTier(),
                            event.currentTier()
                    )
            );
        }

        FactorCraftMod.LOGGER.info("[FactorCraft:Factor] 因子系统已启用（实时因子/日切Tier/阈值广播/灾害冷却）");
    }

    @Override
    public void shutdown() {
        FactorApiProvider.reset();
    }
}