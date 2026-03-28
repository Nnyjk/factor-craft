package com.factorcraft.module.factor.optimization;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.factor.FactorType;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;

import java.util.concurrent.TimeUnit;

/**
 * Factor 计算结果缓存
 * 
 * 缓存 Chunk 的 Factor 浓度计算结果，避免重复计算
 * 支持过期清理和容量限制
 */
public class FactorCalculationCache {
    
    // 缓存条目
    private final Long2ObjectMap<CacheEntry> cache = new Long2ObjectOpenHashMap<>();
    
    // 配置
    private final int maxSize;
    private final long expiryTimeNs;
    
    // 统计
    private int hits = 0;
    private int misses = 0;
    private int evictions = 0;
    
    public FactorCalculationCache(int maxSize, long expirySeconds) {
        this.maxSize = maxSize;
        this.expiryTimeNs = TimeUnit.SECONDS.toNanos(expirySeconds);
    }
    
    /**
     * 获取缓存的 Factor 浓度
     * 
     * @param chunkPos Chunk 位置
     * @param factorType Factor 类型
     * @return 缓存的浓度值，如果未缓存返回 -1
     */
    public float get(ChunkPos chunkPos, FactorType factorType) {
        long key = chunkPos.toLong();
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            misses++;
            return -1;
        }
        
        // 检查是否过期
        long now = System.nanoTime();
        if (now - entry.timestamp > expiryTimeNs) {
            cache.remove(key);
            misses++;
            return -1;
        }
        
        hits++;
        return entry.concentrations.getOrDefault(factorType, 0.0f);
    }
    
    /**
     * 获取所有缓存的 Factor 浓度
     * 
     * @param chunkPos Chunk 位置
     * @return 缓存的浓度映射，如果未缓存返回 null
     */
    public java.util.Map<FactorType, Float> getAll(ChunkPos chunkPos) {
        long key = chunkPos.toLong();
        CacheEntry entry = cache.get(key);
        
        if (entry == null) {
            misses++;
            return null;
        }
        
        // 检查是否过期
        long now = System.nanoTime();
        if (now - entry.timestamp > expiryTimeNs) {
            cache.remove(key);
            misses++;
            return null;
        }
        
        hits++;
        return entry.concentrations;
    }
    
    /**
     * 缓存 Factor 浓度
     * 
     * @param chunkPos Chunk 位置
     * @param concentrations Factor 浓度映射
     */
    public void put(ChunkPos chunkPos, java.util.Map<FactorType, Float> concentrations) {
        long key = chunkPos.toLong();
        
        // 如果缓存已满，移除最旧的条目
        if (cache.size() >= maxSize) {
            evictOldest();
        }
        
        cache.put(key, new CacheEntry(concentrations));
    }
    
    /**
     * 移除缓存条目
     * 
     * @param chunkPos Chunk 位置
     */
    public void remove(ChunkPos chunkPos) {
        cache.remove(chunkPos.toLong());
    }
    
    /**
     * 清除所有缓存
     */
    public void clear() {
        cache.clear();
        hits = 0;
        misses = 0;
        evictions = 0;
    }
    
    /**
     * 清理过期条目
     */
    public void cleanup() {
        long now = System.nanoTime();
        cache.long2ObjectEntrySet().removeIf(entry -> 
            now - entry.getValue().timestamp > expiryTimeNs
        );
    }
    
    /**
     * 移除最旧的条目
     */
    private void evictOldest() {
        long oldestTime = Long.MAX_VALUE;
        long oldestKey = -1;
        
        for (var entry : cache.long2ObjectEntrySet()) {
            if (entry.getValue().timestamp < oldestTime) {
                oldestTime = entry.getValue().timestamp;
                oldestKey = entry.getLongKey();
            }
        }
        
        if (oldestKey != -1) {
            cache.remove(oldestKey);
            evictions++;
        }
    }
    
    /**
     * 获取缓存命中率
     */
    public double getHitRate() {
        int total = hits + misses;
        return total > 0 ? (double) hits / total : 0.0;
    }
    
    /**
     * 获取缓存大小
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * 获取命中次数
     */
    public int getHits() {
        return hits;
    }
    
    /**
     * 获取未命中次数
     */
    public int getMisses() {
        return misses;
    }
    
    /**
     * 获取驱逐次数
     */
    public int getEvictions() {
        return evictions;
    }
    
    /**
     * 获取统计信息
     */
    public String getStats() {
        return String.format(
            "Cache[size=%d, hits=%d, misses=%d, evictions=%d, hitRate=%.2f%%]",
            size(), hits, misses, evictions, getHitRate() * 100
        );
    }
    
    /**
     * 缓存条目
     */
    private static class CacheEntry {
        final long timestamp;
        final java.util.Map<FactorType, Float> concentrations;
        
        CacheEntry(java.util.Map<FactorType, Float> concentrations) {
            this.timestamp = System.nanoTime();
            this.concentrations = new java.util.HashMap<>(concentrations);
        }
    }
    
    // ==================== 简化 API (用于 ChunkFactorManager) ====================
    
    private final Long2ObjectMap<DecayEntry> decayCache = new Long2ObjectOpenHashMap<>();
    
    /**
     * 获取缓存的衰减值
     */
    public Double get(long chunkKey) {
        DecayEntry entry = decayCache.get(chunkKey);
        if (entry == null) {
            misses++;
            return null;
        }
        
        long now = System.nanoTime();
        if (now - entry.timestamp > expiryTimeNs) {
            decayCache.remove(chunkKey);
            misses++;
            return null;
        }
        
        hits++;
        return entry.decay;
    }
    
    /**
     * 存储衰减值到缓存
     */
    public void put(long chunkKey, double decay, long tick) {
        if (decayCache.size() >= maxSize) {
            evictOldestDecay();
        }
        decayCache.put(chunkKey, new DecayEntry(decay, tick));
    }
    
    /**
     * 移除缓存的衰减值
     */
    public void invalidate(long chunkKey) {
        decayCache.remove(chunkKey);
    }
    
    /**
     * 移除最旧的衰减值条目
     */
    private void evictOldestDecay() {
        long oldestTime = Long.MAX_VALUE;
        long oldestKey = -1;
        
        for (var entry : decayCache.long2ObjectEntrySet()) {
            if (entry.getValue().timestamp < oldestTime) {
                oldestTime = entry.getValue().timestamp;
                oldestKey = entry.getLongKey();
            }
        }
        
        if (oldestKey != -1) {
            decayCache.remove(oldestKey);
            evictions++;
        }
    }
    
    /**
     * 衰减值缓存条目
     */
    private static class DecayEntry {
        final long timestamp;
        final double decay;
        final long tick;
        
        DecayEntry(double decay, long tick) {
            this.timestamp = System.nanoTime();
            this.decay = decay;
            this.tick = tick;
        }
    }
}
