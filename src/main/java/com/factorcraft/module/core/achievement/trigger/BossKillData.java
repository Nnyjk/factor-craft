package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Boss 击杀触发器数据
 */
public class BossKillData {
    private final String bossId;
    private final String bossType;
    private final int level;
    
    public BossKillData(String bossId, String bossType, int level) {
        this.bossId = bossId;
        this.bossType = bossType;
        this.level = level;
    }
    
    public String getBossId() {
        return bossId;
    }
    
    public String getBossType() {
        return bossType;
    }
    
    public int getLevel() {
        return level;
    }
}
