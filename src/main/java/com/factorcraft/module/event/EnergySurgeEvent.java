package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 能量涌流事件
 * 
 * 玩家 Factor 恢复速度提升
 */
public class EnergySurgeEvent extends BaseEvent {
    private double recoveryBonus;
    
    public EnergySurgeEvent(Identifier worldKey, int duration) {
        super(EventType.ENERGY_SURGE, duration, worldKey);
        this.recoveryBonus = 2.0;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // Factor 恢复速度提升逻辑由 Factor 模块监听处理
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 维持涌流状态
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        this.recoveryBonus = 1.0;
        super.onEnd(server, world);
    }
    
    public double getRecoveryBonus() {
        return recoveryBonus;
    }
}
