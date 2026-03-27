package com.factorcraft.module.social.market;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.*;

/**
 * 市场配置
 */
public class MarketConfig {
    private static MarketConfig instance;
    
    // 市场税费（百分比，0-100）
    private int marketTaxRate = 5;
    
    // 最大挂单数量（每玩家）
    private int maxListingsPerPlayer = 10;
    
    // 挂单有效期（小时）
    private int listingExpirationHours = 168; // 7 天
    
    // 最低价格
    private int minPrice = 1;
    
    // 最高价格
    private int maxPrice = 1000000;
    
    // 市场刷新间隔（tick）
    private int marketRefreshInterval = 200; // 10 秒
    
    private MarketConfig() {
        load();
    }
    
    public static MarketConfig getInstance() {
        if (instance == null) {
            instance = new MarketConfig();
        }
        return instance;
    }
    
    public int getMarketTaxRate() {
        return marketTaxRate;
    }
    
    public int getMaxListingsPerPlayer() {
        return maxListingsPerPlayer;
    }
    
    public int getListingExpirationHours() {
        return listingExpirationHours;
    }
    
    public int getMinPrice() {
        return minPrice;
    }
    
    public int getMaxPrice() {
        return maxPrice;
    }
    
    public int getMarketRefreshInterval() {
        return marketRefreshInterval;
    }
    
    /**
     * 计算税费
     */
    public int calculateTax(int amount) {
        return (int) Math.ceil(amount * marketTaxRate / 100.0);
    }
    
    /**
     * 计算税后金额
     */
    public int calculateAfterTax(int amount) {
        return amount - calculateTax(amount);
    }
    
    /**
     * 加载配置
     */
    public void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("factor-craft").resolve("market.json");
        
        if (!Files.exists(configPath)) {
            save();
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            
            // 简单 JSON 解析
            String content = json.toString();
            marketTaxRate = extractInt(content, "market_tax_rate", 5);
            maxListingsPerPlayer = extractInt(content, "max_listings_per_player", 10);
            listingExpirationHours = extractInt(content, "listing_expiration_hours", 168);
            minPrice = extractInt(content, "min_price", 1);
            maxPrice = extractInt(content, "max_price", 1000000);
            marketRefreshInterval = extractInt(content, "market_refresh_interval", 200);
            
        } catch (IOException e) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Market] 无法读取市场配置，使用默认值", e);
        }
    }
    
    /**
     * 保存配置
     */
    public void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("factor-craft").resolve("market.json");
        
        String json = String.format(
            "{\n" +
            "  \"market_tax_rate\": %d,\n" +
            "  \"max_listings_per_player\": %d,\n" +
            "  \"listing_expiration_hours\": %d,\n" +
            "  \"min_price\": %d,\n" +
            "  \"max_price\": %d,\n" +
            "  \"market_refresh_interval\": %d\n" +
            "}",
            marketTaxRate, maxListingsPerPlayer, listingExpirationHours, minPrice, maxPrice, marketRefreshInterval
        );
        
        try {
            Files.write(configPath, json.getBytes());
            FactorCraftMod.LOGGER.info("[FactorCraft:Market] 市场配置已保存");
        } catch (IOException e) {
            FactorCraftMod.LOGGER.warn("[FactorCraft:Market] 无法保存市场配置", e);
        }
    }
    
    private int extractInt(String json, String key, int defaultValue) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return defaultValue;
            
            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return defaultValue;
            
            int start = colonIndex + 1;
            while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
                start++;
            }
            
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
                end++;
            }
            
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
