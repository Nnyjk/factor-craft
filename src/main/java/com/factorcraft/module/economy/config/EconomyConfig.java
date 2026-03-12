package com.factorcraft.module.economy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;

public record EconomyConfig(
    boolean enabled,
    double taxRate,
    int auctionDurationHours,
    int maxListingsPerPlayer,
    double minPrice,
    double maxPrice
) {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = Path.of("config/factorcraft/economy.json");
    
    public static EconomyConfig load() {
        try {
            if (Files.exists(PATH)) return GSON.fromJson(Files.readString(PATH), EconomyConfig.class);
        } catch (Exception e) {}
        return new EconomyConfig(false, 0.05, 24, 10, 1.0, 1000000.0);
    }
}