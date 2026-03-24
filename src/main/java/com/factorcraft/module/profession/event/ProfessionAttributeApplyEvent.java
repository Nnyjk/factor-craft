package com.factorcraft.module.profession.event;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 职业属性生效事件
 */
public class ProfessionAttributeApplyEvent implements ProfessionEvent {
    private final ServerPlayerEntity player;
    private final String attributeKey;
    private double baseValue;
    private double finalValue;
    private final long timestamp;
    
    public ProfessionAttributeApplyEvent(ServerPlayerEntity player, String attributeKey, 
                                         double baseValue, double finalValue) {
        this.player = player;
        this.attributeKey = attributeKey;
        this.baseValue = baseValue;
        this.finalValue = finalValue;
        this.timestamp = System.currentTimeMillis();
    }
    
    @Override
    public ProfessionEventType getType() { return ProfessionEventType.ATTRIBUTE_APPLY; }
    @Override
    public ServerPlayerEntity getPlayer() { return player; }
    @Override
    public long getTimestamp() { return timestamp; }
    @Override
    public boolean isCancellable() { return false; }
    @Override
    public boolean isCancelled() { return false; }
    
    public String getAttributeKey() { return attributeKey; }
    public double getBaseValue() { return baseValue; }
    public double getFinalValue() { return finalValue; }
    public void setFinalValue(double value) { this.finalValue = value; }
}