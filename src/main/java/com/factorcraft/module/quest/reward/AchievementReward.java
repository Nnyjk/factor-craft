package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 成就解锁奖励 - 解锁成就
 */
public class AchievementReward implements QuestReward {
    
    private final Identifier achievementId;
    
    public AchievementReward(Identifier achievementId) {
        this.achievementId = achievementId;
    }
    
    @Override
    public QuestRewardType getType() {
        return QuestRewardType.ACHIEVEMENT;
    }
    
    @Override
    public void give(PlayerEntity player) {
        // 集成到成就系统
        // 待完善：实现完整的成就解锁系统
        System.out.println("[AchievementReward] 解锁成就: " + achievementId + " for player " + player.getName().getString());
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("achievement_id", achievementId.toString());
        return nbt;
    }
    
    @Override
    public String getDescription() {
        return "Achievement: " + achievementId.getPath();
    }
    
    public Identifier getAchievementId() { return achievementId; }
}
