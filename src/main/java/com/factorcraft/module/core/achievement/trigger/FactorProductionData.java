package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Factor 生产触发器数据
 */
public class FactorProductionData {
    private final String factorType;
    private final int amount;
    private final String source;
    
    public FactorProductionData(String factorType, int amount, String source) {
        this.factorType = factorType;
        this.amount = amount;
        this.source = source;
    }
    
    public String getFactorType() {
        return factorType;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public String getSource() {
        return source;
    }
}
