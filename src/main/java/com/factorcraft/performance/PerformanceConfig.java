package com.factorcraft.performance;

import com.factorcraft.FactorCraftMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * 服务器性能优化配置
 * 
 * 管理所有性能相关的配置项，包括：
 * - Factor 计算优化
 * - 实体激活范围
 * - 网络同步优化
 * - 内存管理
 */
public class PerformanceConfig {
    
    private static final String CONFIG_FILE = "config/factorcraft-performance.properties";
    private static PerformanceConfig instance;
    
    private final Properties properties = new Properties();
    
    // Factor 计算优化
    public boolean enableIncrementalCalculation = true;
    public int maxChunksPerTick = 10;
    public int calculationCacheSize = 1000;
    public double calculationCacheExpirySeconds = 30.0;
    
    // 实体激活范围
    public boolean enableEntityActivationRange = true;
    public double entityActivationRangeVeryNear = 16.0;
    public double entityActivationRangeNear = 32.0;
    public double entityActivationRangeNormal = 64.0;
    public double entityActivationRangeFar = 96.0;
    
    // 网络同步优化
    public boolean enableBatchedSync = true;
    public int batchSize = 20;
    public int syncIntervalMs = 100;
    public double syncPriorityThreshold = 0.5;
    
    // 内存管理
    public boolean enableObjectPooling = true;
    public int poolSize = 100;
    public boolean enableMemoryMonitoring = true;
    public long memoryCheckIntervalMs = 60000;
    
    // 性能分析
    public boolean enableProfiler = true;
    public int profilerSampleIntervalMs = 1000;
    public String profilerOutputDir = "logs/factorcraft-profiler";
    
    private PerformanceConfig() {
        loadConfig();
    }
    
    public static PerformanceConfig getInstance() {
        if (instance == null) {
            instance = new PerformanceConfig();
        }
        return instance;
    }
    
    /**
     * 加载配置文件
     */
    private void loadConfig() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try (InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
                loadProperties();
                FactorCraftMod.LOGGER.info("Performance config loaded from {}", CONFIG_FILE);
            } catch (IOException e) {
                FactorCraftMod.LOGGER.error("Failed to load performance config", e);
                saveDefaultConfig();
            }
        } else {
            saveDefaultConfig();
        }
    }
    
    /**
     * 从 Properties 加载配置值
     */
    private void loadProperties() {
        // Factor 计算优化
        enableIncrementalCalculation = getBoolean("factor.incrementalCalculation", enableIncrementalCalculation);
        maxChunksPerTick = getInt("factor.maxChunksPerTick", maxChunksPerTick);
        calculationCacheSize = getInt("factor.calculationCacheSize", calculationCacheSize);
        calculationCacheExpirySeconds = getDouble("factor.calculationCacheExpirySeconds", calculationCacheExpirySeconds);
        
        // 实体激活范围
        enableEntityActivationRange = getBoolean("entity.enableActivationRange", enableEntityActivationRange);
        entityActivationRangeVeryNear = getDouble("entity.rangeVeryNear", entityActivationRangeVeryNear);
        entityActivationRangeNear = getDouble("entity.rangeNear", entityActivationRangeNear);
        entityActivationRangeNormal = getDouble("entity.rangeNormal", entityActivationRangeNormal);
        entityActivationRangeFar = getDouble("entity.rangeFar", entityActivationRangeFar);
        
        // 网络同步优化
        enableBatchedSync = getBoolean("network.enableBatchedSync", enableBatchedSync);
        batchSize = getInt("network.batchSize", batchSize);
        syncIntervalMs = getInt("network.syncIntervalMs", syncIntervalMs);
        syncPriorityThreshold = getDouble("network.syncPriorityThreshold", syncPriorityThreshold);
        
        // 内存管理
        enableObjectPooling = getBoolean("memory.enableObjectPooling", enableObjectPooling);
        poolSize = getInt("memory.poolSize", poolSize);
        enableMemoryMonitoring = getBoolean("memory.enableMonitoring", enableMemoryMonitoring);
        memoryCheckIntervalMs = getLong("memory.checkIntervalMs", memoryCheckIntervalMs);
        
        // 性能分析
        enableProfiler = getBoolean("profiler.enable", enableProfiler);
        profilerSampleIntervalMs = getInt("profiler.sampleIntervalMs", profilerSampleIntervalMs);
        profilerOutputDir = getString("profiler.outputDir", profilerOutputDir);
    }
    
    /**
     * 保存默认配置
     */
    private void saveDefaultConfig() {
        Path configPath = Paths.get(CONFIG_FILE);
        
        try {
            Files.createDirectories(configPath.getParent());
            
            properties.setProperty("factor.incrementalCalculation", String.valueOf(enableIncrementalCalculation));
            properties.setProperty("factor.maxChunksPerTick", String.valueOf(maxChunksPerTick));
            properties.setProperty("factor.calculationCacheSize", String.valueOf(calculationCacheSize));
            properties.setProperty("factor.calculationCacheExpirySeconds", String.valueOf(calculationCacheExpirySeconds));
            
            properties.setProperty("entity.enableActivationRange", String.valueOf(enableEntityActivationRange));
            properties.setProperty("entity.rangeVeryNear", String.valueOf(entityActivationRangeVeryNear));
            properties.setProperty("entity.rangeNear", String.valueOf(entityActivationRangeNear));
            properties.setProperty("entity.rangeNormal", String.valueOf(entityActivationRangeNormal));
            properties.setProperty("entity.rangeFar", String.valueOf(entityActivationRangeFar));
            
            properties.setProperty("network.enableBatchedSync", String.valueOf(enableBatchedSync));
            properties.setProperty("network.batchSize", String.valueOf(batchSize));
            properties.setProperty("network.syncIntervalMs", String.valueOf(syncIntervalMs));
            properties.setProperty("network.syncPriorityThreshold", String.valueOf(syncPriorityThreshold));
            
            properties.setProperty("memory.enableObjectPooling", String.valueOf(enableObjectPooling));
            properties.setProperty("memory.poolSize", String.valueOf(poolSize));
            properties.setProperty("memory.enableMonitoring", String.valueOf(enableMemoryMonitoring));
            properties.setProperty("memory.checkIntervalMs", String.valueOf(memoryCheckIntervalMs));
            
            properties.setProperty("profiler.enable", String.valueOf(enableProfiler));
            properties.setProperty("profiler.sampleIntervalMs", String.valueOf(profilerSampleIntervalMs));
            properties.setProperty("profiler.outputDir", profilerOutputDir);
            
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "FactorCraft Performance Configuration");
            }
            
            FactorCraftMod.LOGGER.info("Default performance config saved to {}", CONFIG_FILE);
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("Failed to save performance config", e);
        }
    }
    
    private boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    private int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 重新加载配置（用于命令刷新）
     */
    public void reload() {
        properties.clear();
        loadConfig();
    }
    
    // ==================== Getter 方法 ====================
    
    // Factor 计算优化
    public boolean isEnableIncrementalCalculation() { return enableIncrementalCalculation; }
    public int getMaxChunksPerTick() { return maxChunksPerTick; }
    public int getFactorCacheSize() { return calculationCacheSize; }
    public double getFactorCacheExpirySeconds() { return calculationCacheExpirySeconds; }
    
    // 实体激活范围
    public boolean isEnableEntityActivationRange() { return enableEntityActivationRange; }
    public double getEntityActivationRangeVeryNear() { return entityActivationRangeVeryNear; }
    public double getEntityActivationRangeNear() { return entityActivationRangeNear; }
    public double getEntityActivationRangeNormal() { return entityActivationRangeNormal; }
    public double getEntityActivationRangeFar() { return entityActivationRangeFar; }
    
    // 网络同步优化
    public boolean isEnableBatchedSync() { return enableBatchedSync; }
    public int getNetworkBatchSize() { return batchSize; }
    public int getNetworkSyncInterval() { return syncIntervalMs; }
    public double getSyncPriorityThreshold() { return syncPriorityThreshold; }
    
    // 内存管理
    public boolean isEnableObjectPooling() { return enableObjectPooling; }
    public int getPoolSize() { return poolSize; }
    public boolean isEnableMemoryMonitoring() { return enableMemoryMonitoring; }
    public long getMemoryCheckIntervalMs() { return memoryCheckIntervalMs; }
    
    // 性能分析
    public boolean isEnableProfiler() { return enableProfiler; }
    public int getProfilerSampleIntervalMs() { return profilerSampleIntervalMs; }
    public String getProfilerOutputDir() { return profilerOutputDir; }
}
