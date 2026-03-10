package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * 任务奖励接口
 */
public interface QuestReward {
    
    /**
     * 发放奖励给玩家
     */
    void give(PlayerEntity player);
    
    /**
     * 序列化为 NBT
     */
    NbtCompound toNbt(RegistryWrapper.WrapperLookup registries);
    
    /**
     * 从 NBT 反序列化
     */
    static QuestReward fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        // TODO: 实现反序列化逻辑
        return null;
    }
}
