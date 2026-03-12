package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 科技解锁奖励 - 解锁新的科技节点
 */
public class TechnologyReward implements QuestReward {
    
    private final Identifier technologyId;
    
    public TechnologyReward(Identifier technologyId) {
        this.technologyId = technologyId;
    }
    
    @Override
    public QuestRewardType getType() {
        return QuestRewardType.TECHNOLOGY;
    }
    
    @Override
    public void give(PlayerEntity player) {
        // 集成到 TechnologyModule
        // 待完善：实现完整的科技解锁系统
        System.out.println("[TechnologyReward] 解锁科技: " + technologyId + " for player " + player.getName().getString());
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("technology_id", technologyId.toString());
        return nbt;
    }
    
    @Override
    public String getDescription() {
        return "Unlock: " + technologyId.getPath();
    }
    
    public Identifier getTechnologyId() { return technologyId; }
}
