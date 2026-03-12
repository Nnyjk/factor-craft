package com.factorcraft.performance;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控和分析工具
 */
public class PerformanceMonitor {
    private static final ConcurrentHashMap<String, Metric> METRICS = new ConcurrentHashMap<>();
    private static final AtomicLong totalTicks = new AtomicLong(0);
    private static final AtomicLong totalProcessingTime = new AtomicLong(0);
    
    public static void startTracking(String operation) {
        METRICS.computeIfAbsent(operation, k -> new Metric()).start();
    }
    
    public static void endTracking(String operation) {
        Metric metric = METRICS.get(operation);
        if (metric != null) {
            metric.end();
        }
    }
    
    public static void recordTick(long processingTimeNanos) {
        totalTicks.incrementAndGet();
        totalProcessingTime.addAndGet(processingTimeNanos);
    }
    
    public static OperationStats getStats(String operation) {
        Metric metric = METRICS.get(operation);
        if (metric == null) return new OperationStats(operation, 0, 0, 0, 0);
        
        return new OperationStats(
            operation,
            metric.getCount(),
            metric.getTotalTimeNanos(),
            metric.getAverageTimeNanos(),
            metric.getMaxTimeNanos()
        );
    }
    
    public static SystemStats getSystemStats() {
        long ticks = totalTicks.get();
        long avgTickTime = ticks > 0 ? totalProcessingTime.get() / ticks : 0;
        
        return new SystemStats(
            ticks,
            avgTickTime,
            ChunkFactorCache.size(),
            AsyncNetworkSync.getStats()
        );
    }
    
    public static void reset() {
        METRICS.clear();
        totalTicks.set(0);
        totalProcessingTime.set(0);
    }
    
    public static void printReport() {
        System.out.println("=== FactorCraft Performance Report ===");
        System.out.println();
        
        SystemStats systemStats = getSystemStats();
        System.out.println("System Stats:");
        System.out.printf("  Ticks: %d%n", systemStats.totalTicks());
        System.out.printf("  Avg Tick Time: %.2f ms%n", systemStats.avgTickTimeNanos() / 1_000_000.0);
        System.out.printf("  Cached Chunks: %d%n", systemStats.cachedChunks());
        System.out.println();
        
        System.out.println("Operation Stats:");
        METRICS.keySet().forEach(op -> {
            OperationStats stats = getStats(op);
            System.out.printf("  %s:%n", op);
            System.out.printf("    Count: %d%n", stats.count());
            System.out.printf("    Avg Time: %.2f μs%n", stats.avgTimeNanos() / 1000.0);
            System.out.printf("    Max Time: %.2f μs%n", stats.maxTimeNanos() / 1000.0);
        });
        
        System.out.println("======================================");
    }
    
    private static class Metric {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTimeNanos = new AtomicLong(0);
        private final AtomicLong maxTimeNanos = new AtomicLong(0);
        private final ThreadLocal<Long> startTime = new ThreadLocal<>();
        
        void start() {
            startTime.set(System.nanoTime());
        }
        
        void end() {
            Long start = startTime.get();
            if (start != null) {
                long duration = System.nanoTime() - start;
                count.incrementAndGet();
                totalTimeNanos.addAndGet(duration);
                
                long currentMax;
                do {
                    currentMax = maxTimeNanos.get();
                    if (duration <= currentMax) break;
                } while (!maxTimeNanos.compareAndSet(currentMax, duration));
            }
        }
        
        long getCount() { return count.get(); }
        long getTotalTimeNanos() { return totalTimeNanos.get(); }
        long getAverageTimeNanos() {
            long c = count.get();
            return c > 0 ? totalTimeNanos.get() / c : 0;
        }
        long getMaxTimeNanos() { return maxTimeNanos.get(); }
    }
    
    public record OperationStats(
        String operation,
        long count,
        long totalTimeNanos,
        long avgTimeNanos,
        long maxTimeNanos
    ) {
        public double avgTimeMs() { return avgTimeNanos / 1_000_000.0; }
        public double totalTimeMs() { return totalTimeNanos / 1_000_000.0; }
    }
    
    public record SystemStats(
        long totalTicks,
        long avgTickTimeNanos,
        int cachedChunks,
        AsyncNetworkSync.NetworkStats networkStats
    ) {}
}