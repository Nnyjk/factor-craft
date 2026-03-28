package com.factorcraft.module.ui.achievement;

import com.factorcraft.module.core.init.CoreScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * 成就树 ScreenHandler
 * 用于成就界面的服务端逻辑
 */
public class AchievementTreeScreenHandler extends ScreenHandler {
    
    public AchievementTreeScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(CoreScreenHandlers.ACHIEVEMENT_TREE, syncId);
        
        // 添加玩家物品栏槽位 (仅用于同步，成就界面不使用物品栏)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 142 + row * 18));
            }
        }
        
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 200));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public net.minecraft.item.ItemStack quickMove(PlayerEntity player, int slot) {
        // 成就界面不支持快速移动物品
        return net.minecraft.item.ItemStack.EMPTY;
    }
    
    /**
     * 创建客户端实例的工厂方法
     */
    public static AchievementTreeScreenHandler createClient(int syncId, PlayerInventory playerInventory) {
        return new AchievementTreeScreenHandler(syncId, playerInventory);
    }
}
