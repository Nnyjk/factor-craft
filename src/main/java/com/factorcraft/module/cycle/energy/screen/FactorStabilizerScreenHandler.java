package com.factorcraft.module.cycle.energy.screen;

import com.factorcraft.module.cycle.energy.block.FactorStabilizerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 稳定器屏幕处理器
 * 
 * 稳定器没有物品栏，只显示 Factor 存储状态
 */
public class FactorStabilizerScreenHandler extends ScreenHandler {
    
    private final FactorStabilizerBlockEntity blockEntity;
    
    public FactorStabilizerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }
    
    public FactorStabilizerScreenHandler(int syncId, PlayerInventory playerInventory, FactorStabilizerBlockEntity blockEntity) {
        super(FactorEnergyScreenHandlers.FACTOR_STABILIZER_HANDLER, syncId);
        this.blockEntity = blockEntity;
        
        // 添加玩家物品栏
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        // 添加玩家热键栏
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY; // 稳定器没有物品栏，不支持快速移动
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        if (blockEntity == null || blockEntity.getWorld() == null) return false;
        
        double distance = player.squaredDistanceTo(
            blockEntity.getPos().getX() + 0.5,
            blockEntity.getPos().getY() + 0.5,
            blockEntity.getPos().getZ() + 0.5
        );
        
        return distance <= 64.0;
    }
    
    /**
     * 获取 Factor 存储量
     */
    public int getFactorAmount() {
        return blockEntity != null ? blockEntity.getFactorAmount() : 0;
    }
    
    /**
     * 获取最大 Factor 存储量
     */
    public int getMaxFactor() {
        return blockEntity != null ? blockEntity.getMaxFactor() : 10000;
    }
    
    /**
     * 是否正在稳定
     */
    public boolean isStabilizing() {
        return blockEntity != null && blockEntity.isStabilizing();
    }
}
