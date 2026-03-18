package com.factorcraft.performance;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 性能分析报告生成器
 * 生成详细的性能分析报告和优化建议
 */
public final class PerformanceAnalysisReport {
    
    /**
     * 生成完整性能分析报告
     */
    public static List<Text> generateFullReport() {
        List<Text> report = new ArrayList<>();
        
        // 标题
        report.add(Text.literal("╔══════════════════════════════════════════════════════════════╗")
            .styled(s -> s.withColor(0xFFD700)));
        report.add(Text.literal("║        Factor Craft 性能分析报告                              ║")
            .styled(s -> s.withColor(0xFFD700).withBold(true)));
        report.add(Text.literal("╚══════════════════════════════════════════════════════════════╝")
            .styled(s -> s.withColor(0xFFD700)));
        report.add(Text.empty());
        
        // 系统概览
        addSystemOverview(report);
        
        // Factor 系统性能
        addFactorSystemPerformance(report);
        
        // 扩散系统性能
        addDiffusionPerformance(report);
        
        // 网络同步性能
        addNetworkPerformance(report);
        
        // 缓存效率
        addCacheEfficiency(report);
        
        // 优化建议
        addOptimizationRecommendations(report);
        
        return report;
    }
    
    private static void addSystemOverview(List<Text> report) {
        report.add(Text.literal("📊 系统概览")
            .styled(s -> s.withColor(0x55FFFF).withBold(true)));
        report.add(Text.literal("─────────────────────────────────────")
            .styled(s -> s.withColor(0x888888)));
        
        PerformanceMonitor.SystemStats stats = PerformanceMonitor.getSystemStats();
        
        // Tick 性能
        double avgTickMs = stats.avgTickTimeNanos() / 1_000_000.0;
        int tickColor = avgTickMs < 20 ? 0x55FF55 : avgTickMs < 40 ? 0xFFFF55 : 0xFF5555;
        report.add(Text.literal(String.format("  平均 Tick 时间: %.2f ms", avgTickMs))
            .styled(s -> s.withColor(tickColor)));
        report.add(Text.literal(String.format("  总 Tick 数: %,d", stats.totalTicks()))
            .styled(s -> s.withColor(0xAAAAAA)));
        
        // TPS 估算
        double estimatedTps = avgTickMs > 0 ? 1000.0 / avgTickMs : 20.0;
        int tpsColor = estimatedTps >= 19 ? 0x55FF55 : estimatedTps >= 15 ? 0xFFFF55 : 0xFF5555;
        report.add(Text.literal(String.format("  估算 TPS: %.1f", Math.min(estimatedTps, 20.0)))
            .styled(s -> s.withColor(tpsColor)));
        report.add(Text.empty());
    }
    
    private static void addFactorSystemPerformance(List<Text> report) {
        report.add(Text.literal("⚡ Factor 系统性能")
            .styled(s -> s.withColor(0x55FF55).withBold(true)));
        report.add(Text.literal("─────────────────────────────────────")
            .styled(s -> s.withColor(0x888888)));
        
        // 检查各个操作的性能
        String[] factorOps = {"factor_extract", "factor_diffusion", "factor_concentration"};
        for (String op : factorOps) {
            PerformanceMonitor.OperationStats stats = PerformanceMonitor.getStats(op);
            if (stats.count() > 0) {
                double avgMs = stats.avgTimeMs();
                int color = avgMs < 1 ? 0x55FF55 : avgMs < 5 ? 0xFFFF55 : 0xFF5555;
                
                report.add(Text.literal(String.format("  %-20s avg: %.3f ms | max: %.3f ms | count: %,d",
                    op + ":",
                    avgMs,
                    stats.maxTimeNanos() / 1_000_000.0,
                    stats.count()))
                    .styled(s -> s.withColor(color)));
            }
        }
        report.add(Text.empty());
    }
    
    private static void addDiffusionPerformance(List<Text> report) {
        report.add(Text.literal("🔄 扩散系统性能")
            .styled(s -> s.withColor(0xFFAA00).withBold(true)));
        report.add(Text.literal("─────────────────────────────────────")
            .styled(s -> s.withColor(0x888888)));
        
        PerformanceMonitor.OperationStats diffStats = PerformanceMonitor.getStats("diffusion");
        if (diffStats.count() > 0) {
            double avgMs = diffStats.avgTimeMs();
            int color = avgMs < 5 ? 0x55FF55 : avgMs < 10 ? 0xFFFF55 : 0xFF5555;
            
            report.add(Text.literal(String.format("  扩散计算: avg: %.3f ms | max: %.3f ms",
                avgMs,
                diffStats.maxTimeNanos() / 1_000_000.0))
                .styled(s -> s.withColor(color)));
            
            // 性能评估
            if (avgMs < 5) {
                report.add(Text.literal("  ✓ 扩散性能良好")
                    .styled(s -> s.withColor(0x55FF55)));
            } else if (avgMs < 10) {
                report.add(Text.literal("  ⚠ 扩散性能中等，建议检查机器密度")
                    .styled(s -> s.withColor(0xFFFF55)));
            } else {
                report.add(Text.literal("  ✗ 扩散性能较慢，建议优化")
                    .styled(s -> s.withColor(0xFF5555)));
            }
        } else {
            report.add(Text.literal("  暂无扩散数据")
                .styled(s -> s.withColor(0x888888)));
        }
        report.add(Text.empty());
    }
    
    private static void addNetworkPerformance(List<Text> report) {
        report.add(Text.literal("🌐 网络同步性能")
            .styled(s -> s.withColor(0x5555FF).withBold(true)));
        report.add(Text.literal("─────────────────────────────────────")
            .styled(s -> s.withColor(0x888888)));
        
        AsyncNetworkSync.NetworkStats netStats = AsyncNetworkSync.getStats();
        if (netStats != null) {
            report.add(Text.literal(String.format("  发送包数: %,d", netStats.packetsSent()))
                .styled(s -> s.withColor(0xAAAAAA)));
            report.add(Text.literal(String.format("  待同步队列: %d", netStats.pendingSyncs()))
                .styled(s -> s.withColor(netStats.pendingSyncs() > 100 ? 0xFF5555 : 0xAAAAAA)));
            report.add(Text.literal(String.format("  追踪区块: %d", netStats.trackedChunks()))
                .styled(s -> s.withColor(0xAAAAAA)));
            
            // 网络健康度
            if (netStats.pendingSyncs() > 100) {
                report.add(Text.literal("  ⚠ 网络同步积压较多")
                    .styled(s -> s.withColor(0xFFFF55)));
            } else {
                report.add(Text.literal("  ✓ 网络同步正常")
                    .styled(s -> s.withColor(0x55FF55)));
            }
        }
        report.add(Text.empty());
    }
    
    private static void addCacheEfficiency(List<Text> report) {
        report.add(Text.literal("💾 缓存效率")
            .styled(s -> s.withColor(0xAA55FF).withBold(true)));
        report.add(Text.literal("─────────────────────────────────────")
            .styled(s -> s.withColor(0x888888)));
        
        ChunkFactorCache.CacheStats cacheStats = ChunkFactorCache.getStats();
        double usage = cacheStats.usagePercent();
        int usageColor = usage < 50 ? 0x55FF55 : usage < 80 ? 0xFFFF55 : 0xFF5555;
        
        report.add(Text.literal(String.format("  缓存大小: %,d / %,d (%.1f%%)",
            cacheStats.currentSize(),
            cacheStats.maxSize(),
            usage))
            .styled(s -> s.withColor(usageColor)));
        
        // 缓存建议
        if (usage > 80) {
            report.add(Text.literal("  ⚠ 缓存接近上限，考虑增加容量或清理")
                .styled(s -> s.withColor(0xFFFF55)));
        } else if (usage > 50) {
            report.add(Text.literal("  ℹ 缓存使用适中")
                .styled(s -> s.withColor(0x55FFFF)));
        } else {
            report.add(Text.literal("  ✓ 缓存使用健康")
                .styled(s -> s.withColor(0x55FF55)));
        }
        report.add(Text.empty());
    }
    
    private static void addOptimizationRecommendations(List<Text> report) {
        report.add(Text.literal("💡 优化建议")
            .styled(s -> s.withColor(0xFF55FF).withBold(true)));
        report.add(Text.literal("─────────────────────────────────────")
            .styled(s -> s.withColor(0x888888)));
        
        List<String> recommendations = new ArrayList<>();
        
        // 分析系统状态并生成建议
        PerformanceMonitor.SystemStats sysStats = PerformanceMonitor.getSystemStats();
        double avgTickMs = sysStats.avgTickTimeNanos() / 1_000_000.0;
        
        // Tick 性能建议
        if (avgTickMs > 50) {
            recommendations.add("❌ Tick 时间过高！建议减少机器数量或分散布局");
        } else if (avgTickMs > 30) {
            recommendations.add("⚠️ Tick 时间偏高，监控机器密度");
        }
        
        // 扩散性能建议
        PerformanceMonitor.OperationStats diffStats = PerformanceMonitor.getStats("diffusion");
        if (diffStats.count() > 0 && diffStats.avgTimeMs() > 5) {
            recommendations.add("⚠️ 扩散计算耗时较长，可调整 DIFFUSION_INTERVAL 配置");
        }
        
        // 缓存建议
        ChunkFactorCache.CacheStats cacheStats = ChunkFactorCache.getStats();
        if (cacheStats.usagePercent() > 80) {
            recommendations.add("⚠️ 缓存使用率过高，考虑增加缓存大小");
        }
        
        // 网络建议
        AsyncNetworkSync.NetworkStats netStats = AsyncNetworkSync.getStats();
        if (netStats != null && netStats.pendingSyncs() > 50) {
            recommendations.add("⚠️ 网络同步积压，检查玩家连接状态");
        }
        
        // 默认建议
        if (recommendations.isEmpty()) {
            recommendations.add("✅ 系统运行良好，无需优化");
            recommendations.add("ℹ️ 定期运行性能分析以保持最佳状态");
        }
        
        for (String rec : recommendations) {
            report.add(Text.literal("  " + rec)
                .styled(s -> s.withColor(0xAAAAAA)));
        }
        
        report.add(Text.empty());
        report.add(Text.literal("═══════════════════════════════════════════════════════════════")
            .styled(s -> s.withColor(0xFFD700)));
    }
    
    /**
     * 生成简短性能摘要
     */
    public static List<Text> generateSummary() {
        List<Text> summary = new ArrayList<>();
        
        PerformanceMonitor.SystemStats stats = PerformanceMonitor.getSystemStats();
        double avgTickMs = stats.avgTickTimeNanos() / 1_000_000.0;
        double tps = avgTickMs > 0 ? Math.min(1000.0 / avgTickMs, 20.0) : 20.0;
        
        int tickColor = tps >= 19 ? 0x55FF55 : tps >= 15 ? 0xFFFF55 : 0xFF5555;
        
        summary.add(Text.literal(String.format("TPS: %.1f | Tick: %.2f ms | Chunks: %d",
            tps, avgTickMs, stats.cachedChunks()))
            .styled(s -> s.withColor(tickColor)));
        
        return summary;
    }
    
    /**
     * 获取性能评分 (0-100)
     */
    public static int getPerformanceScore() {
        int score = 100;
        
        PerformanceMonitor.SystemStats stats = PerformanceMonitor.getSystemStats();
        double avgTickMs = stats.avgTickTimeNanos() / 1_000_000.0;
        
        // Tick 性能扣分
        if (avgTickMs > 50) score -= 40;
        else if (avgTickMs > 40) score -= 25;
        else if (avgTickMs > 30) score -= 15;
        else if (avgTickMs > 20) score -= 5;
        
        // 扩散性能扣分
        PerformanceMonitor.OperationStats diffStats = PerformanceMonitor.getStats("diffusion");
        if (diffStats.count() > 0) {
            if (diffStats.avgTimeMs() > 10) score -= 20;
            else if (diffStats.avgTimeMs() > 5) score -= 10;
        }
        
        // 缓存效率扣分
        ChunkFactorCache.CacheStats cacheStats = ChunkFactorCache.getStats();
        if (cacheStats.usagePercent() > 90) score -= 10;
        else if (cacheStats.usagePercent() > 80) score -= 5;
        
        return Math.max(0, score);
    }
    
    /**
     * 获取性能等级
     */
    public static String getPerformanceGrade() {
        int score = getPerformanceScore();
        if (score >= 90) return "S";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        if (score >= 50) return "D";
        return "F";
    }
}