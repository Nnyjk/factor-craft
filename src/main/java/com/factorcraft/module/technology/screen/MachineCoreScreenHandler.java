package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.MachineBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * 机器核心屏幕处理器基类
 * 
 * 提供多方块结构信息同步和基础 GUI 逻辑
 */
public abstract class MachineCoreScreenHandler extends ScreenHandler {
    
    protected final MachineBlockEntity blockEntity;
    protected final PlayerInventory playerInventory;
    
    public MachineCoreScreenHandler(ScreenHandlerType<?> type, int syncId, 
                                     PlayerInventory playerInventory, MachineBlockEntity blockEntity) {
        super(type, syncId);
        this.blockEntity = blockEntity;
        this.playerInventory = playerInventory;
        
        // 添加玩家物品栏槽位
        addPlayerInventorySlots();
    }
    
    /**
     * 添加玩家物品栏槽位
     */
    protected void addPlayerInventorySlots() {
        // 玩家背包 (3x9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 
                    8 + col * 18, 84 + row * 18));
            }
        }
        
        // 玩家快捷栏 (1x9)
        for (int col = 0; col < 9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity != null && player.getBlockPos().isWithinDistance(
            blockEntity.getPos(), 8.0);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (invSlot < 36) {
                // 从玩家背包移动到机器
                if (!this.insertItem(originalStack, 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从机器移动到玩家背包
                if (!this.insertItem(originalStack, 0, 36, true)) {
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
    
    /**
     * 获取 BlockEntity
     */
    public MachineBlockEntity getBlockEntity() {
        return blockEntity;
    }
}