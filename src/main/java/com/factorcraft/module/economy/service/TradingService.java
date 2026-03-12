package com.factorcraft.module.economy.service;

import com.factorcraft.module.economy.config.EconomyConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TradingService {
    private final EconomyConfig config;
    private final Map<String, Trade> trades = new ConcurrentHashMap<>();
    
    public TradingService(EconomyConfig config) { this.config = config; }
    
    public String createTrade(UUID sender, UUID receiver, String offerItem, int offerAmt, String reqItem, int reqAmt) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        long exp = System.currentTimeMillis() + 86400000L;
        trades.put(id, new Trade(id, sender, receiver, offerItem, offerAmt, reqItem, reqAmt, exp));
        return id;
    }
    
    public List<Trade> incoming(UUID player) {
        return trades.values().stream().filter(t -> t.receiver().equals(player)).toList();
    }
    
    public boolean accept(String id) { return trades.remove(id) != null; }
    public void decline(String id) { trades.remove(id); }
    public double tax(double amount) { return amount * config.taxRate(); }
    
    public record Trade(String id, UUID sender, UUID receiver, String offerItem, int offerAmount, String requestItem, int requestAmount, long expiration) {}
}