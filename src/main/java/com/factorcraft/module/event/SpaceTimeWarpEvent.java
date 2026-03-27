package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 时空扭曲事件
 * 
 * 传送门效率提升
 */
public class SpaceTimeWarpEvent extends BaseEvent {
    private double portalEfficiencyBonus;
    
    public SpaceTimeWarpEvent(Identifier worldKey, int duration) {
        super(EventType.SPACE_TIME_WARP, duration, worldKey);
        this.portalEfficiencyBonus = 1.5;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // 传送门效率提升逻辑由传送模块监听处理
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 维持扭曲状态
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        this.portalEfficiencyBonus = 1.0;
        super.onEnd(server, world);
    }
    
    public double getPortalEfficiencyBonus() {
        return portalEfficiencyBonus;
    }
}
