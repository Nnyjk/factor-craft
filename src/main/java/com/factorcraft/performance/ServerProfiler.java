package com.factorcraft.performance;

import com.factorcraft.FactorCraftMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 服务器性能分析器
 * 
 * 用于分析服务器性能瓶颈，记录各种操作的耗时
 * 支持微秒级精度计时和周期性报告生成
 */
public class ServerProfiler {
    
    private static ServerProfiler instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // 性能指标收集
    private final Map<String, ProfilerSection> sections = new ConcurrentHashMap<>();
    private final Map<String, Long> activeTimers = new ConcurrentHashMap<>();
    
    // 分析状态
    private boolean profiling = false;
    private long startTime = 0;
    private int tickCount = 0;
    
    // 输出目录
    private final Path outputDir;
    
    // 采样间隔（毫秒）
    private final int sampleIntervalMs;
    
    private ServerProfiler() {
        PerformanceConfig config = PerformanceConfig.getInstance();
        this.outputDir = Paths.get(config.profilerOutputDir);
        this.sampleIntervalMs = config.profilerSampleIntervalMs;
        
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("Failed to create profiler output directory", e);
        }
    }
    
    public static ServerProfiler getInstance() {
        if (instance == null) {
            instance = new ServerProfiler();
        }
        return instance;
    }
    
    /**
     * 开始性能分析
     */
    public void startProfiling() {
        if (profiling) {
            FactorCraftMod.LOGGER.warn("Profiler is already running");
            return;
        }
        
        profiling = true;
        startTime = System.currentTimeMillis();
        tickCount = 0;
        sections.clear();
        activeTimers.clear();
        
        FactorCraftMod.LOGGER.info("Server profiling started");
    }
    
    /**
     * 停止性能分析并生成报告
     */
    public void stopProfiling() {
        if (!profiling) {
            return;
        }
        
        profiling = false;
        long duration = System.currentTimeMillis() - startTime;
        
        FactorCraftMod.LOGGER.info("Server profiling stopped. Duration: {} ms, Ticks: {}", duration, tickCount);
        
        // 生成报告
        generateReport(duration);
    }
    
    /**
     * 记录 tick
     */
    public void onServerTick() {
        if (!profiling) {
            return;
        }
        tickCount++;
    }
    
    /**
     * 开始计时
     * @param sectionName 性能分析段名称
     */
    public void startTimer(String sectionName) {
        if (!profiling) {
            return;
        }
        activeTimers.put(sectionName, System.nanoTime());
    }
    
    /**
     * 结束计时并记录
     * @param sectionName 性能分析段名称
     */
    public void endTimer(String sectionName) {
        if (!profiling) {
            return;
        }
        
        Long startTime = activeTimers.remove(sectionName);
        if (startTime == null) {
            return;
        }
        
        long durationNs = System.nanoTime() - startTime;
        recordMeasurement(sectionName, durationNs);
    }
    
    /**
     * 记录单次测量值
     */
    private void recordMeasurement(String sectionName, long durationNs) {
        ProfilerSection section = sections.computeIfAbsent(sectionName, k -> new ProfilerSection());
        section.record(durationNs);
    }
    
    /**
     * 生成性能报告
     */
    private void generateReport(long durationMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf.format(new Date());
        Path reportPath = outputDir.resolve("profile_" + timestamp + ".json");
        
        ProfilerReport report = new ProfilerReport();
        report.startTime = startTime;
        report.durationMs = durationMs;
        report.tickCount = tickCount;
        report.averageTps = tickCount / (durationMs / 1000.0);
        report.sections = new HashMap<>(sections);
        
        // 计算平均耗时
        for (Map.Entry<String, ProfilerSection> entry : report.sections.entrySet()) {
            entry.getValue().calculateStats();
        }
        
        try {
            String json = gson.toJson(report);
            Files.writeString(reportPath, json);
            FactorCraftMod.LOGGER.info("Performance report saved to {}", reportPath);
            
            // 同时生成文本摘要
            generateTextSummary(report, reportPath.resolveSibling("profile_" + timestamp + ".txt"));
        } catch (IOException e) {
            FactorCraftMod.LOGGER.error("Failed to save performance report", e);
        }
    }
    
    /**
     * 生成文本摘要报告
     */
    private void generateTextSummary(ProfilerReport report, Path path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FactorCraft Performance Profile ===\n\n");
        sb.append("Duration: ").append(report.durationMs).append(" ms\n");
        sb.append("Ticks: ").append(report.tickCount).append("\n");
        sb.append("Average TPS: ").append(String.format("%.2f", report.averageTps)).append("\n\n");
        sb.append("=== Section Statistics ===\n\n");
        
        // 按平均耗时排序
        List<Map.Entry<String, ProfilerSection>> sorted = new ArrayList<>(report.sections.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue().avgNs, a.getValue().avgNs));
        
        for (Map.Entry<String, ProfilerSection> entry : sorted) {
            ProfilerSection section = entry.getValue();
            sb.append(entry.getKey()).append(":\n");
            sb.append("  Count: ").append(section.count).append("\n");
            sb.append("  Total: ").append(String.format("%.2f ms", section.totalNs / 1_000_000.0)).append("\n");
            sb.append("  Avg: ").append(String.format("%.3f ms", section.avgNs / 1_000_000.0)).append("\n");
            sb.append("  Min: ").append(String.format("%.3f ms", section.minNs / 1_000_000.0)).append("\n");
            sb.append("  Max: ").append(String.format("%.3f ms", section.maxNs / 1_000_000.0)).append("\n");
            sb.append("\n");
        }
        
        Files.writeString(path, sb.toString());
        FactorCraftMod.LOGGER.info("Performance summary saved to {}", path);
    }
    
    /**
     * 获取当前分析状态
     */
    public boolean isProfiling() {
        return profiling;
    }
    
    /**
     * 获取已运行的 tick 数
     */
    public int getTickCount() {
        return tickCount;
    }
    
    /**
     * 获取运行时长（毫秒）
     */
    public long getDuration() {
        return profiling ? System.currentTimeMillis() - startTime : 0;
    }
    
    // ==================== 内部类 ====================
    
    /**
     * 性能分析段
     */
    private static class ProfilerSection {
        public int count = 0;
        public long totalNs = 0;
        public long minNs = Long.MAX_VALUE;
        public long maxNs = 0;
        public long avgNs = 0;
        
        public void record(long durationNs) {
            count++;
            totalNs += durationNs;
            minNs = Math.min(minNs, durationNs);
            maxNs = Math.max(maxNs, durationNs);
        }
        
        public void calculateStats() {
            if (count > 0) {
                avgNs = totalNs / count;
            }
        }
    }
    
    /**
     * 性能分析报告
     */
    private static class ProfilerReport {
        public long startTime;
        public long durationMs;
        public int tickCount;
        public double averageTps;
        public Map<String, ProfilerSection> sections;
    }
}
