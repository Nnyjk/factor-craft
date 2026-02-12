package com.factorcraft.module.factor.api;

import net.minecraft.server.world.ServerWorld;

import java.util.OptionalLong;

/**
 * 对齐 docs/06 中建议的 Factor API。
 */
public interface FactorApi {
    double getFactor(ServerWorld world);

    int getTier(ServerWorld world);

    OptionalLong predictCrossing(ServerWorld world, double target);

    void addFactorOffset(ServerWorld world, double offset, long durationTicks);
}
