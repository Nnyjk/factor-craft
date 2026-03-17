package com.factorcraft.network;

import com.factorcraft.FactorCraftMod;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端配置缓存
 * 
 * 存储从服务端同步的配置数据
 */
public class ClientConfigCache {
    
    private static final Map<String, JsonObject> CACHE = new HashMap<>();
    
    /**
     * 缓存配置
     */
    public static void cacheConfig(String configName, JsonObject config) {
        CACHE.put(configName, config);
        FactorCraftMod.LOGGER.debug("[ClientConfig] 缓存配置：{}", configName);
    }
    
    /**
     * 获取配置
     */
    public static JsonObject getConfig(String configName) {
        return CACHE.get(configName);
    }
    
    /**
     * 检查配置是否存在
     */
    public static boolean hasConfig(String configName) {
        return CACHE.containsKey(configName);
    }
    
    /**
     * 获取配置值
     */
    public static double getDouble(String configName, String key, double defaultValue) {
        JsonObject config = getConfig(configName);
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsDouble();
    }
    
    /**
     * 获取配置值
     */
    public static int getInt(String configName, String key, int defaultValue) {
        JsonObject config = getConfig(configName);
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsInt();
    }
    
    /**
     * 获取配置值
     */
    public static String getString(String configName, String key, String defaultValue) {
        JsonObject config = getConfig(configName);
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsString();
    }
    
    /**
     * 获取配置值
     */
    public static boolean getBoolean(String configName, String key, boolean defaultValue) {
        JsonObject config = getConfig(configName);
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).getAsBoolean();
    }
    
    /**
     * 清除缓存（断开连接时调用）
     */
    public static void clear() {
        CACHE.clear();
        FactorCraftMod.LOGGER.debug("[ClientConfig] 缓存已清除");
    }
    
    /**
     * 获取缓存的配置数量
     */
    public static int size() {
        return CACHE.size();
    }
}
