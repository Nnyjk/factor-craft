package com.factorcraft.module.cycle.automation.endgame.screen;

import com.factorcraft.module.cycle.automation.endgame.block.entity.QuantumStorageBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * 量子仓储单元 ScreenHandler
 */
public class QuantumStorageScreenHandler extends ScreenHandler {
    
    private final Inventory inventory;
    private final QuantumStorageBlockEntity blockEntity;
    
    public QuantumStorageScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }
    
    public QuantumStorageScreenHandler(int syncId, PlayerInventory playerInventory, QuantumStorageBlockEntity blockEntity) {
        super(com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationScreenHandlers.QUANTUM_STORAGE, syncId);
        this.blockEntity = blockEntity;
        this.inventory = blockEntity != null ? blockEntity.getInventory() : new net.minecraft.inventory.SimpleInventory(27);
        
        // 添加仓储物品槽 (3 行 9 列)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9, 8 + col * 18, 17 + row * 18));
            }
        }
        
        // 添加玩家物品栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // 添加玩家快捷栏
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (invSlot < 27) {
                if (!this.insertItem(originalStack, 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return newStack;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        if (blockEntity != null && blockEntity.isRemoved()) {
            return false;
        }
        return this.inventory.canPlayerUse(player);
    }
}
