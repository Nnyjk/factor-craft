package com.factorcraft.module.cycle.automation.endgame.screen;

import com.factorcraft.module.cycle.automation.endgame.block.entity.AutoExtractorMK2BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * 自动提取器 MK-II ScreenHandler
 */
public class AutoExtractorMK2ScreenHandler extends ScreenHandler {
    
    private final Inventory inventory;
    private final AutoExtractorMK2BlockEntity blockEntity;
    
    public AutoExtractorMK2ScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }
    
    public AutoExtractorMK2ScreenHandler(int syncId, PlayerInventory playerInventory, AutoExtractorMK2BlockEntity blockEntity) {
        super(com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationScreenHandlers.AUTO_EXTRACTOR_MK2, syncId);
        
        this.blockEntity = blockEntity;
        this.inventory = blockEntity != null ? blockEntity.getInventory() : new net.minecraft.inventory.SimpleInventory(9);
        
        checkSize(inventory, 9);
        inventory.onOpen(playerInventory.player);
        
        // 机器槽位 (9 个)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 18));
        }
        
        // 玩家物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 58 + i * 18));
            }
        }
        
        // 玩家热键栏
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 116));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (invSlot < 9) {
                if (!this.insertItem(originalStack, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, 9, false)) {
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
