package com.factorcraft.module.quest.ui;

import com.factorcraft.module.quest.manager.QuestManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * 任务追踪界面处理器
 */
public class QuestTrackerScreenHandler extends ScreenHandler {
    
    private final QuestManager questManager;
    private final PlayerEntity player;
    
    public QuestTrackerScreenHandler(int syncId, PlayerInventory playerInventory, QuestManager questManager) {
        super(null, syncId);
        this.questManager = questManager;
        this.player = playerInventory.player;
        
        // 添加玩家物品栏
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 184 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 242));
        }
    }
    
    public QuestManager getQuestManager() {
        return questManager;
    }
    
    public PlayerEntity getPlayer() {
        return player;
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
