package com.factorcraft.performance;

import com.factorcraft.module.network.FactorSyncPayload;
import com.factorcraft.module.network.NetworkConfig;
import com.factorcraft.module.network.NetworkSyncTracker;
import com.factorcraft.module.network.TraitSyncPayload;
import com.factorcraft.module.material.trait.TraitInstance;
import io.netty.channel.ChannelFuture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步网络同步系统 (R3.1 优化版)
 * 
 * 优化特性:
 * - 条件同步：使用 NetworkSyncTracker 检测浓度变化
 * - 配置化频率：使用 NetworkConfig.SYNC_INTERVAL_MS
 * - 批量同步：支持多区块合并发送
 * - 压缩支持：大数据量时自动压缩
 */
public class AsyncNetworkSync {
    private static final ExecutorService NETWORK_EXECUTOR = Executors.newFixedThreadPool(2);
    private static final int SYNC_INTERVAL_TICKS = 100; // 5 秒
    private static final int MAX_PACKETS_PER_TICK = 10;
    
    private static final ConcurrentLinkedQueue<SyncTask> PENDING_SYNCS = new ConcurrentLinkedQueue<>();
    private static final Long2LongOpenHashMap LAST_SYNC_TIMES = new Long2LongOpenHashMap();
    private static final AtomicInteger packetCount = new AtomicInteger(0);
    
    /**
     * 异步发送 Factor 同步包 (条件同步)
     * 
     * 使用 NetworkSyncTracker 检测浓度变化，仅当必要时才发送
     * 
     * @param player 目标玩家
     * @param pos 区块位置
     * @param concentration Factor 浓度
     * @param factorType Factor 类型
     */
    public static void syncFactorAsync(ServerPlayerEntity player, ChunkPos pos, double concentration, Identifier factorType) {
        long chunkKey = ChunkPos.toLong(pos.x, pos.z);
        long currentTime = System.currentTimeMillis();
        
        // 1. 频率限制检查 (使用配置化的间隔)
        if (LAST_SYNC_TIMES.containsKey(chunkKey)) {
            long lastSync = LAST_SYNC_TIMES.get(chunkKey);
            if (currentTime - lastSync < NetworkConfig.SYNC_INTERVAL_MS) {
                return;
            }
        }
        
        // 2. 条件同步检查 - 仅当浓度变化超过阈值时才发送
        long currentTick = currentTime / 50; // 转换为 tick
        if (!NetworkSyncTracker.shouldSyncFactor(pos, currentTick, concentration)) {
            return;
        }
        
        LAST_SYNC_TIMES.put(chunkKey, currentTime);
        
        NETWORK_EXECUTOR.submit(() -> {
            try {
                // 使用条件同步方法
                FactorSyncPayload payload = FactorSyncPayload.single(factorType, pos, (float)concentration);
                payload.conditionalSendTo(player);
                packetCount.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Error sending factor sync: " + e.getMessage());
            }
        });
    }
    
    /**
     * 异步发送 Factor 同步包 (向后兼容)
     * 
     * @deprecated 使用 {@link #syncFactorAsync(ServerPlayerEntity, ChunkPos, double, Identifier)} 替代
     */
    @Deprecated
    public static void syncFactorAsync(ServerPlayerEntity player, ChunkPos pos, double concentration) {
        syncFactorAsync(player, pos, concentration, Identifier.of("factorcraft", "generic"));
    }
    
    /**
     * 异步发送特性同步包
     */
    public static void syncTraitAsync(ServerPlayerEntity player, int slot, List<TraitInstance> traits) {
        NETWORK_EXECUTOR.submit(() -> {
            try {
                TraitSyncPayload payload = new TraitSyncPayload(slot, traits);
                ServerPlayNetworking.send(player, payload);
                packetCount.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Error sending trait sync: " + e.getMessage());
            }
        });
    }
    
    /**
     * 批量同步多个区块 (条件同步优化版)
     * 
     * 使用 NetworkSyncTracker 过滤无需同步的区块
     */
    public static void syncBatchAsync(ServerPlayerEntity player, Int2ObjectOpenHashMap<ChunkSyncData> batch, Identifier factorType) {
        NETWORK_EXECUTOR.submit(() -> {
            int sent = 0;
            List<ChunkSyncData> toSync = new ArrayList<>();
            long currentTick = System.currentTimeMillis() / 50; // 转换为 tick
            
            // 过滤出需要同步的区块
            for (var entry : batch.int2ObjectEntrySet()) {
                ChunkSyncData data = entry.getValue();
                ChunkPos pos = new ChunkPos(data.x(), data.z());
                
                if (NetworkSyncTracker.shouldSyncFactor(pos, currentTick, data.concentration())) {
                    toSync.add(data);
                }
            }
            
            // 如果有需要同步的区块，批量发送
            if (!toSync.isEmpty()) {
                ChunkPos[] positions = toSync.stream()
                    .map(d -> new ChunkPos(d.x(), d.z()))
                    .toArray(ChunkPos[]::new);
                float[] concentrations = new float[toSync.size()];
                for (int i = 0; i < toSync.size(); i++) {
                    concentrations[i] = (float)toSync.get(i).concentration();
                }
                
                FactorSyncPayload payload = FactorSyncPayload.batch(factorType, positions, concentrations);
                payload.conditionalBatchSendTo(player);
                sent = toSync.size();
            }
            
            packetCount.addAndGet(sent);
        });
    }
    
    /**
     * 批量同步多个区块 (向后兼容)
     * 
     * @deprecated 使用 {@link #syncBatchAsync(ServerPlayerEntity, Int2ObjectOpenHashMap, Identifier)} 替代
     */
    @Deprecated
    public static void syncBatchAsync(ServerPlayerEntity player, Int2ObjectOpenHashMap<ChunkSyncData> batch) {
        syncBatchAsync(player, batch, Identifier.of("factorcraft", "generic"));
    }
    
    /**
     * 处理待同步队列
     */
    public static void processPendingSyncs(MinecraftServer server) {
        if (PENDING_SYNCS.isEmpty()) return;
        
        int processed = 0;
        while (!PENDING_SYNCS.isEmpty() && processed < MAX_PACKETS_PER_TICK) {
            SyncTask task = PENDING_SYNCS.poll();
            if (task != null) {
                task.execute();
                processed++;
            }
        }
    }
    
    /**
     * 获取网络统计信息
     */
    public static NetworkStats getStats() {
        return new NetworkStats(
            packetCount.get(),
            PENDING_SYNCS.size(),
            LAST_SYNC_TIMES.size()
        );
    }
    
    /**
     * 重置统计
     */
    public static void resetStats() {
        packetCount.set(0);
        LAST_SYNC_TIMES.clear();
    }
    
    public static void shutdown() {
        NETWORK_EXECUTOR.shutdown();
        try {
            if (!NETWORK_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                NETWORK_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            NETWORK_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    public record ChunkSyncData(int x, int z, double concentration) {}
    
    public record NetworkStats(int packetsSent, int pendingSyncs, int trackedChunks) {
        public String toString() {
            return String.format("NetworkStats{sent=%d, pending=%d, chunks=%d}", 
                packetsSent, pendingSyncs, trackedChunks);
        }
    }
    
    private interface SyncTask {
        void execute();
        ServerPlayerEntity player();
    }
}
