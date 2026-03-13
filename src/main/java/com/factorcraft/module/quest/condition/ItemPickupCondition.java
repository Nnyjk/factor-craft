package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 获得物品条件 - 检测玩家是否获得指定物品
 */
public class ItemPickupCondition implements QuestCondition {
    
    private final Identifier itemId;
    private final int requiredCount;
    private int currentCount;
    
    public ItemPickupCondition(Identifier itemId, int requiredCount) {
        this.itemId = itemId;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.ITEM_PICKUP;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return currentCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) currentCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item_id", itemId.toString());
        nbt.putInt("required", requiredCount);
        nbt.putInt("current", currentCount);
        return nbt;
    }
    
    /**
     * 通知物品获得
     */
    public void onPickup(ItemStack stack) {
        if (stack.getItem().equals(Identifier.of(itemId.getNamespace(), itemId.getPath()))) {
            currentCount += stack.getCount();
        }
    }
    
    public Identifier getItemId() { return itemId; }
    public int getRequiredCount() { return requiredCount; }
    public int getCurrentCount() { return currentCount; }
}
