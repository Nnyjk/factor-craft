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
        // TODO: 集成到 FactorNetworkManager
        // FactorNetworkManager.giveFactor(player, amount);
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
