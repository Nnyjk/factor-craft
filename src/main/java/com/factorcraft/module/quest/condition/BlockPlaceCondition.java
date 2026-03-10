package com.factorcraft.module.quest.condition;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 放置方块条件 - 检测玩家是否放置指定方块
 */
public class BlockPlaceCondition implements QuestCondition {
    
    private final Identifier blockId;
    private final int requiredCount;
    private int placeCount;
    
    public BlockPlaceCondition(Identifier blockId, int requiredCount) {
        this.blockId = blockId;
        this.requiredCount = requiredCount;
        this.placeCount = 0;
    }
    
    @Override
    public QuestConditionType getType() {
        return QuestConditionType.BLOCK_PLACE;
    }
    
    @Override
    public boolean check(PlayerEntity player, Object context) {
        return placeCount >= requiredCount;
    }
    
    @Override
    public float getProgress(PlayerEntity player, Object context) {
        return Math.min(1.0f, (float) placeCount / requiredCount);
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("block_id", blockId.toString());
        nbt.putInt("required", requiredCount);
        nbt.putInt("current", placeCount);
        return nbt;
    }
    
    public void onPlace(int count) {
        this.placeCount += count;
    }
    
    public Identifier getBlockId() { return blockId; }
    public int getRequiredCount() { return requiredCount; }
}
