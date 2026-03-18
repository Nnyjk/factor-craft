package com.factorcraft.module.event.worldevent;

import net.minecraft.server.world.ServerWorld;

/**
 * 世界事件结束事件
 * 
 * 当世界事件结束时发布到事件总线
 */
public record WorldEventEndedEvent(
    ServerWorld world,
    ActiveWorldEvent event
) {
    public WorldEventType getType() {
        return event.getType();
    }
    
    public long getDuration() {
        return event.getElapsedTicks();
    }
}