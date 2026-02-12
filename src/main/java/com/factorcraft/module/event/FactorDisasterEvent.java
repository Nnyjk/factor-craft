package com.factorcraft.module.event;

import net.minecraft.server.world.ServerWorld;

public record FactorDisasterEvent(ServerWorld world, String disasterId, int severity) {}
