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
        // TODO: 集成到 TechnologyModule
        // TechnologyManager.unlock(player, technologyId);
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
