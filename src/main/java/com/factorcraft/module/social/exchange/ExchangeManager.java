package com.factorcraft.module.social.exchange;

import com.factorcraft.FactorCraftMod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Factor 交易所管理器 - 单例模式
 */
public class ExchangeManager {
    private static ExchangeManager instance;
    
    // 所有订单（ID -> TradeOrder）
    private final Map<UUID, TradeOrder> orders = new ConcurrentHashMap<>();
    
    // 买单队列（按价格降序，时间升序）
    private final PriorityQueue<TradeOrder> buyOrders = new PriorityQueue<>(
        (a, b) -> {
            int priceCompare = Integer.compare(b.getPricePerUnit(), a.getPricePerUnit());
            if (priceCompare != 0) return priceCompare;
            return Long.compare(a.getTimestamp(), b.getTimestamp());
        }
    );
    
    // 卖单队列（按价格升序，时间升序）
    private final PriorityQueue<TradeOrder> sellOrders = new PriorityQueue<>(
        (a, b) -> {
            int priceCompare = Integer.compare(a.getPricePerUnit(), b.getPricePerUnit());
            if (priceCompare != 0) return priceCompare;
            return Long.compare(a.getTimestamp(), b.getTimestamp());
        }
    );
    
    // Factor 价格
    private final FactorPrice factorPrice;
    
    // 24 小时成交量
    private int volume24h;
    
    // 最后更新时间
    private long lastUpdateTime;
    
    private ExchangeManager() {
        this.factorPrice = new FactorPrice();
        this.volume24h = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public static ExchangeManager getInstance() {
        if (instance == null) {
            instance = new ExchangeManager();
        }
        return instance;
    }
    
    /**
     * 创建订单
     */
    public TradeOrder createOrder(UUID playerId, String playerName, String factorType, TradeOrder.OrderType type, 
                                   TradeOrder.OrderMode mode, int quantity, int pricePerUnit) {
        if (quantity <= 0) {
            return null;
        }
        
        // 市价单价格设为当前价
        if (mode == TradeOrder.OrderMode.MARKET) {
            pricePerUnit = factorPrice.getCurrentPrice();
        }
        
        UUID orderId = UUID.randomUUID();
        TradeOrder order = new TradeOrder(orderId, playerId, playerName, factorType, type, mode, quantity, pricePerUnit);
        
        orders.put(orderId, order);
        
        if (type == TradeOrder.OrderType.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        
        // 尝试撮合
        matchOrders();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Exchange] {} 创建了{}单：{} Factor @ {} (剩余：{})",
            playerName, type == TradeOrder.OrderType.BUY ? "买" : "卖", quantity, pricePerUnit, order.getRemainingQuantity());
        
        return order;
    }
    
    /**
     * 取消订单
     */
    public boolean cancelOrder(UUID orderId, UUID playerId) {
        TradeOrder order = orders.get(orderId);
        if (order == null) {
            return false;
        }
        
        if (!order.getPlayerId().equals(playerId)) {
            return false;
        }
        
        if (order.isComplete()) {
            return false;
        }
        
        order.cancel();
        buyOrders.remove(order);
        sellOrders.remove(order);
        
        return true;
    }
    
    /**
     * 撮合订单
     */
    private void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            TradeOrder buyOrder = buyOrders.peek();
            TradeOrder sellOrder = sellOrders.peek();
            
            if (buyOrder == null || sellOrder == null || buyOrder.isComplete() || sellOrder.isComplete()) {
                break;
            }
            
            // 检查价格是否匹配（买价 >= 卖价）
            if (buyOrder.getPricePerUnit() < sellOrder.getPricePerUnit()) {
                break;
            }
            
            // 撮合交易
            int matchQuantity = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
            int tradePrice = sellOrder.getPricePerUnit(); // 以卖价成交
            
            buyOrder.fill(matchQuantity);
            sellOrder.fill(matchQuantity);
            factorPrice.recordTrade(matchQuantity);
            volume24h += matchQuantity;
            
            FactorCraftMod.LOGGER.info("[FactorCraft:Exchange] 撮合交易：{} Factor @ {} (买：{} / 卖：{})",
                matchQuantity, tradePrice, buyOrder.getPlayerName(), sellOrder.getPlayerName());
            
            // 移除已完成的订单
            if (buyOrder.isComplete()) {
                buyOrders.poll();
            }
            if (sellOrder.isComplete()) {
                sellOrders.poll();
            }
        }
        
        // 更新价格
        factorPrice.updatePrice(getBuyVolume(), getSellVolume());
    }
    
    /**
     * 获取买单总量
     */
    private int getBuyVolume() {
        return buyOrders.stream()
            .mapToInt(TradeOrder::getRemainingQuantity)
            .sum();
    }
    
    /**
     * 获取卖单总量
     */
    private int getSellVolume() {
        return sellOrders.stream()
            .mapToInt(TradeOrder::getRemainingQuantity)
            .sum();
    }
    
    /**
     * 获取 Factor 当前价格
     */
    public int getCurrentPrice() {
        return factorPrice.getCurrentPrice();
    }
    
    /**
     * 获取最近 N 个订单
     */
    public List<TradeOrder> getRecentOrders(int limit) {
        return orders.values().stream()
            .sorted(Comparator.comparingLong(TradeOrder::getTimestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取 Factor 价格对象
     */
    public FactorPrice getFactorPrice() {
        return factorPrice;
    }
    
    /**
     * 获取 Factor 价格对象（按类型）
     */
    public FactorPrice getFactorPrice(String factorType) {
        // 当前只支持一种 Factor 类型，后续可扩展
        return factorPrice;
    }
    
    /**
     * 获取玩家的有效订单
     */
    public List<TradeOrder> getActiveOrders(UUID playerId) {
        return orders.values().stream()
            .filter(order -> order.getPlayerId().equals(playerId) && !order.isComplete())
            .collect(Collectors.toList());
    }
    
    /**
     * 获取价格数据（用于同步）
     */
    public FactorPrice getPriceData() {
        return factorPrice;
    }
    
    /**
     * 获取所有有效订单
     */
    public List<TradeOrder> getAllOrders() {
        return orders.values().stream()
            .filter(order -> !order.isComplete())
            .collect(Collectors.toList());
    }
    
    /**
     * 获取玩家的订单
     */
    public List<TradeOrder> getPlayerOrders(UUID playerId) {
        return orders.values().stream()
            .filter(order -> order.getPlayerId().equals(playerId) && !order.isComplete())
            .collect(Collectors.toList());
    }
    
    /**
     * 获取买单队列（前 N 个）
     */
    public List<TradeOrder> getTopBuyOrders(int n) {
        return buyOrders.stream().limit(n).collect(Collectors.toList());
    }
    
    /**
     * 获取卖单队列（前 N 个）
     */
    public List<TradeOrder> getTopSellOrders(int n) {
        return sellOrders.stream().limit(n).collect(Collectors.toList());
    }
    
    /**
     * 获取 24 小时成交量
     */
    public int getVolume24h() {
        return volume24h;
    }
    
    /**
     * 清理已完成订单
     */
    public void cleanupCompletedOrders() {
        List<UUID> toRemove = orders.entrySet().stream()
            .filter(e -> e.getValue().isComplete())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        for (UUID orderId : toRemove) {
            orders.remove(orderId);
        }
    }
}
