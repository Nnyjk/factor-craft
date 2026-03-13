package com.factorcraft.module.ui.handler;

import com.factorcraft.module.cultivation.blockentity.CultivationCoreBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CultivationScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final CultivationCoreBlockEntity blockEntity;
    
    public CultivationScreenHandler(int syncId, PlayerInventory playerInventory, CultivationCoreBlockEntity blockEntity) {
        super(null, syncId);
        this.inventory = new SimpleInventory(1);
        this.blockEntity = blockEntity;
        
        // 输入槽
        this.addSlot(new Slot(inventory, 0, 80, 35));
        
        // 玩家背包
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        
        // 快捷栏
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            
            if (slot < 1) {
                if (!this.insertItem(itemStack2, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        
        return itemStack;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
    
    public CultivationCoreBlockEntity getBlockEntity() {
        return blockEntity;
    }
}