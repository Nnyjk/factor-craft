package com.factorcraft.module.gear.api;

import net.minecraft.server.network.ServerPlayerEntity;

public interface GearApi {
    double getConductivity(ServerPlayerEntity player);

    boolean tryActivateAbility(ServerPlayerEntity player, String abilityId);
}
