package com.factorcraft.module.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Factor 浓度同步数据包
 * 
 * 用于服务器向客户端同步区块 Factor 浓度数据
 * 
 * 优化特性:
 * - 条件同步：使用 NetworkSyncTracker 检测浓度变化
 * - 批量同步：支持多个区块浓度合并发送
 * - 压缩支持：大数据量时自动压缩
 */
public record FactorSyncPayload(
    Identifier factorType,
    ChunkPos chunkPos,
    float concentration,
    long timestamp,
    boolean isBatch,
    BatchData batchData
) implements CustomPayload {
    
    public static final Id<FactorSyncPayload> ID = new Id<>(Identifier.of("factorcraft", "factor_sync"));
    
    /**
     * 批量同步数据结构
     */
    public record BatchData(
        int chunkCount,
        int[] chunkXs,
        int[] chunkZs,
        float[] concentrations
    ) {}
    
    public static final PacketCodec<RegistryByteBuf, BatchData> BATCH_DATA_CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, BatchData::chunkCount,
        intArrayCodec(), BatchData::chunkXs,
        intArrayCodec(), BatchData::chunkZs,
        floatArrayCodec(), BatchData::concentrations,
        BatchData::new
    );
    
    public static final PacketCodec<RegistryByteBuf, FactorSyncPayload> CODEC = PacketCodec.tuple(
        Identifier.PACKET_CODEC, FactorSyncPayload::factorType,
        ChunkPos.PACKET_CODEC, FactorSyncPayload::chunkPos,
        PacketCodecs.FLOAT, FactorSyncPayload::concentration,
        PacketCodecs.LONG, FactorSyncPayload::timestamp,
        PacketCodecs.BOOLEAN, FactorSyncPayload::isBatch,
        BATCH_DATA_CODEC, FactorSyncPayload::batchData,
        FactorSyncPayload::new
    );
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    /**
     * 创建单区块同步 Payload
     */
    public static FactorSyncPayload single(Identifier factorType, ChunkPos chunkPos, float concentration) {
        return new FactorSyncPayload(
            factorType,
            chunkPos,
            concentration,
            System.currentTimeMillis(),
            false,
            null
        );
    }
    
    /**
     * 创建批量同步 Payload
     * 
     * @param factorType Factor 类型
     * @param chunkPositions 区块位置列表
     * @param concentrations 对应的浓度列表
     * @return 批量同步 Payload
     */
    public static FactorSyncPayload batch(Identifier factorType, ChunkPos[] chunkPositions, float[] concentrations) {
        if (chunkPositions.length != concentrations.length) {
            throw new IllegalArgumentException("Chunk positions and concentrations must have same length");
        }
        
        int[] chunkXs = new int[chunkPositions.length];
        int[] chunkZs = new int[chunkPositions.length];
        for (int i = 0; i < chunkPositions.length; i++) {
            chunkXs[i] = chunkPositions[i].x;
            chunkZs[i] = chunkPositions[i].z;
        }
        
        return new FactorSyncPayload(
            factorType,
            chunkPositions.length > 0 ? chunkPositions[0] : new ChunkPos(0, 0),
            0.0f,
            System.currentTimeMillis(),
            true,
            new BatchData(chunkPositions.length, chunkXs, chunkZs, concentrations)
        );
    }
    
    /**
     * 条件同步到玩家
     * 
     * 使用 NetworkSyncTracker 检测浓度变化，仅当必要时才发送
     */
    public void conditionalSendTo(ServerPlayerEntity player) {
        if (isBatch) {
            conditionalBatchSendTo(player);
            return;
        }
        
        long currentTick = System.currentTimeMillis() / 50; // 转换为 tick
        
        if (NetworkSyncTracker.shouldSyncFactor(chunkPos, currentTick, concentration)) {
            ServerPlayNetworking.send(player, this);
            NetworkSyncTracker.markFactorSynced(chunkPos, concentration);
        }
    }
    
    /**
     * 批量条件同步到玩家
     * 
     * 遍历批量数据，仅同步变化的区块
     */
    public void conditionalBatchSendTo(ServerPlayerEntity player) {
        if (!isBatch || batchData == null) {
            conditionalSendTo(player);
            return;
        }
        
        // 过滤出需要同步的区块
        List<Integer> syncIndices = new ArrayList<>();
        long currentTick = System.currentTimeMillis() / 50;
        
        for (int i = 0; i < batchData.chunkCount; i++) {
            ChunkPos pos = new ChunkPos(batchData.chunkXs[i], batchData.chunkZs[i]);
            float conc = batchData.concentrations[i];
            
            if (NetworkSyncTracker.shouldSyncFactor(pos, currentTick, conc)) {
                syncIndices.add(i);
            }
        }
        
        // 如果有需要同步的区块，发送过滤后的包
        if (!syncIndices.isEmpty()) {
            int count = syncIndices.size();
            int[] chunkXs = new int[count];
            int[] chunkZs = new int[count];
            float[] concentrations = new float[count];
            
            for (int i = 0; i < count; i++) {
                int idx = syncIndices.get(i);
                chunkXs[i] = batchData.chunkXs[idx];
                chunkZs[i] = batchData.chunkZs[idx];
                concentrations[i] = batchData.concentrations[idx];
            }
            
            FactorSyncPayload filtered = batch(
                factorType,
                java.util.stream.IntStream.range(0, count)
                    .mapToObj(i -> new ChunkPos(chunkXs[i], chunkZs[i]))
                    .toArray(ChunkPos[]::new),
                concentrations
            );
            
            ServerPlayNetworking.send(player, filtered);
            
            // 更新追踪器
            for (int i = 0; i < count; i++) {
                ChunkPos pos = new ChunkPos(chunkXs[i], chunkZs[i]);
                NetworkSyncTracker.markFactorSynced(pos, concentrations[i]);
            }
        }
    }
    
    /**
     * 检查是否应该发送此包
     * 
     * @param player 目标玩家
     * @return true 如果应该发送
     */
    public boolean shouldSendTo(ServerPlayerEntity player) {
        if (isBatch) {
            return true; // 批量包总是发送（内部已过滤）
        }
        
        long currentTick = System.currentTimeMillis() / 50;
        return NetworkSyncTracker.shouldSyncFactor(chunkPos, currentTick, concentration);
    }
    
    /**
     * 创建 int 数组 PacketCodec
     */
    private static PacketCodec<RegistryByteBuf, int[]> intArrayCodec() {
        return new PacketCodec<>() {
            @Override
            public int[] decode(RegistryByteBuf buf) {
                int size = buf.readVarInt();
                int[] arr = new int[size];
                for (int i = 0; i < size; i++) {
                    arr[i] = buf.readVarInt();
                }
                return arr;
            }
            
            @Override
            public void encode(RegistryByteBuf buf, int[] arr) {
                buf.writeVarInt(arr.length);
                for (int value : arr) {
                    buf.writeVarInt(value);
                }
            }
        };
    }
    
    /**
     * 创建 float 数组 PacketCodec
     */
    private static PacketCodec<RegistryByteBuf, float[]> floatArrayCodec() {
        return new PacketCodec<>() {
            @Override
            public float[] decode(RegistryByteBuf buf) {
                int size = buf.readVarInt();
                float[] arr = new float[size];
                for (int i = 0; i < size; i++) {
                    arr[i] = buf.readFloat();
                }
                return arr;
            }
            
            @Override
            public void encode(RegistryByteBuf buf, float[] arr) {
                buf.writeVarInt(arr.length);
                for (float value : arr) {
                    buf.writeFloat(value);
                }
            }
        };
    }
}
