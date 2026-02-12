package com.factorcraft.module.event;

import net.minecraft.server.network.ServerPlayerEntity;

public record TrinketProcEvent(ServerPlayerEntity player, String trinketId, String trigger) {}
