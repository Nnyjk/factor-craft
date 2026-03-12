package com.factorcraft.module.economy.service;

import com.factorcraft.module.economy.config.EconomyConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionService {
    private final EconomyConfig config;
    private final Map<String, Auction> auctions = new ConcurrentHashMap<>();
    
    public AuctionService(EconomyConfig config) { this.config = config; }
    
    public String createAuction(UUID seller, String itemId, int amount, double startPrice) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        long end = System.currentTimeMillis() + config.auctionDurationHours() * 3600000L;
        auctions.put(id, new Auction(id, seller, itemId, amount, startPrice, end, null));
        return id;
    }
    
    public boolean bid(String id, UUID bidder, double amount) {
        Auction a = auctions.get(id);
        if (a == null || amount <= a.currentBid()) return false;
        auctions.put(id, new Auction(a.id(), a.seller(), a.itemId(), a.amount(), amount, a.endTime(), bidder));
        return true;
    }
    
    public List<Auction> active() {
        long now = System.currentTimeMillis();
        return auctions.values().stream().filter(a -> a.endTime() > now).toList();
    }
    
    public record Auction(String id, UUID seller, String itemId, int amount, double currentBid, long endTime, UUID highestBidder) {}
}