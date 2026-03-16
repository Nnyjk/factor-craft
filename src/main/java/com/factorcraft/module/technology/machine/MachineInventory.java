package com.factorcraft.module.technology.machine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

/**
 * 机器物品库存接口
 * 
 * 为机器提供简单的物品存储功能
 */
public interface MachineInventory extends Inventory {
    
    /**
     * 获取物品列表
     */
    DefaultedList<ItemStack> getItems();
    
    /**
     * 从 NBT 读取物品
     */
    default void readInventoryNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.readNbt(nbt, getItems(), registries);
    }
    
    /**
     * 写入物品到 NBT
     */
    default void writeInventoryNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        Inventories.writeNbt(nbt, getItems(), registries);
    }
    
    // ==================== Inventory 接口实现 ====================
    
    @Override
    default int size() {
        return getItems().size();
    }
    
    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }
    
    @Override
    default ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(getItems(), slot, amount);
    }
    
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }
    
    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }
    
    @Override
    default void clear() {
        getItems().clear();
    }
    
    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
}