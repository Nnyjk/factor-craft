package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * Factor 奖励 - 给予玩家 Factor 能量
 */
public class FactorReward implements QuestReward {
    
    private final double amount;
    
    public FactorReward(double amount) {
        this.amount = amount;
    }
    
    @Override
    public QuestRewardType getType() {
        return QuestRewardType.FACTOR;
    }
    
    @Override
    public void give(PlayerEntity player) {
        if (player.getWorld().isClient) {
            return; // 仅服务端处理
        }
        
        // 获取玩家所在区块
        var chunkPos = player.getChunkPos();
        
        // 注入 Factor 到区块
        com.factorcraft.module.factor.management.ChunkFactorManager.injectFactor(
            player.getWorld(),
            chunkPos,
            amount
        );
        
        System.out.println("[FactorReward] 已注入 " + amount + " Factor 到区块 " + chunkPos.x + "," + chunkPos.z);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putDouble("amount", amount);
        return nbt;
    }
    
    @Override
    public String getDescription() {
        return amount + " Factor";
    }
    
    public double getAmount() { return amount; }
}
