package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.factor.api.FactorApiProvider;
import com.factorcraft.module.shared.ModuleMilestone;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;

public final class FactorSystemModule implements FactorCraftModule {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static final FactorService SERVICE = new FactorService();

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
                    FactorCraftMod.LOGGER.debug("[M1] {} {}", world.getRegistryKey().getValue(), SERVICE.debugHudLine(world));
                }
            });

            SimpleFactorEventBus.getInstance().subscribe(FactorTierChangeEvent.class, EventPriority.NORMAL, event ->
                    FactorCraftMod.LOGGER.info(
                            "[M1] 日切 Tier 变更: world={}, day={}, {} -> {}",
                            event.world().getRegistryKey().getValue(),
                            event.dayIndex(),
                            event.previousTier(),
                            event.currentTier()
                    )
            );
        }

        FactorCraftMod.LOGGER.info("[{}] 因子系统 M1 已启用（实时因子/日切Tier/阈值广播/灾害冷却）", ModuleMilestone.M1_FACTOR_AND_TIER);
    }

    @Override
    public void shutdown() {
        FactorApiProvider.reset();
    }
}
