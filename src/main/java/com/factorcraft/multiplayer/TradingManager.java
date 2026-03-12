package com.factorcraft.multiplayer;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

/**
 * Factor 交易系统
 * 允许玩家之间交易 Factor 物品
 */
public class TradingManager {
    private static final Map<UUID, List<TradeOffer>> PLAYER_TRADES = new HashMap<>();
    private static final Map<String, TradeOffer> ACTIVE_TRADES = new HashMap<>();
    private static final int MAX_TRADES_PER_PLAYER = 10;
    private static final int TRADE_DURATION_HOURS = 24;
    
    /**
     * 创建交易报价
     */
    public static String createTradeOffer(ServerPlayerEntity player, ItemStack offeredItem, int factorAmount) {
        UUID playerId = player.getUuid();
        
        // 检查数量限制
        List<TradeOffer> playerTrades = PLAYER_TRADES.computeIfAbsent(playerId, k -> new ArrayList<>());
        if (playerTrades.size() >= MAX_TRADES_PER_PLAYER) {
            player.sendMessage(Text.literal("§c你已达到最大交易数量限制"), false);
            return null;
        }
        
        // 检查库存
        if (!hasItem(player, offeredItem)) {
            player.sendMessage(Text.literal("§c你没有足够的物品"), false);
            return null;
        }
        
        // 创建交易
        String tradeId = UUID.randomUUID().toString().substring(0, 8);
        TradeOffer trade = new TradeOffer(
            tradeId,
            playerId,
            player.getName().getString(),
            offeredItem,
            factorAmount,
            System.currentTimeMillis(),
            System.currentTimeMillis() + TRADE_DURATION_HOURS * 3600 * 1000L
        );
        
        playerTrades.add(trade);
        ACTIVE_TRADES.put(tradeId, trade);
        
        player.sendMessage(Text.literal("§a已创建交易: " + offeredItem.getName().getString() + " x" + offeredItem.getCount()), false);
        return tradeId;
    }
    
    /**
     * 接受交易
     */
    public static boolean acceptTrade(ServerPlayerEntity buyer, String tradeId) {
        TradeOffer trade = ACTIVE_TRADES.get(tradeId);
        if (trade == null) {
            buyer.sendMessage(Text.literal("§c交易不存在或已过期"), false);
            return false;
        }
        
        if (trade.isExpired()) {
            ACTIVE_TRADES.remove(tradeId);
            buyer.sendMessage(Text.literal("§c交易已过期"), false);
            return false;
        }
        
        // TODO: 检查买家 Factor 点数
        // if (getFactorPoints(buyer) < trade.factorAmount()) {
        //     buyer.sendMessage(Text.literal("§cFactor 点数不足"), false);
        //     return false;
        // }
        
        // 执行交易
        // 移除卖家的物品
        // 移除买家的 Factor 点数
        // 给买家物品
        // 给卖家 Factor 点数
        
        // 清理交易
        ACTIVE_TRADES.remove(tradeId);
        List<TradeOffer> sellerTrades = PLAYER_TRADES.get(trade.sellerId());
        if (sellerTrades != null) {
            sellerTrades.remove(trade);
        }
        
        buyer.sendMessage(Text.literal("§a交易成功! 获得 " + trade.offeredItem().getName().getString()), false);
        return true;
    }
    
    /**
     * 取消交易
     */
    public static boolean cancelTrade(ServerPlayerEntity player, String tradeId) {
        TradeOffer trade = ACTIVE_TRADES.get(tradeId);
        if (trade == null || !trade.sellerId().equals(player.getUuid())) {
            player.sendMessage(Text.literal("§c无法取消该交易"), false);
            return false;
        }
        
        ACTIVE_TRADES.remove(tradeId);
        List<TradeOffer> playerTrades = PLAYER_TRADES.get(player.getUuid());
        if (playerTrades != null) {
            playerTrades.remove(trade);
        }
        
        player.sendMessage(Text.literal("§a已取消交易"), false);
        return true;
    }
    
    /**
     * 获取所有可用交易
     */
    public static List<TradeOffer> getAvailableTrades() {
        List<TradeOffer> trades = new ArrayList<>();
        for (TradeOffer trade : ACTIVE_TRADES.values()) {
            if (!trade.isExpired()) {
                trades.add(trade);
            }
        }
        return trades;
    }
    
    /**
     * 获取玩家的交易
     */
    public static List<TradeOffer> getPlayerTrades(UUID playerId) {
        return PLAYER_TRADES.getOrDefault(playerId, Collections.emptyList());
    }
    
    /**
     * 清理过期交易
     */
    public static void cleanExpiredTrades() {
        ACTIVE_TRADES.entrySet().removeIf(entry -> entry.getValue().isExpired());
        PLAYER_TRADES.values().forEach(trades -> 
            trades.removeIf(TradeOffer::isExpired)
        );
    }
    
    private static boolean hasItem(ServerPlayerEntity player, ItemStack item) {
        return player.getInventory().count(item.getItem()) >= item.getCount();
    }
}

/**
 * 交易报价
 */
record TradeOffer(
    String id,
    UUID sellerId,
    String sellerName,
    ItemStack offeredItem,
    int factorAmount,
    long createdTime,
    long expireTime
) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
    
    public long getRemainingTime() {
        return Math.max(0, expireTime - System.currentTimeMillis());
    }
}