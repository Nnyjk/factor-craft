package com.factorcraft.module.machine.synthesizer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 合成器 GUI 容器处理器
 */
public class SynthesizerScreenHandler extends ScreenHandler {
    
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    
    // 槽位索引常量
    private static final int INPUT_START = 0;
    private static final int INPUT_END = 3;
    private static final int OUTPUT_SLOT = 4;
    private static final int PLAYER_INV_START = 5;
    private static final int PLAYER_INV_END = 40;
    
    /**
     * 服务端构造器
     */
    public SynthesizerScreenHandler(int syncId, PlayerInventory playerInventory, SynthesizerBlockEntity blockEntity) {
        this(syncId, playerInventory, blockEntity, new ArrayPropertyDelegate(2));
    }
    
    /**
     * 客户端构造器
     */
    public SynthesizerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(SynthesizerBlockEntity.INVENTORY_SIZE), new ArrayPropertyDelegate(2));
    }
    
    /**
     * 通用构造器
     */
    public SynthesizerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.FACTOR_SYNTHESIZER, syncId);
        
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        
        // 添加属性同步
        addProperties(propertyDelegate);
        
        // 添加输入槽（4个）
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                int x = 44 + col * 18;
                int y = 20 + row * 18;
                addSlot(new Slot(inventory, row * 2 + col, x, y));
            }
        }
        
        // 添加输出槽（1个）
        addSlot(new Slot(inventory, OUTPUT_SLOT, 116, 35));
        
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
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            if (slotIndex == OUTPUT_SLOT) {
                // 从输出槽移动到玩家物品栏
                if (!insertItem(originalStack, PLAYER_INV_START, PLAYER_INV_END + 1, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= PLAYER_INV_START) {
                // 从玩家物品栏移动到输入槽
                if (!insertItem(originalStack, INPUT_START, INPUT_END + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从输入槽移动到玩家物品栏
                if (!insertItem(originalStack, PLAYER_INV_START, PLAYER_INV_END + 1, false)) {
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
        return inventory.canPlayerUse(player);
    }
    
    /**
     * 获取处理进度
     */
    public int getProcessingProgress() {
        return propertyDelegate.get(0);
    }
    
    /**
     * 获取最大处理进度
     */
    public int getMaxProcessingProgress() {
        return propertyDelegate.get(1);
    }
    
    /**
     * 是否正在处理
     */
    public boolean isProcessing() {
        return getProcessingProgress() > 0;
    }
}