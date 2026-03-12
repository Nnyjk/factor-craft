package com.factorcraft.module.economy.service;

import com.factorcraft.module.economy.config.EconomyConfig;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MarketService {
    private final EconomyConfig config;
    private final Map<String, Listing> listings = new ConcurrentHashMap<>();
    
    public MarketService(EconomyConfig config) { this.config = config; }
    
    public String createListing(UUID seller, String itemId, int amount, double price) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        listings.put(id, new Listing(id, seller, itemId, amount, price));
        return id;
    }
    
    public Optional<Listing> get(String id) { return Optional.ofNullable(listings.get(id)); }
    public List<Listing> all() { return new ArrayList<>(listings.values()); }
    public void remove(String id) { listings.remove(id); }
    
    public record Listing(String id, UUID seller, String itemId, int amount, double price) {}
}