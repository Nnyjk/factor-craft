package com.factorcraft.module.cycle.energy.screen;

import com.factorcraft.module.cycle.energy.block.entity.FactorReactorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 反应堆屏幕处理器
 */
public class FactorReactorScreenHandler extends ScreenHandler {
    
    private final FactorReactorBlockEntity blockEntity;
    
    public FactorReactorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(FactorEnergyScreenHandlers.FACTOR_REACTOR_HANDLER, syncId);
        this.blockEntity = null;
    }
    
    public FactorReactorScreenHandler(int syncId, PlayerInventory playerInventory, FactorReactorBlockEntity blockEntity) {
        super(FactorEnergyScreenHandlers.FACTOR_REACTOR_HANDLER, syncId);
        this.blockEntity = blockEntity;
        
        // TODO: 添加槽位
        // 输入槽：高密度 Factor
        // 冷却剂槽
        // 能量输出指示
        
        // 玩家物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // 玩家快捷栏
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            
            // TODO: 实现快速移动逻辑
            
            if (!this.insertItem(originalStack, 0, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }
            
            slot.markDirty();
        }
        
        return newStack;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        // 手动实现距离检查（Fabric 1.21.4 没有 Inventories.canPlayerUse）
        if (blockEntity == null || blockEntity.getWorld() == null) return false;
        return player.squaredDistanceTo(
            blockEntity.getPos().getX() + 0.5,
            blockEntity.getPos().getY() + 0.5,
            blockEntity.getPos().getZ() + 0.5
        ) <= 64.0;
    }
}
