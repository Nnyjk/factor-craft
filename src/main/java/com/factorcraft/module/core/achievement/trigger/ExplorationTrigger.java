package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 探索发现触发器
 * 监听维度传送、结构发现等事件
 */
public class ExplorationTrigger implements AchievementTrigger<ExplorationData> {
    
    private final String id;
    private final String dimension;
    private final String structure;
    
    public ExplorationTrigger(String id, String dimension, String structure) {
        this.id = id;
        this.dimension = dimension;
        this.structure = structure;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean matches(ExplorationData data) {
        // 检查维度是否匹配（空表示任意维度）
        if (dimension != null && !dimension.equals(data.getDimension())) {
            return false;
        }
        // 检查结构是否匹配（空表示任意结构）
        if (structure != null && !structure.equals(data.getStructure())) {
            return false;
        }
        return true;
    }
    
    @Override
    public int trigger(ServerPlayerEntity player, ExplorationData data) {
        // 探索发现返回固定进度 1
        return 1;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.EXPLORATION;
    }
}
