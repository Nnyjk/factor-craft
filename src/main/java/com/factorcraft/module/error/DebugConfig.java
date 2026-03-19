package com.factorcraft.module.error;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 调试配置
 * 
 * 可配置的日志级别和调试开关
 */
public class DebugConfig {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "factorcraft", "debug.json");
    
    // 调试开关
    private static boolean debugEnabled = false;
    private static boolean machineDebugEnabled = false;
    private static boolean networkDebugEnabled = false;
    private static boolean questDebugEnabled = false;
    private static boolean performanceDebugEnabled = false;
    
    // 日志级别
    private static LogLevel globalLogLevel = LogLevel.INFO;
    
    /**
     * 日志级别枚举
     */
    public enum LogLevel {
        TRACE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4),
        OFF(5);
        
        private final int level;
        
        LogLevel(int level) {
            this.level = level;
        }
        
        public boolean shouldLog(LogLevel threshold) {
            return this.level >= threshold.level;
        }
    }
    
    /**
     * 加载配置
     */
    public static void load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                BufferedReader reader = Files.newBufferedReader(CONFIG_PATH);
                JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                
                debugEnabled = config.getAsJsonPrimitive("debugEnabled").getAsBoolean();
                machineDebugEnabled = config.getAsJsonPrimitive("machineDebug").getAsBoolean();
                networkDebugEnabled = config.getAsJsonPrimitive("networkDebug").getAsBoolean();
                questDebugEnabled = config.getAsJsonPrimitive("questDebug").getAsBoolean();
                performanceDebugEnabled = config.getAsJsonPrimitive("performanceDebug").getAsBoolean();
                
                String levelStr = config.getAsJsonPrimitive("logLevel").getAsString();
                globalLogLevel = LogLevel.valueOf(levelStr.toUpperCase());
            } else {
                // 创建默认配置
                save();
            }
        } catch (Exception e) {
            // 使用默认值
            debugEnabled = false;
        }
    }
    
    /**
     * 保存配置
     */
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            
            JsonObject config = new JsonObject();
            config.addProperty("debugEnabled", debugEnabled);
            config.addProperty("machineDebug", machineDebugEnabled);
            config.addProperty("networkDebug", networkDebugEnabled);
            config.addProperty("questDebug", questDebugEnabled);
            config.addProperty("performanceDebug", performanceDebugEnabled);
            config.addProperty("logLevel", globalLogLevel.name());
            
            BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH);
            GSON.toJson(config, writer);
            writer.close();
        } catch (Exception e) {
            // 忽略保存错误
        }
    }
    
    /**
     * 重载配置
     */
    public static void reload() {
        load();
    }
    
    // Getters
    
    public static boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    public static boolean isMachineDebugEnabled() {
        return debugEnabled || machineDebugEnabled;
    }
    
    public static boolean isNetworkDebugEnabled() {
        return debugEnabled || networkDebugEnabled;
    }
    
    public static boolean isQuestDebugEnabled() {
        return debugEnabled || questDebugEnabled;
    }
    
    public static boolean isPerformanceDebugEnabled() {
        return debugEnabled || performanceDebugEnabled;
    }
    
    public static LogLevel getGlobalLogLevel() {
        return globalLogLevel;
    }
    
    // Setters
    
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
        save();
    }
    
    public static void setMachineDebugEnabled(boolean enabled) {
        machineDebugEnabled = enabled;
        save();
    }
    
    public static void setNetworkDebugEnabled(boolean enabled) {
        networkDebugEnabled = enabled;
        save();
    }
    
    public static void setQuestDebugEnabled(boolean enabled) {
        questDebugEnabled = enabled;
        save();
    }
    
    public static void setPerformanceDebugEnabled(boolean enabled) {
        performanceDebugEnabled = enabled;
        save();
    }
    
    public static void setGlobalLogLevel(LogLevel level) {
        globalLogLevel = level;
        save();
    }
}