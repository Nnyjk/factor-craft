package com.factorcraft.module.event;

import net.minecraft.server.world.ServerWorld;

public record FactorTierChangeEvent(ServerWorld world, int previousTier, int currentTier, long dayIndex) {}
