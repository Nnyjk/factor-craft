package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.nbt.NbtCompound;

/**
 * 任务条件接口
 */
public interface QuestCondition {
    
    /**
     * 获取条件类型
     */
    QuestConditionType getType();
    
    /**
     * 检查条件是否完成
     */
    boolean check(PlayerEntity player, Object context);
    
    /**
     * 获取进度 (0.0-1.0)
     */
    float getProgress(PlayerEntity player, Object context);
    
    /**
     * 序列化为 NBT
     */
    NbtCompound toNbt(RegistryWrapper.WrapperLookup registries);
    
    /**
     * 从 NBT 反序列化
     */
    static QuestCondition fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        // TODO: 实现反序列化逻辑
        return null;
    }
}
