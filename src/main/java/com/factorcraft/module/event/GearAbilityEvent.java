package com.factorcraft.module.event;

import net.minecraft.server.network.ServerPlayerEntity;

public record GearAbilityEvent(ServerPlayerEntity player, String abilityId, boolean success) {}
