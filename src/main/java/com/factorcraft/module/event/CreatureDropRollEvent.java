package com.factorcraft.module.event;

import net.minecraft.server.network.ServerPlayerEntity;

public record CreatureDropRollEvent(ServerPlayerEntity killer, String entityId, String poolId, int tier) {}
