package com.factorcraft.module.social.exchange;

import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * Factor 价格数据
 */
public class FactorPrice {
    // 当前价格
    private int currentPrice;
    
    // 24 小时价格变化
    private int priceChange24h;
    
    // 24 小时价格变化百分比
    private double priceChangePercent;
    
    // 24 小时最高价
    private int high24h;
    
    // 24 小时最低价
    private int low24h;
    
    // 24 小时成交量
    private int volume24h;
    
    // 价格历史（最近 24 个点，每小时一个）
    private final List<Integer> priceHistory;
    
    // 最后更新时间
    private long lastUpdateTime;
    
    // 基础价格
    private static final int BASE_PRICE = 100;
    
    // 价格波动范围（百分比）
    private static final double PRICE_VOLATILITY = 0.05; // 5%
    
    public FactorPrice() {
        this.currentPrice = BASE_PRICE;
        this.priceChange24h = 0;
        this.priceChangePercent = 0.0;
        this.high24h = BASE_PRICE;
        this.low24h = BASE_PRICE;
        this.volume24h = 0;
        this.priceHistory = new ArrayList<>();
        this.lastUpdateTime = System.currentTimeMillis();
        
        // 初始化价格历史
        for (int i = 0; i < 24; i++) {
            priceHistory.add(BASE_PRICE);
        }
    }
    
    public int getCurrentPrice() {
        return currentPrice;
    }
    
    // 别名方法（用于 UI）
    public int currentPrice() {
        return currentPrice;
    }
    
    public int getPriceChange24h() {
        return priceChange24h;
    }
    
    public double getPriceChangePercent() {
        return priceChangePercent;
    }
    
    // 别名方法（用于 UI）
    public double get24hChangePercent() {
        return priceChangePercent;
    }
    
    public int getHigh24h() {
        return high24h;
    }
    
    public int getLow24h() {
        return low24h;
    }
    
    public int getVolume24h() {
        return volume24h;
    }
    
    // 别名方法（用于 UI）
    public int get24hVolume() {
        return volume24h;
    }
    
    public List<Integer> getPriceHistory() {
        return new ArrayList<>(priceHistory);
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    /**
     * 更新价格（基于供需）
     */
    public void updatePrice(int buyVolume, int sellVolume) {
        // 计算供需比
        double supplyDemandRatio = (double) buyVolume / Math.max(sellVolume, 1);
        
        // 根据供需调整价格
        double priceAdjustment;
        if (supplyDemandRatio > 1.5) {
            // 需求大于供应，价格上涨
            priceAdjustment = 1.0 + (supplyDemandRatio - 1.5) * 0.1;
        } else if (supplyDemandRatio < 0.67) {
            // 供应大于需求，价格下跌
            priceAdjustment = 1.0 - (0.67 - supplyDemandRatio) * 0.1;
        } else {
            // 供需平衡，小幅波动
            priceAdjustment = 1.0 + (Math.random() - 0.5) * PRICE_VOLATILITY;
        }
        
        // 应用价格调整
        int oldPrice = currentPrice;
        currentPrice = (int) (currentPrice * priceAdjustment);
        
        // 限制价格范围
        currentPrice = Math.max(10, Math.min(10000, currentPrice));
        
        // 更新 24 小时数据
        priceChange24h = currentPrice - priceHistory.get(0);
        priceChangePercent = (double) priceChange24h / priceHistory.get(0) * 100.0;
        high24h = Math.max(high24h, currentPrice);
        low24h = Math.min(low24h, currentPrice);
        volume24h += buyVolume + sellVolume;
        
        // 更新价格历史
        priceHistory.remove(0);
        priceHistory.add(currentPrice);
        
        // 重置 24 小时高低价（每 24 小时）
        long hoursSinceUpdate = (System.currentTimeMillis() - lastUpdateTime) / (1000 * 60 * 60);
        if (hoursSinceUpdate >= 24) {
            high24h = currentPrice;
            low24h = currentPrice;
            volume24h = 0;
        }
        
        lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * 记录交易
     */
    public void recordTrade(int quantity) {
        volume24h += quantity;
    }
    
    /**
     * 写入 NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("current_price", currentPrice);
        nbt.putInt("price_change_24h", priceChange24h);
        nbt.putDouble("price_change_percent", priceChangePercent);
        nbt.putInt("high_24h", high24h);
        nbt.putInt("low_24h", low24h);
        nbt.putInt("volume_24h", volume24h);
        nbt.putLong("last_update_time", lastUpdateTime);
        
        // 价格历史
        for (int i = 0; i < priceHistory.size(); i++) {
            nbt.putInt("price_history_" + i, priceHistory.get(i));
        }
        
        return nbt;
    }
    
    /**
     * 从 NBT 读取
     */
    public static FactorPrice fromNbt(NbtCompound nbt) {
        FactorPrice price = new FactorPrice();
        price.currentPrice = nbt.getInt("current_price");
        price.priceChange24h = nbt.getInt("price_change_24h");
        price.priceChangePercent = nbt.getDouble("price_change_percent");
        price.high24h = nbt.getInt("high_24h");
        price.low24h = nbt.getInt("low_24h");
        price.volume24h = nbt.getInt("volume_24h");
        price.lastUpdateTime = nbt.getLong("last_update_time");
        
        price.priceHistory.clear();
        for (int i = 0; i < 24; i++) {
            price.priceHistory.add(nbt.getInt("price_history_" + i));
        }
        
        return price;
    }
}
