package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 合成物品条件 - 检测玩家是否合成指定物品
 */
public class ItemCraftCondition implements QuestCondition {
    
    private final Identifier itemId;
    private final int requiredCount;
    private int craftedCount;
    
    public ItemCraftCondition(Identifier itemId, int requiredCount) {
        this.itemId = itemId;
        this.requiredCount = requiredCount;
        this.craftedCount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.ITEM_CRAFT;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return craftedCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) craftedCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item_id", itemId.toString());
        nbt.putInt("required", requiredCount);
        nbt.putInt("current", craftedCount);
        return nbt;
    }
    
    public void onCraft(int count) {
        this.craftedCount += count;
    }
    
    public Identifier getItemId() { return itemId; }
    public int getRequiredCount() { return requiredCount; }
}
