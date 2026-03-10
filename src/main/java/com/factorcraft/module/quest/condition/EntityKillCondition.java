package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 击杀怪物条件 - 检测玩家是否击杀指定怪物
 */
public class EntityKillCondition implements QuestCondition {
    
    private final Identifier entityId;
    private final int requiredCount;
    private int killCount;
    
    public EntityKillCondition(Identifier entityId, int requiredCount) {
        this.entityId = entityId;
        this.requiredCount = requiredCount;
        this.killCount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.ENTITY_KILL;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return killCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) killCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("entity_id", entityId.toString());
        nbt.putInt("required", requiredCount);
        nbt.putInt("current", killCount);
        return nbt;
    }
    
    public void onKill(int count) {
        this.killCount += count;
    }
    
    public Identifier getEntityId() { return entityId; }
    public int getRequiredCount() { return requiredCount; }
}
