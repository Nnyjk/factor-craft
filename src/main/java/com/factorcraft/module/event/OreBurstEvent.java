package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 矿脉爆发事件
 * 
 * 随机位置生成临时矿点
 */
public class OreBurstEvent extends BaseEvent {
    private int oreNodesSpawned;
    
    public OreBurstEvent(Identifier worldKey, int duration) {
        super(EventType.ORE_BURST, duration, worldKey);
        this.oreNodesSpawned = 0;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // TODO: 生成临时矿点
        this.oreNodesSpawned = 5;
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 可以在这里添加矿点消失倒计时
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        // TODO: 移除临时矿点
        this.oreNodesSpawned = 0;
        super.onEnd(server, world);
    }
    
    public int getOreNodesSpawned() {
        return oreNodesSpawned;
    }
}
