package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 生物狂潮事件
 * 
 * 区域内生物生成率大幅提升
 */
public class CreatureFrenzyEvent extends BaseEvent {
    private double spawnRateBonus;
    
    public CreatureFrenzyEvent(Identifier worldKey, int duration) {
        super(EventType.CREATURE_FRENZY, duration, worldKey);
        this.spawnRateBonus = 3.0;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // 生物生成率提升逻辑由生物模块监听处理
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 维持狂潮状态
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        this.spawnRateBonus = 1.0;
        super.onEnd(server, world);
    }
    
    public double getSpawnRateBonus() {
        return spawnRateBonus;
    }
}
