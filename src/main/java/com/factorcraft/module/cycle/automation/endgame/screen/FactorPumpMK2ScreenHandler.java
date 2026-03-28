package com.factorcraft.module.cycle.automation.endgame.screen;

import com.factorcraft.module.cycle.automation.endgame.block.entity.FactorPumpMK2BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 泵 MK-II ScreenHandler
 */
public class FactorPumpMK2ScreenHandler extends ScreenHandler {
    
    private final FactorPumpMK2BlockEntity blockEntity;
    
    public FactorPumpMK2ScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }
    
    public FactorPumpMK2ScreenHandler(int syncId, PlayerInventory playerInventory, FactorPumpMK2BlockEntity blockEntity) {
        super(com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationScreenHandlers.FACTOR_PUMP_MK2, syncId);
        this.blockEntity = blockEntity;
        
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
            
            // 泵没有机器槽位，直接返回
            return ItemStack.EMPTY;
        }
        
        return newStack;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        if (blockEntity != null && blockEntity.isRemoved()) {
            return false;
        }
        return true;
    }
}
