package com.factorcraft.module.event.worldevent;

import net.minecraft.server.world.ServerWorld;

/**
 * 世界事件触发事件
 * 
 * 当世界事件开始时发布到事件总线
 */
public record WorldEventTriggeredEvent(
    ServerWorld world,
    ActiveWorldEvent event
) {
    public WorldEventType getType() {
        return event.getType();
    }
    
    public int getSeverity() {
        return event.getSeverity();
    }
    
    public int getRadius() {
        return event.getRadius();
    }
}