package com.factorcraft.module.factor.api;

import net.minecraft.server.world.ServerWorld;

import java.util.OptionalLong;

final class NoopFactorApi implements FactorApi {
    @Override
    public double getFactor(ServerWorld world) {
        return 0;
    }

    @Override
    public int getTier(ServerWorld world) {
        return 0;
    }

    @Override
    public OptionalLong predictCrossing(ServerWorld world, double target) {
        return OptionalLong.empty();
    }

    @Override
    public void addFactorOffset(ServerWorld world, double offset, long durationTicks) {
        // skeleton
    }
}
