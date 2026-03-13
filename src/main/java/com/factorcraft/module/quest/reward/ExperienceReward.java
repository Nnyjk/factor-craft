package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

/**
 * 经验奖励
 */
public class ExperienceReward implements QuestReward {
    
    private final int amount;
    
    public ExperienceReward(int amount) {
        this.amount = amount;
    }
    
    @Override
    public QuestRewardType getType() {
        return QuestRewardType.EXPERIENCE;
    }
    
    @Override
    public void give(PlayerEntity player) {
        player.addExperience(amount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("amount", amount);
        return nbt;
    }
    
    @Override
    public String getDescription() {
        return amount + " XP";
    }
    
    public int getAmount() { return amount; }
}
