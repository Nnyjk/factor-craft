package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 提交物品条件 - 检测玩家是否提交指定物品给任务 NPC
 */
public class ItemSubmitCondition implements QuestCondition {
    
    private final Identifier itemId;
    private final int requiredCount;
    private int submittedCount;
    
    public ItemSubmitCondition(Identifier itemId, int requiredCount) {
        this.itemId = itemId;
        this.requiredCount = requiredCount;
        this.submittedCount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.ITEM_SUBMIT;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return submittedCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) submittedCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item_id", itemId.toString());
        nbt.putInt("required", requiredCount);
        nbt.putInt("current", submittedCount);
        return nbt;
    }
    
    public void onSubmit(int count) {
        this.submittedCount += count;
    }
    
    public Identifier getItemId() { return itemId; }
    public int getRequiredCount() { return requiredCount; }
}
