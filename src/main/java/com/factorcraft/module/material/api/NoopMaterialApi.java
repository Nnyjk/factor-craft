package com.factorcraft.module.material.api;

import com.factorcraft.module.material.service.MaterialRollResult;
import com.factorcraft.module.material.state.MaterialDayStatusSnapshot;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Optional;

final class NoopMaterialApi implements MaterialApi {
    @Override
    public MaterialRollResult rollForCurrentTier(ServerWorld world, long seed) {
        return new MaterialRollResult(seed, "minecraft:overworld", 0, "factorcraft:noop", List.of(), List.of(), List.of());
    }

    @Override
    public Optional<MaterialDayStatusSnapshot> latestDaySnapshot(ServerWorld world) {
        return Optional.empty();
    }
}
