package com.factorcraft.performance;

import com.factorcraft.FactorCraftMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

/**
 * 批量网络同步
 * 
 * 将多个网络包合并为单个包发送，减少网络开销
 * 支持优先级队列和批量大小限制
 */
public class BatchedNetworkSync {
    
    private static BatchedNetworkSync instance;
    
    // 待发送的包队列
    private final ConcurrentLinkedQueue<QueuedPayload> pendingPayloads = new ConcurrentLinkedQueue<>();
    
    // 配置
    private final int batchSize;
    private final int syncIntervalMs;
    
    // 统计
    private long payloadsSent = 0;
    private long batchesSent = 0;
    private long bytesSaved = 0;
    
    public BatchedNetworkSync(int batchSize, int syncIntervalMs) {
        this.batchSize = batchSize;
        this.syncIntervalMs = syncIntervalMs;
    }
    
    public static BatchedNetworkSync getInstance() {
        if (instance == null) {
            instance = new BatchedNetworkSync(
                PerformanceConfig.getInstance().getNetworkBatchSize(),
                PerformanceConfig.getInstance().getNetworkSyncInterval()
            );
        }
        return instance;
    }
    
    /**
     * 添加待发送的包
     */
    public void queue(CustomPayload payload, ServerPlayerEntity player) {
        queue(payload, 0.0, player);
    }
    
    /**
     * 添加带优先级的包
     */
    public void queue(CustomPayload payload, double priority, ServerPlayerEntity player) {
        pendingPayloads.offer(new QueuedPayload(payload, priority, player, System.currentTimeMillis()));
        
        // 检查是否达到批量大小
        if (pendingPayloads.size() >= batchSize) {
            flush();
        }
    }
    
    /**
     * 刷新队列，发送所有待发送的包
     */
    public void flush() {
        if (pendingPayloads.isEmpty()) {
            return;
        }
        
        // 按玩家分组
        var payloadsByPlayer = new java.util.HashMap<ServerPlayerEntity, List<QueuedPayload>>();
        
        int collected = 0;
        while (collected < batchSize && !pendingPayloads.isEmpty()) {
            QueuedPayload queued = pendingPayloads.poll();
            if (queued != null) {
                payloadsByPlayer
                    .computeIfAbsent(queued.player, k -> new ObjectArrayList<>())
                    .add(queued);
                collected++;
            }
        }
        
        // 发送批量包
        for (var entry : payloadsByPlayer.entrySet()) {
            ServerPlayerEntity player = entry.getKey();
            List<QueuedPayload> payloads = entry.getValue();
            
            if (payloads.size() == 1) {
                // 单个包直接发送
                sendPayload(payloads.get(0).payload, player);
            } else {
                // 多个包合并发送
                sendBatch(payloads, player);
            }
        }
    }
    
    /**
     * 向特定玩家刷新队列
     */
    public void flush(ServerPlayerEntity player) {
        List<QueuedPayload> playerPayloads = new ObjectArrayList<>();
        
        // 收集该玩家的所有待发送包
        for (QueuedPayload queued : pendingPayloads) {
            if (queued.player == player) {
                playerPayloads.add(queued);
            }
        }
        
        // 从队列中移除
        pendingPayloads.removeAll(playerPayloads);
        
        // 发送
        if (playerPayloads.size() == 1) {
            sendPayload(playerPayloads.get(0).payload, player);
        } else if (!playerPayloads.isEmpty()) {
            sendBatch(playerPayloads, player);
        }
    }
    
    /**
     * 发送单个包
     */
    private void sendPayload(CustomPayload payload, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, payload);
        payloadsSent++;
        batchesSent++;
    }
    
    /**
     * 发送批量包
     */
    private void sendBatch(List<QueuedPayload> payloads, ServerPlayerEntity player) {
        // 估算节省的字节数（每个包头部约 3 字节）
        bytesSaved += (payloads.size() - 1) * 3;
        
        // 创建批处理包
        BatchedPayload batchedPayload = new BatchedPayload(payloads.stream()
            .map(q -> q.payload)
            .toList());
        
        ServerPlayNetworking.send(player, batchedPayload);
        
        batchesSent++;
        payloadsSent += payloads.size();
    }
    
    /**
     * 清空队列
     */
    public void clear() {
        pendingPayloads.clear();
    }
    
    /**
     * 获取统计信息
     */
    public String getStats() {
        return String.format(
            "BatchedNetworkSync: payloads=%d, batches=%d, bytesSaved=%d, pending=%d",
            payloadsSent, batchesSent, bytesSaved, pendingPayloads.size()
        );
    }
    
    /**
     * 队列中的包
     */
    private static class QueuedPayload {
        final CustomPayload payload;
        final double priority;
        final ServerPlayerEntity player;
        final long queuedTime;
        
        QueuedPayload(CustomPayload payload, double priority, ServerPlayerEntity player, long queuedTime) {
            this.payload = payload;
            this.priority = priority;
            this.player = player;
            this.queuedTime = queuedTime;
        }
    }
    
    /**
     * 批处理网络包
     */
    public record BatchedPayload(List<CustomPayload> payloads) implements CustomPayload {
        public static final Id<BatchedPayload> ID = new Id<>(net.minecraft.util.Identifier.of("factorcraft", "batched_sync"));
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
