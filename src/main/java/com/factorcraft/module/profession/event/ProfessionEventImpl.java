package com.factorcraft.module.profession.event;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 职业事件实现
 */
public class ProfessionEventImpl implements ProfessionEvent {
    
    private final ProfessionEventType type;
    private final ServerPlayerEntity player;
    private final long timestamp;
    private final boolean cancellable;
    private boolean cancelled;
    
    public ProfessionEventImpl(ProfessionEventType type, ServerPlayerEntity player) {
        this.type = type;
        this.player = player;
        this.timestamp = System.currentTimeMillis();
        this.cancellable = false; // 默认不可取消
        this.cancelled = false;
    }
    
    @Override
    public ProfessionEventType getType() {
        return type;
    }
    
    @Override
    public ServerPlayerEntity getPlayer() {
        return player;
    }
    
    @Override
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public boolean isCancellable() {
        return cancellable;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void cancel() {
        if (!cancellable) {
            throw new UnsupportedOperationException("此事件不可取消");
        }
        this.cancelled = true;
    }
}