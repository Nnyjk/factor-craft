package com.factorcraft.module.cycle.energy.screen;

import com.factorcraft.module.cycle.energy.block.entity.FactorCompressorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 压缩机 ScreenHandler
 */
public class FactorCompressorScreenHandler extends ScreenHandler {
    
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final FactorCompressorBlockEntity blockEntity;
    
    public FactorCompressorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(FactorEnergyScreenHandlers.FACTOR_COMPRESSOR_HANDLER, syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(2), null);
    }
    
    public FactorCompressorScreenHandler(int syncId, PlayerInventory playerInventory, 
                                         FactorCompressorBlockEntity blockEntity) {
        this(FactorEnergyScreenHandlers.FACTOR_COMPRESSOR_HANDLER, syncId, playerInventory, blockEntity.getInventory(), 
             new ArrayPropertyDelegate(2), blockEntity);
    }
    
    public FactorCompressorScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, 
                                         Inventory inventory, PropertyDelegate delegate,
                                         FactorCompressorBlockEntity blockEntity) {
        super(type, syncId);
        this.inventory = inventory;
        this.propertyDelegate = delegate;
        this.blockEntity = blockEntity;
        
        checkSize(inventory, 2);
        inventory.onOpen(playerInventory.player);
        
        // 输入槽 (0)
        this.addSlot(new Slot(inventory, 0, 56, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return true;
            }
        });
        
        // 输出槽 (1)
        this.addSlot(new Slot(inventory, 1, 104, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });
        
        // 玩家物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // 玩家热键栏
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        
        addProperties(delegate);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (invSlot < 2) {
                if (!this.insertItem(originalStack, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(originalStack, 0, 2, false)) {
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
    
    public int getFactorAmount() {
        return blockEntity != null ? blockEntity.getFactorAmount() : propertyDelegate.get(0);
    }
    
    public int getProgress() {
        return blockEntity != null ? blockEntity.getProgress() : propertyDelegate.get(1);
    }
    
    public int getMaxProgress() {
        return blockEntity != null ? blockEntity.getMaxProgress() : 200;
    }
}
