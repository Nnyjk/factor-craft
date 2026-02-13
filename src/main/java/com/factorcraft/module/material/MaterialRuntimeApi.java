package com.factorcraft.module.material;

import com.factorcraft.module.factor.api.FactorApiProvider;
import com.factorcraft.module.material.api.MaterialApi;
import com.factorcraft.module.material.service.MaterialRollResult;
import com.factorcraft.module.material.service.MaterialsM2Service;
import com.factorcraft.module.material.state.MaterialDayStatusSnapshot;
import com.factorcraft.module.material.state.MaterialStatusState;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 将 M2 服务桥接为对外 API，并与 M1 FactorApi 联动（当前 Tier 驱动 roll）。
 */
public final class MaterialRuntimeApi implements MaterialApi {
    private static final String DIMENSION_FALLBACK = "minecraft:overworld";

    private final Supplier<MaterialsM2Service> serviceSupplier;
    private final Supplier<MaterialStatusState> stateSupplier;

    public MaterialRuntimeApi(Supplier<MaterialsM2Service> serviceSupplier, Supplier<MaterialStatusState> stateSupplier) {
        this.serviceSupplier = serviceSupplier;
        this.stateSupplier = stateSupplier;
    }

    @Override
    public MaterialRollResult rollForCurrentTier(ServerWorld world, long seed) {
        MaterialsM2Service service = serviceSupplier.get();
        if (service == null || world == null) {
            return new MaterialRollResult(seed, DIMENSION_FALLBACK, 0, "factorcraft:noop", java.util.List.of(), java.util.List.of(), java.util.List.of());
        }

        int currentTier = FactorApiProvider.get().getTier(world);
        String dimension = world.getRegistryKey().getValue().toString();
        return service.roll(seed, dimension, currentTier);
    }

    @Override
    public Optional<MaterialDayStatusSnapshot> latestDaySnapshot(ServerWorld world) {
        MaterialStatusState state = stateSupplier.get();
        if (state == null || world == null) {
            return Optional.empty();
        }
        String dimension = world.getRegistryKey().getValue().toString();
        return state.latest(dimension);
    }
}
