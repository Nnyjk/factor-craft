package com.factorcraft.module.profession.event;

import com.factorcraft.module.profession.model.ProfessionType;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 职业升级事件
 */
public class ProfessionLevelUpEvent implements ProfessionEvent {
    private final ServerPlayerEntity player;
    private final int oldLevel;
    private final int newLevel;
    private final long timestamp;
    
    public ProfessionLevelUpEvent(ServerPlayerEntity player, int oldLevel, int newLevel) {
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public ProfessionEventType getType() { return ProfessionEventType.LEVEL_UP; }
    @Override
    public ServerPlayerEntity getPlayer() { return player; }
    @Override
    public long getTimestamp() { return timestamp; }
    @Override
    public boolean isCancellable() { return false; }
    @Override
    public boolean isCancelled() { return false; }
    
    public int getOldLevel() { return oldLevel; }
    public int getNewLevel() { return newLevel; }
    public int getLevelIncrease() { return newLevel - oldLevel; }
}