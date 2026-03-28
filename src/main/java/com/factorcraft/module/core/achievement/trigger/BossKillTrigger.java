package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Boss 击杀触发器
 * 监听 Boss 实体死亡事件
 */
public class BossKillTrigger implements AchievementTrigger<BossKillData> {
    
    private final String id;
    private final String bossId;
    private final String bossType;
    private final Integer minLevel;
    
    public BossKillTrigger(String id, String bossId, String bossType, Integer minLevel) {
        this.id = id;
        this.bossId = bossId;
        this.bossType = bossType;
        this.minLevel = minLevel;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean matches(BossKillData data) {
        // 检查 Boss ID 是否匹配（空表示任意 Boss）
        if (bossId != null && !bossId.equals(data.getBossId())) {
            return false;
        }
        // 检查 Boss 类型是否匹配（空表示任意类型）
        if (bossType != null && !bossType.equals(data.getBossType())) {
            return false;
        }
        // 检查等级是否达到要求
        if (minLevel != null && data.getLevel() < minLevel) {
            return false;
        }
        return true;
    }
    
    @Override
    public int trigger(ServerPlayerEntity player, BossKillData data) {
        // 击杀 Boss 返回固定进度 1
        return 1;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.BOSS_KILL;
    }
}
