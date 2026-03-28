package com.factorcraft.module.social.market;

import com.factorcraft.FactorCraftMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 市场管理器 - 单例模式
 */
public class MarketManager {
    private static MarketManager instance;
    
    // 所有挂单（ID -> TradeListing）
    private final Map<UUID, TradeListing> listings = new ConcurrentHashMap<>();
    
    // 玩家挂单索引（PlayerUUID -> List<ListingID>）
    private final Map<UUID, List<UUID>> playerListings = new ConcurrentHashMap<>();
    
    // 物品挂单索引（ItemName -> List<ListingID>）
    private final Map<String, List<UUID>> itemIndex = new ConcurrentHashMap<>();
    
    // 服务器实例
    private MinecraftServer server;
    
    private MarketManager() {
    }
    
    public static MarketManager getInstance() {
        if (instance == null) {
            instance = new MarketManager();
        }
        return instance;
    }
    
    public void setServer(MinecraftServer server) {
        this.server = server;
    }
    
    /**
     * 创建新挂单
     */
    public TradeListing createListing(ServerPlayerEntity seller, ItemStack itemStack, int quantity, int pricePerUnit) {
        MarketConfig config = MarketConfig.getInstance();
        
        // 验证价格
        if (pricePerUnit < config.getMinPrice() || pricePerUnit > config.getMaxPrice()) {
            return null;
        }
        
        // 验证数量
        if (quantity <= 0) {
            return null;
        }
        
        // 验证玩家挂单数量限制
        List<UUID> playerListingIds = playerListings.computeIfAbsent(seller.getUuid(), k -> new ArrayList<>());
        if (playerListingIds.size() >= config.getMaxListingsPerPlayer()) {
            return null;
        }
        
        // 创建挂单
        UUID listingId = UUID.randomUUID();
        TradeListing listing = new TradeListing(
            listingId,
            seller.getUuid(),
            seller.getName().getString(),
            itemStack,
            quantity,
            pricePerUnit
        );
        
        // 添加到索引
        listings.put(listingId, listing);
        playerListingIds.add(listingId);
        
        String itemName = listing.getItemIdentifier();
        itemIndex.computeIfAbsent(itemName, k -> new ArrayList<>()).add(listingId);
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Market] {} 创建了挂单：{} x {} @ {} Factor",
            seller.getName().getString(), quantity, itemName, pricePerUnit);
        
        return listing;
    }
    
    /**
     * 添加已有挂单（用于从 NBT 加载）
     */
    public void addListing(TradeListing listing) {
        UUID listingId = listing.getId();
        UUID sellerId = listing.getSellerId();
        
        listings.put(listingId, listing);
        playerListings.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(listingId);
        
        String itemName = listing.getItemIdentifier();
        itemIndex.computeIfAbsent(itemName, k -> new ArrayList<>()).add(listingId);
    }
    
    /**
     * 取消挂单
     */
    public boolean cancelListing(UUID listingId, UUID playerId) {
        TradeListing listing = listings.get(listingId);
        if (listing == null) {
            return false;
        }
        
        if (!listing.getSellerId().equals(playerId)) {
            return false;
        }
        
        if (listing.isSold()) {
            return false;
        }
        
        listing.setSold(true);
        listings.remove(listingId);
        
        playerListings.computeIfPresent(playerId, (k, v) -> {
            v.remove(listingId);
            return v.isEmpty() ? null : v;
        });
        
        String itemName = listing.getItemIdentifier();
        itemIndex.computeIfPresent(itemName, (k, v) -> {
            v.remove(listingId);
            return v.isEmpty() ? null : v;
        });
        
        return true;
    }
    
    /**
     * 购买物品
     */
    public TradeListing purchaseListing(UUID listingId, ServerPlayerEntity buyer) {
        TradeListing listing = listings.get(listingId);
        if (listing == null) {
            return null;
        }
        
        if (listing.isSold()) {
            return null;
        }
        
        if (listing.getSellerId().equals(buyer.getUuid())) {
            return null; // 不能购买自己的物品
        }
        
        // TODO: 验证买家 Factor 余额
        // TODO: 扣除买家 Factor
        // TODO: 给卖家转账（扣除税费）
        
        listing.setSold(true);
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Market] {} 购买了 {} 的物品：{} x {} @ {} Factor",
            buyer.getName().getString(), listing.getSellerName(),
            listing.getItemIdentifier(), listing.getQuantity(), listing.getPricePerUnit());
        
        return listing;
    }
    
    /**
     * 获取所有有效挂单
     */
    public List<TradeListing> getAllListings() {
        return listings.values().stream()
            .filter(listing -> !listing.isSold())
            .collect(Collectors.toList());
    }
    
    /**
     * 根据物品搜索挂单
     */
    public List<TradeListing> searchByItem(String itemName) {
        List<UUID> listingIds = itemIndex.get(itemName);
        if (listingIds == null) {
            return Collections.emptyList();
        }
        
        return listingIds.stream()
            .map(listings::get)
            .filter(listing -> listing != null && !listing.isSold())
            .collect(Collectors.toList());
    }
    
    /**
     * 获取玩家的所有挂单
     */
    public List<TradeListing> getPlayerListings(UUID playerId) {
        List<UUID> listingIds = playerListings.get(playerId);
        if (listingIds == null) {
            return Collections.emptyList();
        }
        
        return listingIds.stream()
            .map(listings::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取分页挂单列表
     */
    public List<TradeListing> getListings(int page, int pageSize) {
        List<TradeListing> activeListings = listings.values().stream()
            .filter(listing -> !listing.isSold())
            .collect(Collectors.toList());
        
        int startIndex = page * pageSize;
        if (startIndex >= activeListings.size()) {
            return Collections.emptyList();
        }
        
        int endIndex = Math.min(startIndex + pageSize, activeListings.size());
        return activeListings.subList(startIndex, endIndex);
    }
    
    /**
     * 获取总挂单数量
     */
    public int getTotalListingsCount() {
        return (int) listings.values().stream().filter(listing -> !listing.isSold()).count();
    }
    
    /**
     * 清理过期挂单
     */
    public void cleanupExpiredListings() {
        MarketConfig config = MarketConfig.getInstance();
        long expirationMillis = config.getListingExpirationHours() * 60L * 60L * 1000L;
        long now = System.currentTimeMillis();
        
        List<UUID> toRemove = new ArrayList<>();
        for (TradeListing listing : listings.values()) {
            if (!listing.isSold() && (now - listing.getTimestamp()) > expirationMillis) {
                toRemove.add(listing.getId());
            }
        }
        
        for (UUID listingId : toRemove) {
            TradeListing listing = listings.get(listingId);
            if (listing != null) {
                FactorCraftMod.LOGGER.info("[FactorCraft:Market] 清理过期挂单：{} - {}",
                    listing.getSellerName(), listing.getItemIdentifier());
                cancelListing(listingId, listing.getSellerId());
            }
        }
    }
    
    /**
     * 获取挂单数量
     */
    public int getListingCount() {
        return (int) listings.values().stream().filter(l -> !l.isSold()).count();
    }
    
    /**
     * 获取玩家的所有活跃挂单
     */
    public List<TradeListing> getActiveListings(UUID playerId) {
        List<UUID> listingIds = playerListings.get(playerId);
        if (listingIds == null) {
            return Collections.emptyList();
        }
        
        return listingIds.stream()
            .map(listings::get)
            .filter(l -> l != null && !l.isSold())
            .collect(Collectors.toList());
    }
    
    /**
     * 根据 ID 获取挂单
     */
    public TradeListing getListing(UUID listingId) {
        return listings.get(listingId);
    }
    
    /**
     * 购买挂单
     */
    public boolean buyListing(UUID listingId, UUID buyerId) {
        TradeListing listing = listings.get(listingId);
        if (listing == null || listing.isSold()) {
            return false;
        }
        listing.setSold(true);
        return true;
    }
    
    /**
     * 清空所有数据（用于加载新数据）
     */
    public void clear() {
        listings.clear();
        playerListings.clear();
        itemIndex.clear();
    }
}
