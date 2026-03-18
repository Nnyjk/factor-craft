package com.factorcraft.performance;

import com.factorcraft.module.factor.FactorService;
import com.factorcraft.module.factor.TideStatus;
import com.factorcraft.module.factor.management.ChunkFactorManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能分析器
 * 提供详细的性能分析和报告功能
 */
public class PerformanceProfiler {
    private static final Map<String, OperationProfile> PROFILES = new ConcurrentHashMap<>();
    private static final AtomicLong profilerStartTime = new AtomicLong(0);
    private static volatile boolean profiling = false;
    
    /**
     * 开始性能分析会话
     */
    public static void startProfiling() {
        PROFILES.clear();
        profilerStartTime.set(System.currentTimeMillis());
        profiling = true;
    }
    
    /**
     * 停止性能分析会话
     */
    public static void stopProfiling() {
        profiling = false;
    }
    
    /**
     * 记录操作
     */
    public static void recordOperation(String operation, long durationNanos) {
        if (!profiling) return;
        
        PROFILES.computeIfAbsent(operation, k -> new OperationProfile(k))
            .record(durationNanos);
    }
    
    /**
     * 获取性能报告
     */
    public static PerformanceReport generateReport(MinecraftServer server) {
        List<OperationProfile> operations = new ArrayList<>(PROFILES.values());
        operations.sort(Comparator.comparingLong(OperationProfile::getTotalTimeNanos).reversed());
        
        PerformanceMonitor.SystemStats systemStats = PerformanceMonitor.getSystemStats();
        
        // 收集世界统计
        List<WorldStats> worldStats = new ArrayList<>();
        for (ServerWorld world : server.getWorlds()) {
            double factor = FactorService.getInstance().getFactor(world);
            TideStatus status = TideStatus.fromConcentration(factor);
            int loadedChunks = world.getChunkManager().getLoadedChunkCount();
            
            worldStats.add(new WorldStats(
                world.getRegistryKey().getValue().toString(),
                factor,
                status.getName(),
                loadedChunks
            ));
        }
        
        return new PerformanceReport(
            System.currentTimeMillis() - profilerStartTime.get(),
            operations,
            systemStats,
            worldStats
        );
    }
    
    /**
     * 格式化报告为文本
     */
    public static List<Text> formatReport(PerformanceReport report) {
        List<Text> lines = new ArrayList<>();
        
        lines.add(Text.literal("═══════════════════════════════════════")
            .styled(s -> s.withColor(Formatting.GOLD)));
        lines.add(Text.literal("       Factor Craft Performance Report")
            .styled(s -> s.withColor(Formatting.GOLD).withBold(true)));
        lines.add(Text.literal("═══════════════════════════════════════")
            .styled(s -> s.withColor(Formatting.GOLD)));
        lines.add(Text.empty());
        
        // 系统概览
        lines.add(Text.literal("📊 System Overview")
            .styled(s -> s.withColor(Formatting.AQUA).withBold(true)));
        lines.add(Text.literal("  Duration: " + formatDuration(report.durationMs()))
            .styled(s -> s.withColor(Formatting.GRAY)));
        lines.add(Text.literal("  Total Ticks: " + report.systemStats().totalTicks())
            .styled(s -> s.withColor(Formatting.GRAY)));
        lines.add(Text.literal("  Avg Tick Time: " + 
            String.format("%.2f", report.systemStats().avgTickTimeNanos() / 1_000_000.0) + " ms")
            .styled(s -> s.withColor(Formatting.GRAY)));
        lines.add(Text.literal("  Cached Chunks: " + report.systemStats().cachedChunks())
            .styled(s -> s.withColor(Formatting.GRAY)));
        lines.add(Text.empty());
        
        // 操作性能
        lines.add(Text.literal("⏱️ Operation Performance")
            .styled(s -> s.withColor(Formatting.YELLOW).withBold(true)));
        
        for (OperationProfile op : report.operations()) {
            Formatting color = getColorForPerformance(op.getAverageTimeNanos());
            lines.add(Text.literal(String.format("  %-20s ", op.getName()) +
                String.format("avg: %.2f μs | max: %.2f μs | count: %d",
                    op.getAverageTimeNanos() / 1000.0,
                    op.getMaxTimeNanos() / 1000.0,
                    op.getCount()))
                .styled(s -> s.withColor(color)));
        }
        lines.add(Text.empty());
        
        // 世界统计
        lines.add(Text.literal("🌍 World Statistics")
            .styled(s -> s.withColor(Formatting.GREEN).withBold(true)));
        for (WorldStats ws : report.worldStats()) {
            lines.add(Text.literal(String.format("  %s: Factor=%.1f%% (%s), Chunks=%d",
                ws.worldName().replace("minecraft:", ""),
                ws.factor() * 100,
                ws.status(),
                ws.loadedChunks()))
                .styled(s -> s.withColor(Formatting.GRAY)));
        }
        lines.add(Text.empty());
        
        // 性能建议
        lines.add(Text.literal("💡 Performance Recommendations")
            .styled(s -> s.withColor(Formatting.LIGHT_PURPLE).withBold(true)));
        
        List<String> recommendations = analyzePerformance(report);
        for (String rec : recommendations) {
            lines.add(Text.literal("  • " + rec)
                .styled(s -> s.withColor(Formatting.GRAY)));
        }
        
        lines.add(Text.literal("═══════════════════════════════════════")
            .styled(s -> s.withColor(Formatting.GOLD)));
        
        return lines;
    }
    
    /**
     * 分析性能并生成建议
     */
    private static List<String> analyzePerformance(PerformanceReport report) {
        List<String> recommendations = new ArrayList<>();
        
        // 检查平均 tick 时间
        double avgTickMs = report.systemStats().avgTickTimeNanos() / 1_000_000.0;
        if (avgTickMs > 50) {
            recommendations.add("⚠️ High tick time detected. Consider reducing machine count or spread them across chunks.");
        } else if (avgTickMs > 20) {
            recommendations.add("ℹ️ Moderate tick time. Monitor performance as factory expands.");
        } else {
            recommendations.add("✅ Good tick performance. System running smoothly.");
        }
        
        // 检查扩散性能
        for (OperationProfile op : report.operations()) {
            if (op.getName().equals("diffusion")) {
                if (op.getAverageTimeNanos() > 1_000_000) { // > 1ms
                    recommendations.add("⚠️ Diffusion system is slow. Consider increasing DIFFUSION_INTERVAL.");
                }
            }
        }
        
        // 检查缓存使用
        int cacheSize = report.systemStats().cachedChunks();
        if (cacheSize > 5000) {
            recommendations.add("ℹ️ Large chunk cache. Consider reducing cache size for memory efficiency.");
        }
        
        // 检查网络同步
        if (report.systemStats().networkStats() != null) {
            var netStats = report.systemStats().networkStats();
            if (netStats.pendingSyncs() > 100) {
                recommendations.add("⚠️ High network sync backlog. Check for connectivity issues.");
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("✅ No performance issues detected.");
        }
        
        return recommendations;
    }
    
    private static Formatting getColorForPerformance(long avgNanos) {
        double avgMs = avgNanos / 1_000_000.0;
        if (avgMs < 1) return Formatting.GREEN;
        if (avgMs < 5) return Formatting.YELLOW;
        if (avgMs < 10) return Formatting.GOLD;
        return Formatting.RED;
    }
    
    private static String formatDuration(long ms) {
        if (ms < 1000) return ms + " ms";
        if (ms < 60000) return (ms / 1000) + " seconds";
        return (ms / 60000) + " minutes";
    }
    
    // Records
    
    public record PerformanceReport(
        long durationMs,
        List<OperationProfile> operations,
        PerformanceMonitor.SystemStats systemStats,
        List<WorldStats> worldStats
    ) {}
    
    public record WorldStats(
        String worldName,
        double factor,
        String status,
        int loadedChunks
    ) {}
    
    /**
     * 操作性能统计
     */
    public static class OperationProfile {
        private final String name;
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTimeNanos = new AtomicLong(0);
        private final AtomicLong maxTimeNanos = new AtomicLong(0);
        
        public OperationProfile(String name) {
            this.name = name;
        }
        
        public void record(long durationNanos) {
            count.incrementAndGet();
            totalTimeNanos.addAndGet(durationNanos);
            
            long currentMax;
            do {
                currentMax = maxTimeNanos.get();
                if (durationNanos <= currentMax) break;
            } while (!maxTimeNanos.compareAndSet(currentMax, durationNanos));
        }
        
        public String getName() { return name; }
        public long getCount() { return count.get(); }
        public long getTotalTimeNanos() { return totalTimeNanos.get(); }
        public long getAverageTimeNanos() {
            long c = count.get();
            return c > 0 ? totalTimeNanos.get() / c : 0;
        }
        public long getMaxTimeNanos() { return maxTimeNanos.get(); }
    }
}