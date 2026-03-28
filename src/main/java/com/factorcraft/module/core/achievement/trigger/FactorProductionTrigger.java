package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Factor 生产触发器
 * 监听 Factor 晶体生产、提纯等事件
 */
public class FactorProductionTrigger implements AchievementTrigger<FactorProductionData> {
    
    private final String id;
    private final String factorType;
    private final int minAmount;
    private final String source;
    
    public FactorProductionTrigger(String id, String factorType, int minAmount, String source) {
        this.id = id;
        this.factorType = factorType;
        this.minAmount = minAmount;
        this.source = source;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean matches(FactorProductionData data) {
        // 检查 Factor 类型是否匹配（空表示任意类型）
        if (factorType != null && !factorType.equals(data.getFactorType())) {
            return false;
        }
        // 检查数量是否达到阈值
        if (data.getAmount() < minAmount) {
            return false;
        }
        // 检查来源是否匹配（空表示任意来源）
        if (source != null && !source.equals(data.getSource())) {
            return false;
        }
        return true;
    }
    
    @Override
    public int trigger(ServerPlayerEntity player, FactorProductionData data) {
        // 返回实际生产数量作为进度
        return data.getAmount();
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.FACTOR_PRODUCTION;
    }
}
