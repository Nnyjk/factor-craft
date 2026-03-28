package com.factorcraft.module.social.screen;

import com.factorcraft.module.social.market.MarketManager;
import com.factorcraft.module.social.market.TradeListing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 市场屏幕处理器
 */
public class MarketScreenHandler extends ScreenHandler {
    
    private final PlayerEntity player;
    private final MarketManager marketManager;
    
    // 当前显示的交易列表
    private List<TradeListing> listings = new ArrayList<>();
    
    // 当前页码
    private int currentPage = 0;
    private static final int LISTINGS_PER_PAGE = 8;
    
    public MarketScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }
    
    public MarketScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModScreenHandlers.MARKET, syncId);
        this.player = player;
        this.marketManager = MarketManager.getInstance();
        this.listings = marketManager.getActiveListings(player.getUuid());
        
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
    
    public MarketManager getMarketManager() {
        return marketManager;
    }
    
    public List<TradeListing> getListings() {
        return listings;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getTotalPages() {
        return (int) Math.ceil((double) listings.size() / LISTINGS_PER_PAGE);
    }
    
    public List<TradeListing> getCurrentPageListings() {
        int start = currentPage * LISTINGS_PER_PAGE;
        int end = Math.min(start + LISTINGS_PER_PAGE, listings.size());
        if (start >= listings.size()) {
            return new ArrayList<>();
        }
        return listings.subList(start, end);
    }
    
    /**
     * 翻页
     */
    public void nextPage() {
        if (currentPage < getTotalPages() - 1) {
            currentPage++;
        }
    }
    
    /**
     * 上一页
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }
    
    /**
     * 购买物品
     */
    public boolean buyListing(UUID listingId) {
        TradeListing listing = marketManager.getListing(listingId);
        if (listing == null || listing.isSold()) {
            return false;
        }
        
        return marketManager.buyListing(listingId, player.getUuid());
    }
    
    /**
     * 取消挂单
     */
    public boolean cancelListing(UUID listingId) {
        return marketManager.cancelListing(listingId, player.getUuid());
    }
    
    /**
     * 刷新列表
     */
    public void refreshListings() {
        this.listings = marketManager.getActiveListings(player.getUuid());
    }
}
