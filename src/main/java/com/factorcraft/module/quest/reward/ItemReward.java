package com.factorcraft.module.quest.reward;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

/**
 * 物品奖励
 */
public class ItemReward implements QuestReward {
    
    private final Identifier itemId;
    private final int count;
    
    public ItemReward(Identifier itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }
    
    @Override
    public QuestRewardType getType() {
        return QuestRewardType.ITEM;
    }
    
    @Override
    public void give(PlayerEntity player) {
        ItemStack stack = new ItemStack(player.getRegistryManager().getOrThrow(net.minecraft.registry.RegistryKeys.ITEM).get(itemId), count);
        if (!player.giveItemStack(stack)) {
            player.dropItem(stack, false);
        }
    }
    
    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item_id", itemId.toString());
        nbt.putInt("count", count);
        return nbt;
    }
    
    @Override
    public String getDescription() {
        return count + "x " + itemId.getPath();
    }
    
    public Identifier getItemId() { return itemId; }
    public int getCount() { return count; }
}
