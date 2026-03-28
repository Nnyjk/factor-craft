package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 机器制作触发器数据
 */
public class MachineCraftData {
    private final String machineId;
    private final int tier;
    
    public MachineCraftData(String machineId, int tier) {
        this.machineId = machineId;
        this.tier = tier;
    }
    
    public String getMachineId() {
        return machineId;
    }
    
    public int getTier() {
        return tier;
    }
}
