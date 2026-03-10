package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * 任务奖励接口
 */
public interface QuestReward {
    
    QuestRewardType getType();
    
    /**
     * 给予奖励
     */
    void give(PlayerEntity player);
    
    /**
     * 序列化为 NBT
     */
    NbtCompound toNbt(RegistryWrapper.WrapperLookup registries);
    
    /**
     * 获取奖励描述
     */
    String getDescription();
}
