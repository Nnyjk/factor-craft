package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 维度传输条件 - 检测玩家是否传输到指定维度
 */
public class DimensionTravelCondition implements QuestCondition {
    
    private final Identifier dimensionId;
    private boolean traveled;
    
    public DimensionTravelCondition(Identifier dimensionId) {
        this.dimensionId = dimensionId;
        this.traveled = false;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.DIMENSION_TRAVEL;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return traveled;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return traveled ? 1.0f : 0.0f;
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("dimension_id", dimensionId.toString());
        nbt.putBoolean("traveled", traveled);
        return nbt;
    }
    
    public void onTravel() {
        this.traveled = true;
    }
    
    public Identifier getDimensionId() { return dimensionId; }
}
