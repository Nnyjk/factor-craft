package com.factorcraft.module.technology.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * Factor 提取器屏幕处理器 - Fabric 1.21.4
 */
public class FactorExtractorScreenHandler extends ScreenHandler {
    
    public FactorExtractorScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreens.FACTOR_EXTRACTOR, syncId);
        
        // 添加玩家物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // 简化版本：不实现快速移动
        return ItemStack.EMPTY;
    }
}