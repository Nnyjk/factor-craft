package com.factorcraft.module.event;

import net.minecraft.server.world.ServerWorld;

public record FactorThresholdEvent(ServerWorld world, int fromTier, int toTier, long dayIndex) {}
