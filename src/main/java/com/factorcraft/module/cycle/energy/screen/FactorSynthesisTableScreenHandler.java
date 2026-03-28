package com.factorcraft.module.cycle.energy.screen;

import com.factorcraft.module.cycle.energy.block.entity.FactorSynthesisTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 合成台屏幕处理器
 * 
 * 管理 3x3 输入格、输出格和玩家物品栏的同步
 */
public class FactorSynthesisTableScreenHandler extends ScreenHandler {
    
    private final Inventory inventory;
    private final int[] properties;
    
    public FactorSynthesisTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, new int[2]);
    }
    
    public FactorSynthesisTableScreenHandler(int syncId, PlayerInventory playerInventory, 
                                              FactorSynthesisTableBlockEntity blockEntity,
                                              int[] properties) {
        super(FactorEnergyScreenHandlers.FACTOR_SYNTHESIS_TABLE_HANDLER, syncId);
        
        if (blockEntity != null) {
            this.inventory = blockEntity.getInventory();
            this.properties = properties;
        } else {
            this.inventory = null;
            this.properties = new int[2];
        }
        
        // 3x3 输入格
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(inventory, row * 3 + col, 30 + col * 18, 17 + row * 18));
            }
        }
        
        // 输出格
        addSlot(new Slot(inventory, 9, 124, 35));
        
        // 玩家物品栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        
        // 玩家热键栏
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            itemStack = originalStack.copy();
            
            if (index < 10) {
                // 从合成台移动到玩家物品栏
                if (!this.insertItem(originalStack, 10, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家物品栏移动到合成台
                if (!this.insertItem(originalStack, 0, 10, false)) {
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
    
    @Override
    public boolean canUse(PlayerEntity player) {
        if (inventory == null) return false;
        // Fabric 1.21.4: inventories.canPlayerUse() 不存在，改用手动距离检查
        return inventory.canPlayerUse(player);
    }
    
    public int getProgress() {
        return properties[0];
    }
    
    public int getMaxTime() {
        return properties[1];
    }
}
