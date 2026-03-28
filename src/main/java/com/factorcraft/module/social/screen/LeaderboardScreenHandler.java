package com.factorcraft.module.social.screen;

import com.factorcraft.module.social.leaderboard.LeaderboardEntry;
import com.factorcraft.module.social.leaderboard.LeaderboardManager;
import com.factorcraft.module.social.leaderboard.LeaderboardType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * 排行榜屏幕处理器
 */
public class LeaderboardScreenHandler extends ScreenHandler {
    
    private final PlayerEntity player;
    private final LeaderboardManager leaderboardManager;
    
    // 当前显示的排行榜条目
    private List<LeaderboardEntry> entries = new ArrayList<>();
    
    // 当前选中的排行榜类型
    private LeaderboardType currentType = LeaderboardType.FACTOR_COLLECTOR;
    
    public LeaderboardScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }
    
    public LeaderboardScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModScreenHandlers.LEADERBOARD, syncId);
        this.player = player;
        this.leaderboardManager = LeaderboardManager.getInstance();
        this.entries = leaderboardManager.getTopN(currentType, 10);
        
        // 添加玩家物品栏槽位
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 166 + i * 18));
            }
        }
        
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 224));
        }
    }
    
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
    
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }
    
    public PlayerEntity getPlayer() {
        return player;
    }
    
    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }
    
    public List<LeaderboardEntry> getEntries() {
        return entries;
    }
    
    public LeaderboardType getCurrentType() {
        return currentType;
    }
    
    public void setCurrentType(LeaderboardType type) {
        this.currentType = type;
        this.entries = leaderboardManager.getTopN(type, 10);
    }
    
    /**
     * 获取所有排行榜类型
     */
    public LeaderboardType[] getTypes() {
        return LeaderboardType.values();
    }
    
    /**
     * 刷新排行榜
     */
    public void refreshEntries() {
        this.entries = leaderboardManager.getTopN(currentType, 10);
    }
    
    /**
     * 获取玩家的排名
     */
    public int getPlayerRank() {
        return leaderboardManager.getPlayerRank(player.getUuid(), currentType);
    }
}
