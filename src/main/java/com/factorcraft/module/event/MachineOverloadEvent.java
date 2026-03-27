package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 机器过载事件
 * 
 * 玩家机器临时产出翻倍但消耗增加
 */
public class MachineOverloadEvent extends BaseEvent {
    private double efficiencyBonus;
    
    public MachineOverloadEvent(Identifier worldKey, int duration) {
        super(EventType.MACHINE_OVERLOAD, duration, worldKey);
        this.efficiencyBonus = 2.0;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // 机器效率提升逻辑由机器模块监听处理
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 维持过载状态
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        this.efficiencyBonus = 1.0;
        super.onEnd(server, world);
    }
    
    public double getEfficiencyBonus() {
        return efficiencyBonus;
    }
}
