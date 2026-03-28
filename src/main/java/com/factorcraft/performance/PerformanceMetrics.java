package com.factorcraft.performance;

import com.factorcraft.FactorCraftMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能指标收集器
 * 
 * 收集服务器运行时性能指标，包括：
 * - TPS（Ticks Per Second）
 * - 内存使用
 * - CPU 使用率
 * - 网络统计
 */
public class PerformanceMetrics {
    
    private static PerformanceMetrics instance;
    
    // TPS 计算
    private final AtomicLong lastTickTime = new AtomicLong(0);
    private final double[] tickDurations = new double[60];
    private int tickIndex = 0;
    
    // 内存监控
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private long lastMemoryCheck = 0;
    private MemoryUsage lastHeapUsage = null;
    
    // 网络统计
    private final AtomicLong packetsSent = new AtomicLong(0);
    private final AtomicLong packetsReceived = new AtomicLong(0);
    private final AtomicLong bytesSent = new AtomicLong(0);
    private final AtomicLong bytesReceived = new AtomicLong(0);
    
    // Factor 计算统计
    private final AtomicLong factorCalculationCount = new AtomicLong(0);
    private final AtomicLong factorCalculationTimeNs = new AtomicLong(0);
    
    // 实体统计
    private final AtomicLong activeEntities = new AtomicLong(0);
    private final AtomicLong inactiveEntities = new AtomicLong(0);
    
    private PerformanceMetrics() {}
    
    public static PerformanceMetrics getInstance() {
        if (instance == null) {
            instance = new PerformanceMetrics();
        }
        return instance;
    }
    
    /**
     * 记录 tick 开始
     */
    public void onTickStart() {
        lastTickTime.set(System.nanoTime());
    }
    
    /**
     * 记录 tick 结束并计算 TPS
     */
    public void onTickEnd() {
        long endTime = System.nanoTime();
        long startTime = lastTickTime.get();
        long duration = endTime - startTime;
        
        synchronized (tickDurations) {
            tickDurations[tickIndex] = duration / 1_000_000.0; // 转换为毫秒
            tickIndex = (tickIndex + 1) % tickDurations.length;
        }
    }
    
    /**
     * 获取平均 TPS
     */
    public double getAverageTps() {
        synchronized (tickDurations) {
            double totalDuration = 0;
            int count = 0;
            for (double duration : tickDurations) {
                if (duration > 0) {
                    totalDuration += duration;
                    count++;
                }
            }
            if (count == 0 || totalDuration == 0) {
                return 20.0;
            }
            double avgDurationMs = totalDuration / count;
            return MathHelper.clamp(1000.0 / avgDurationMs, 0.0, 20.0);
        }
    }
    
    /**
     * 获取平均 tick 耗时（毫秒）
     */
    public double getAverageTickTime() {
        synchronized (tickDurations) {
            double totalDuration = 0;
            int count = 0;
            for (double duration : tickDurations) {
                if (duration > 0) {
                    totalDuration += duration;
                    count++;
                }
            }
            return count > 0 ? totalDuration / count : 50.0;
        }
    }
    
    /**
     * 获取堆内存使用量（MB）
     */
    public MemoryUsage getHeapMemoryUsage() {
        return memoryBean.getHeapMemoryUsage();
    }
    
    /**
     * 获取内存使用百分比
     */
    public double getMemoryUsagePercent() {
        MemoryUsage usage = getHeapMemoryUsage();
        if (usage == null) {
            return 0;
        }
        long max = usage.getMax();
        if (max < 0) {
            max = usage.getCommitted();
        }
        if (max <= 0) {
            return 0;
        }
        return (usage.getUsed() * 100.0) / max;
    }
    
    /**
     * 记录网络包发送
     */
    public void recordPacketSent(int bytes) {
        packetsSent.incrementAndGet();
        bytesSent.addAndGet(bytes);
    }
    
    /**
     * 记录网络包接收
     */
    public void recordPacketReceived(int bytes) {
        packetsReceived.incrementAndGet();
        bytesReceived.addAndGet(bytes);
    }
    
    /**
     * 记录 Factor 计算
     */
    public void recordFactorCalculation(long timeNs) {
        factorCalculationCount.incrementAndGet();
        factorCalculationTimeNs.addAndGet(timeNs);
    }
    
    /**
     * 获取 Factor 计算平均耗时（微秒）
     */
    public double getAverageFactorCalculationTime() {
        long count = factorCalculationCount.get();
        if (count == 0) {
            return 0;
        }
        return (factorCalculationTimeNs.get() / (double) count) / 1000.0;
    }
    
    /**
     * 获取 Factor 计算总次数
     */
    public long getFactorCalculationCount() {
        return factorCalculationCount.get();
    }
    
    /**
     * 设置活跃实体数
     */
    public void setActiveEntities(long count) {
        activeEntities.set(count);
    }
    
    /**
     * 设置非活跃实体数
     */
    public void setInactiveEntities(long count) {
        inactiveEntities.set(count);
    }
    
    /**
     * 获取活跃实体数
     */
    public long getActiveEntities() {
        return activeEntities.get();
    }
    
    /**
     * 获取非活跃实体数
     */
    public long getInactiveEntities() {
        return inactiveEntities.get();
    }
    
    /**
     * 获取发送的包总数
     */
    public long getPacketsSent() {
        return packetsSent.get();
    }
    
    /**
     * 获取接收的包总数
     */
    public long getPacketsReceived() {
        return packetsReceived.get();
    }
    
    /**
     * 获取发送的字节总数
     */
    public long getBytesSent() {
        return bytesSent.get();
    }
    
    /**
     * 获取接收的字节总数
     */
    public long getBytesReceived() {
        return bytesReceived.get();
    }
    
    /**
     * 重置统计
     */
    public void reset() {
        synchronized (tickDurations) {
            for (int i = 0; i < tickDurations.length; i++) {
                tickDurations[i] = 0;
            }
            tickIndex = 0;
        }
        packetsSent.set(0);
        packetsReceived.set(0);
        bytesSent.set(0);
        bytesReceived.set(0);
        factorCalculationCount.set(0);
        factorCalculationTimeNs.set(0);
        activeEntities.set(0);
        inactiveEntities.set(0);
    }
    
    /**
     * 获取性能摘要
     */
    public String getSummary() {
        MemoryUsage heap = getHeapMemoryUsage();
        return String.format(
            "TPS: %.2f | Tick: %.2fms | Memory: %d/%d MB (%.1f%%) | Entities: %d active / %d inactive | Factor calcs: %d (%.2fμs avg)",
            getAverageTps(),
            getAverageTickTime(),
            heap.getUsed() / (1024 * 1024),
            heap.getMax() / (1024 * 1024),
            getMemoryUsagePercent(),
            getActiveEntities(),
            getInactiveEntities(),
            getFactorCalculationCount(),
            getAverageFactorCalculationTime()
        );
    }
}
