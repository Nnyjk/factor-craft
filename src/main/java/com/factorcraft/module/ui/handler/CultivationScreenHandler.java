package com.factorcraft.module.ui.handler;

import com.factorcraft.module.cultivation.blockentity.CultivationCoreBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;

/**
 * 培育核心屏幕处理器
 */
public class CultivationScreenHandler extends ScreenHandler {
    
    private final Inventory inventory;
    private final BlockEntity blockEntity;
    private final ScreenHandlerContext context;
    private final PropertyDelegate propertyDelegate;
    
    /**
     * 客户端构造函数
     */
    public CultivationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null, ScreenHandlerContext.EMPTY, null);
    }
    
    /**
     * 服务端构造函数
     */
    public CultivationScreenHandler(int syncId, PlayerInventory playerInventory, 
                                    BlockEntity blockEntity, ScreenHandlerContext context, 
                                    PropertyDelegate propertyDelegate) {
        super(null, syncId);
        this.inventory = new SimpleInventory(1);
        this.blockEntity = blockEntity;
        this.context = context;
        this.propertyDelegate = propertyDelegate;
        
        // 输入槽（目标物品）
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
        
        // 添加属性委托（用于进度同步）
        if (propertyDelegate != null) {
            this.addProperties(propertyDelegate);
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
                // 从 BlockEntity 槽位移出
                if (!this.insertItem(itemStack2, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 移入 BlockEntity 槽位
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
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
        if (blockEntity == null) return false;
        return !blockEntity.isRemoved();
    }
    
    /**
     * 获取当前进度
     */
    public int getProgress() {
        return propertyDelegate != null ? propertyDelegate.get(0) : 0;
    }
    
    /**
     * 获取最大进度
     */
    public int getMaxProgress() {
        return propertyDelegate != null ? propertyDelegate.get(1) : 200;
    }
    
    /**
     * 获取 BlockEntity
     */
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
