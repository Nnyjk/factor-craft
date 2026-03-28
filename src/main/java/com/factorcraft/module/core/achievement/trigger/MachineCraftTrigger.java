package com.factorcraft.module.core.achievement.trigger;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 机器制作触发器
 * 监听机器设备制作事件
 */
public class MachineCraftTrigger implements AchievementTrigger<MachineCraftData> {
    
    private final String id;
    private final String machineId;
    private final Integer requiredTier;
    
    public MachineCraftTrigger(String id, String machineId, Integer requiredTier) {
        this.id = id;
        this.machineId = machineId;
        this.requiredTier = requiredTier;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean matches(MachineCraftData data) {
        // 检查机器 ID 是否匹配（空表示任意机器）
        if (machineId != null && !machineId.equals(data.getMachineId())) {
            return false;
        }
        // 检查等级是否达到要求
        if (requiredTier != null && data.getTier() < requiredTier) {
            return false;
        }
        return true;
    }
    
    @Override
    public int trigger(ServerPlayerEntity player, MachineCraftData data) {
        // 制作机器返回固定进度 1
        return 1;
    }
    
    @Override
    public TriggerType getType() {
        return TriggerType.MACHINE_CRAFT;
    }
}
