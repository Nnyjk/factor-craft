package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * 商人访问事件
 * 
 * 特殊 NPC 商人随机出现，提供稀有物品交易
 */
public class MerchantVisitEvent extends BaseEvent {
    private boolean merchantSpawned;
    
    public MerchantVisitEvent(Identifier worldKey, int duration) {
        super(EventType.MERCHANT_VISIT, duration, worldKey);
        this.merchantSpawned = false;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // TODO: 生成商人 NPC
        this.merchantSpawned = true;
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 可以在这里添加商人移动逻辑
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        // TODO: 移除商人 NPC
        this.merchantSpawned = false;
        super.onEnd(server, world);
    }
    
    public boolean isMerchantSpawned() {
        return merchantSpawned;
    }
}
