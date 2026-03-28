package com.factorcraft.module.cycle.automation.endgame.screen;

import com.factorcraft.module.cycle.automation.endgame.block.entity.AdvancedCrafterBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * 高级合成器 ScreenHandler
 * 支持 4 个并行任务的 UI
 */
public class AdvancedCrafterScreenHandler extends ScreenHandler {
    
    private final AdvancedCrafterBlockEntity blockEntity;
    
    public AdvancedCrafterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new AdvancedCrafterBlockEntity(null, null));
    }
    
    public AdvancedCrafterScreenHandler(int syncId, PlayerInventory playerInventory, AdvancedCrafterBlockEntity blockEntity) {
        super(com.factorcraft.module.cycle.automation.endgame.init.EndgameAutomationScreenHandlers.ADVANCED_CRAFTER, syncId);
        this.blockEntity = blockEntity;
        
        // 添加 4 个任务的物品槽
        for (int task = 0; task < AdvancedCrafterBlockEntity.PARALLEL_COUNT; task++) {
            int xOffset = (task % 2) * 60;
            int yOffset = (task / 2) * 60;
            
            // 输入槽 (3x3)
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    addSlot(new Slot(blockEntity.getTaskInventory(task), col + row * 3, 8 + xOffset + col * 18, 17 + yOffset + row * 18));
                }
            }
            
            // 输出槽
            addSlot(new Slot(blockEntity.getTaskInventory(task), 9, 62 + xOffset, 71 + yOffset));
        }
        
        // 添加玩家物品栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 142 + row * 18));
            }
        }
        
        // 添加玩家快捷栏
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 200));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.canPlayerUse(player);
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            itemStack = originalStack.copy();
            
            // 尝试合并到玩家物品栏
            if (index < 40) {
                // 从方块物品栏移动到玩家物品栏
                if (!this.insertItem(originalStack, 40, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家物品栏移动到方块物品栏
                if (!this.insertItem(originalStack, 0, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        
        return itemStack;
    }
}
