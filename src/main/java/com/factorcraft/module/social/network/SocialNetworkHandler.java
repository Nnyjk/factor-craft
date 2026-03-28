package com.factorcraft.module.social.network;

import com.factorcraft.module.social.SocialStorage;
import com.factorcraft.module.social.exchange.ExchangeManager;
import com.factorcraft.module.social.leaderboard.LeaderboardManager;
import com.factorcraft.module.social.leaderboard.LeaderboardType;
import com.factorcraft.module.social.market.MarketManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * 社交网络包处理
 */
public class SocialNetworkHandler {
    
    /**
     * 注册所有网络包
     */
    public static void register() {
        // 注册 Payload 类型
        PayloadTypeRegistry.playS2C().register(MarketSyncPayload.ID, MarketSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ExchangeSyncPayload.ID, ExchangeSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaderboardSyncPayload.ID, LeaderboardSyncPayload.CODEC);
        
        // 注册 C2S 请求处理
        PayloadTypeRegistry.playC2S().register(MarketRequestPayload.ID, MarketRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ExchangeRequestPayload.ID, ExchangeRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LeaderboardRequestPayload.ID, LeaderboardRequestPayload.CODEC);
        
        // 注册接收器
        ServerPlayNetworking.registerGlobalReceiver(MarketRequestPayload.ID, SocialNetworkHandler::handleMarketRequest);
        ServerPlayNetworking.registerGlobalReceiver(ExchangeRequestPayload.ID, SocialNetworkHandler::handleExchangeRequest);
        ServerPlayNetworking.registerGlobalReceiver(LeaderboardRequestPayload.ID, SocialNetworkHandler::handleLeaderboardRequest);
    }
    
    /**
     * 处理市场数据请求
     */
    private static void handleMarketRequest(MarketRequestPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        MarketManager marketManager = MarketManager.getInstance();
        
        // 获取分页数据
        int page = payload.page();
        int pageSize = 20;
        var listings = marketManager.getListings(page, pageSize);
        int totalListings = marketManager.getTotalListingsCount();
        int totalPages = (totalListings + pageSize - 1) / pageSize;
        
        // 发送同步包
        MarketSyncPayload response = new MarketSyncPayload(listings, page, totalPages);
        ServerPlayNetworking.send(player, response);
    }
    
    /**
     * 处理交易所数据请求
     */
    private static void handleExchangeRequest(ExchangeRequestPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        
        // 获取订单和价格数据
        var orders = exchangeManager.getRecentOrders(20);
        var priceData = exchangeManager.getFactorPrice(payload.factorType());
        
        // 转换为 OrderData 列表
        var orderDataList = orders.stream()
            .map(order -> new ExchangeSyncPayload.OrderData(
                order.getId().toString(),
                order.getPlayerId().toString(),
                order.getPlayerName(),
                order.getFactorType(),
                order.getQuantity(),
                order.getPricePerUnit(),
                ExchangeSyncPayload.OrderType.valueOf(order.getType().name()),
                order.getTimestamp(),
                order.isComplete()
            ))
            .toList();
        
        // 发送同步包
        ExchangeSyncPayload response = new ExchangeSyncPayload(orderDataList, payload.factorType(), priceData.getCurrentPrice());
        ServerPlayNetworking.send(player, response);
    }
    
    /**
     * 处理排行榜数据请求
     */
    private static void handleLeaderboardRequest(LeaderboardRequestPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        LeaderboardType type = payload.type();
        
        // 获取排行榜数据和玩家排名
        var entries = leaderboardManager.getTopN(type, 10);
        int playerRank = leaderboardManager.getPlayerRank(player.getUuid(), type);
        
        // 转换为 EntryData 列表
        var entryDataList = entries.stream()
            .map(LeaderboardSyncPayload.EntryData::fromEntry)
            .toList();
        
        // 发送同步包
        LeaderboardSyncPayload response = new LeaderboardSyncPayload(type, entryDataList);
        ServerPlayNetworking.send(player, response);
    }
    
    /**
     * 同步市场数据给玩家
     */
    public static void syncMarketData(ServerPlayerEntity player, int page, int totalPages) {
        MarketManager marketManager = MarketManager.getInstance();
        var listings = marketManager.getListings(page, 20);
        MarketSyncPayload payload = new MarketSyncPayload(listings, page, totalPages);
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 同步交易所数据给玩家
     */
    public static void syncExchangeData(ServerPlayerEntity player, String factorType) {
        ExchangeManager exchangeManager = ExchangeManager.getInstance();
        var orders = exchangeManager.getRecentOrders(20);
        var priceData = exchangeManager.getFactorPrice(factorType);
        
        // 转换为 OrderData 列表
        var orderDataList = orders.stream()
            .map(order -> new ExchangeSyncPayload.OrderData(
                order.getId().toString(),
                order.getPlayerId().toString(),
                order.getPlayerName(),
                order.getFactorType(),
                order.getQuantity(),
                order.getPricePerUnit(),
                ExchangeSyncPayload.OrderType.valueOf(order.getType().name()),
                order.getTimestamp(),
                order.isComplete()
            ))
            .toList();
        
        ExchangeSyncPayload payload = new ExchangeSyncPayload(orderDataList, factorType, priceData.getCurrentPrice());
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 同步排行榜数据给玩家
     */
    public static void syncLeaderboardData(ServerPlayerEntity player, LeaderboardType type) {
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        var entries = leaderboardManager.getTopN(type, 10);
        int playerRank = leaderboardManager.getPlayerRank(player.getUuid(), type);
        
        // 转换为 EntryData 列表
        var entryDataList = entries.stream()
            .map(LeaderboardSyncPayload.EntryData::fromEntry)
            .toList();
        
        LeaderboardSyncPayload payload = new LeaderboardSyncPayload(type, entryDataList);
        ServerPlayNetworking.send(player, payload);
    }
}
