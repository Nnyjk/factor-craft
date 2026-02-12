package com.factorcraft.module.event;

import net.minecraft.server.world.ServerWorld;

public record CreatureSpawnRollEvent(ServerWorld world, String entityId, int tier, int weight) {}
