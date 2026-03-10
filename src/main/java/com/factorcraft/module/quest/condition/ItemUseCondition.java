package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 使用物品条件 - 检测玩家是否使用指定物品
 */
public class ItemUseCondition implements QuestCondition {
    
    private final Identifier itemId;
    private final int requiredCount;
    private int usedCount;
    
    public ItemUseCondition(Identifier itemId, int requiredCount) {
        this.itemId = itemId;
        this.requiredCount = requiredCount;
        this.usedCount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.ITEM_USE;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return usedCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) usedCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item_id", itemId.toString());
        nbt.putInt("required", requiredCount);
        nbt.putInt("current", usedCount);
        return nbt;
    }
    
    public void onUse(int count) {
        this.usedCount += count;
    }
    
    public Identifier getItemId() { return itemId; }
    public int getRequiredCount() { return requiredCount; }
}
