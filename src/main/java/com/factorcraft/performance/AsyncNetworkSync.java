package com.factorcraft.performance;

import com.factorcraft.module.network.FactorSyncPayload;
import com.factorcraft.module.network.TraitSyncPayload;
import com.factorcraft.module.material.trait.TraitInstance;
import io.netty.channel.ChannelFuture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步网络同步系统
 * 批量发送，减少网络包数量
 */
public class AsyncNetworkSync {
    private static final ExecutorService NETWORK_EXECUTOR = Executors.newFixedThreadPool(2);
    private static final int SYNC_INTERVAL_TICKS = 100; // 5秒
    private static final int MAX_PACKETS_PER_TICK = 10;
    
    private static final ConcurrentLinkedQueue<SyncTask> PENDING_SYNCS = new ConcurrentLinkedQueue<>();
    private static final Long2LongOpenHashMap LAST_SYNC_TIMES = new Long2LongOpenHashMap();
    private static final AtomicInteger packetCount = new AtomicInteger(0);
    
    /**
     * 异步发送 Factor 同步包
     */
    public static void syncFactorAsync(ServerPlayerEntity player, ChunkPos pos, double concentration) {
        long chunkKey = ChunkPos.toLong(pos.x, pos.z);
        long currentTime = System.currentTimeMillis();
        
        // 限流：每个区块每秒最多同步 2 次
        if (LAST_SYNC_TIMES.containsKey(chunkKey)) {
            long lastSync = LAST_SYNC_TIMES.get(chunkKey);
            if (currentTime - lastSync < 500) {
                return;
            }
        }
        
        LAST_SYNC_TIMES.put(chunkKey, currentTime);
        
        NETWORK_EXECUTOR.submit(() -> {
            try {
                FactorSyncPayload payload = new FactorSyncPayload(pos, concentration);
                ServerPlayNetworking.send(player, payload);
                packetCount.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Error sending factor sync: " + e.getMessage());
            }
        });
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
     * 批量同步多个区块
     */
    public static void syncBatchAsync(ServerPlayerEntity player, Int2ObjectOpenHashMap<ChunkSyncData> batch) {
        NETWORK_EXECUTOR.submit(() -> {
            int sent = 0;
            for (var entry : batch.int2ObjectEntrySet()) {
                if (sent >= MAX_PACKETS_PER_TICK) break;
                
                ChunkSyncData data = entry.getValue();
                FactorSyncPayload payload = new FactorSyncPayload(
                    new ChunkPos(data.x(), data.z()),
                    data.concentration()
                );
                
                ServerPlayNetworking.send(player, payload);
                sent++;
            }
            
            packetCount.addAndGet(sent);
        });
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