package com.factorcraft.module.event;

import net.minecraft.server.world.ServerWorld;

public record FactorChangeEvent(ServerWorld world, double previous, double current) {}
