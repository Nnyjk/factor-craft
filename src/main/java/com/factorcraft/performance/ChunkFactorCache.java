package com.factorcraft.performance;

import com.factorcraft.module.factor.state.ChunkFactorState;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 区块 Factor 状态缓存
 * 使用 FastUtil 优化内存占用，读写锁保证线程安全
 */
public class ChunkFactorCache {
    private static final Long2ObjectOpenHashMap<ChunkFactorState> CACHE = new Long2ObjectOpenHashMap<>(1024);
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final int MAX_CACHE_SIZE = 10000;
    private static final long CLEANUP_INTERVAL = 6000; // 5分钟（100 ticks）
    private static long lastCleanup = 0;
    
    public static Optional<ChunkFactorState> get(ChunkPos pos) {
        LOCK.readLock().lock();
        try {
            ChunkFactorState state = CACHE.get(toLong(pos));
            return Optional.ofNullable(state);
        } finally {
            LOCK.readLock().unlock();
        }
    }
    
    public static void put(ChunkPos pos, ChunkFactorState state) {
        LOCK.writeLock().lock();
        try {
            CACHE.put(toLong(pos), state);
            
            if (CACHE.size() > MAX_CACHE_SIZE) {
                // 清理旧条目
                long threshold = System.currentTimeMillis() - 600000; // 10分钟前
                CACHE.long2ObjectEntrySet().removeIf(entry -> {
                    ChunkFactorState s = entry.getValue();
                    return s.getLastUpdatedTick() < threshold;
                });
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }
    
    public static void remove(ChunkPos pos) {
        LOCK.writeLock().lock();
        try {
            CACHE.remove(toLong(pos));
        } finally {
            LOCK.writeLock().unlock();
        }
    }
    
    private static long toLong(ChunkPos pos) {
        return ChunkPos.toLong(pos.x, pos.z);
    }
    
    public static void clear() {
        LOCK.writeLock().lock();
        try {
            CACHE.clear();
        } finally {
            LOCK.writeLock().unlock();
        }
    }
    
    public static int size() {
        LOCK.readLock().lock();
        try {
            return CACHE.size();
        } finally {
            LOCK.readLock().unlock();
        }
    }
    
    public static void tickCleanup(long currentTick) {
        if (currentTick - lastCleanup > CLEANUP_INTERVAL) {
            LOCK.writeLock().lock();
            try {
                // 移除超过 10 分钟未访问的区块
                long threshold = currentTick - 12000;
                CACHE.long2ObjectEntrySet().removeIf(entry -> {
                    ChunkFactorState state = entry.getValue();
                    return state.getLastUpdatedTick() < threshold;
                });
                lastCleanup = currentTick;
            } finally {
                LOCK.writeLock().unlock();
            }
        }
    }
    
    public static CacheStats getStats() {
        LOCK.readLock().lock();
        try {
            return new CacheStats(CACHE.size(), MAX_CACHE_SIZE);
        } finally {
            LOCK.readLock().unlock();
        }
    }
    
    public record CacheStats(int currentSize, int maxSize) {
        public double usagePercent() {
            return (double) currentSize / maxSize * 100;
        }
    }
}