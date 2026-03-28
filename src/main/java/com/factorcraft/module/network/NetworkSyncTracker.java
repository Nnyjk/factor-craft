package com.factorcraft.module.network;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络同步追踪器
 * 
 * 追踪每个区块/机器的同步状态，用于条件同步
 * 
 * 功能:
 * - 追踪上次 Factor 浓度同步时间
 * - 追踪上次同步的浓度值
 * - 追踪上次机器状态同步时间
 * - 追踪上次同步的机器状态
 */
public class NetworkSyncTracker {
    
    // ==================== Chunk 同步追踪 ====================
    
    /** 上次 Factor 浓度同步时间 (tick) */
    private static final Long2ObjectMap<Long> CHUNK_LAST_SYNC_TICK = new Long2ObjectOpenHashMap<>();
    
    /** 上次同步的 Factor 浓度值 */
    private static final Long2ObjectMap<Double> CHUNK_LAST_CONCENTRATION = new Long2ObjectOpenHashMap<>();
    
    /**
     * 获取区块的长键
     */
    /**
     * 获取区块的长键
     */
    private static long getChunkKey(ChunkPos pos) {
        return pos.toLong();
    }
    
    /**
     * 获取区块的长键 (从坐标)
     */
    private static long getChunkKey(int chunkX, int chunkZ) {
        return ChunkPos.toLong(chunkX, chunkZ);
    }
    
    /**
     * 检查是否应该同步 Factor 浓度
     * 
     * @param pos 区块位置
     * @param currentTick 当前 tick
     * @param concentration 当前浓度
     * @return true 如果应该同步
     */
    public static boolean shouldSyncFactor(ChunkPos pos, long currentTick, double concentration) {
        long key = getChunkKey(pos);
        
        // 检查上次同步时间
        Long lastSyncTickObj = CHUNK_LAST_SYNC_TICK.get(key);
        long lastSyncTick = lastSyncTickObj != null ? lastSyncTickObj : -1L;
        if (currentTick - lastSyncTick < NetworkConfig.FACTOR_SYNC_INTERVAL_TICKS) {
            NetworkConfig.syncSkippedCooldown++;
            return false;
        }
        
        // 检查浓度变化
        Double lastConcentrationObj = CHUNK_LAST_CONCENTRATION.get(key);
        double lastConcentration = lastConcentrationObj != null ? lastConcentrationObj : 0.0;
        double diff = Math.abs(concentration - lastConcentration);
        if (diff < NetworkConfig.FACTOR_SYNC_THRESHOLD) {
            NetworkConfig.syncSkippedNoChange++;
            return false;
        }
        
        return true;
    }
    
    /**
     * 记录 Factor 浓度已同步
     */
    public static void markFactorSynced(ChunkPos pos, double concentration) {
        long key = getChunkKey(pos);
        long currentTick = System.currentTimeMillis() / 50; // 转换为 tick
        CHUNK_LAST_SYNC_TICK.put(key, Long.valueOf(currentTick));
        CHUNK_LAST_CONCENTRATION.put(key, Double.valueOf(concentration));
        NetworkConfig.factorSyncPacketsSent++;
    }
    
    /**
     * 清除区块追踪数据
     */
    public static void clearChunk(ChunkPos pos) {
        long key = getChunkKey(pos);
        CHUNK_LAST_SYNC_TICK.remove(key);
        CHUNK_LAST_CONCENTRATION.remove(key);
    }
    
    // ==================== Machine 同步追踪 ====================
    
    /** 上次机器状态同步时间 (ms) */
    private static final Long2ObjectMap<Long> MACHINE_LAST_SYNC_TIME = new Long2ObjectOpenHashMap<>();
    
    /** 上次同步的机器状态哈希 */
    private static final Long2ObjectMap<Integer> MACHINE_LAST_STATE_HASH = new Long2ObjectOpenHashMap<>();
    
    /**
     * 获取机器的长键
     */
    private static long getMachineKey(BlockPos pos) {
        return pos.asLong();
    }
    
    /**
     * 计算机器状态哈希
     */
    private static int calculateStateHash(String machineType, boolean isWorking, 
                                         double progress, double factorStorage,
                                         int energyStored) {
        int result = machineType.hashCode();
        result = 31 * result + Boolean.hashCode(isWorking);
        result = 31 * result + Double.hashCode(progress);
        result = 31 * result + Double.hashCode(factorStorage);
        result = 31 * result + Integer.hashCode(energyStored);
        return result;
    }
    
    /**
     * 检查是否应该同步机器状态
     * 
     * @param pos 机器位置
     * @param stateHash 当前状态哈希
     * @return true 如果应该同步
     */
    public static boolean shouldSyncMachine(BlockPos pos, int stateHash) {
        long key = getMachineKey(pos);
        
        // 检查冷却时间
        Long lastSyncTimeObj = MACHINE_LAST_SYNC_TIME.get(key);
        long lastSyncTime = lastSyncTimeObj != null ? lastSyncTimeObj : 0L;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSyncTime < NetworkConfig.MACHINE_SYNC_COOLDOWN_MS) {
            NetworkConfig.syncSkippedCooldown++;
            return false;
        }
        
        // 检查状态是否变化
        if (NetworkConfig.MACHINE_STATE_CHANGE_ONLY) {
            Integer lastHashObj = MACHINE_LAST_STATE_HASH.get(key);
            int lastHash = lastHashObj != null ? lastHashObj : -1;
            if (lastHash == stateHash) {
                NetworkConfig.syncSkippedNoChange++;
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 记录机器状态已同步
     */
    public static void markMachineSynced(BlockPos pos, int stateHash) {
        long key = getMachineKey(pos);
        MACHINE_LAST_SYNC_TIME.put(key, Long.valueOf(System.currentTimeMillis()));
        MACHINE_LAST_STATE_HASH.put(key, Integer.valueOf(stateHash));
        NetworkConfig.machineSyncPacketsSent++;
    }
    
    /**
     * 清除机器追踪数据
     */
    public static void clearMachine(BlockPos pos) {
        long key = getMachineKey(pos);
        MACHINE_LAST_SYNC_TIME.remove(key);
        MACHINE_LAST_STATE_HASH.remove(key);
    }
    
    /**
     * 清除所有追踪数据
     */
    public static void clearAll() {
        CHUNK_LAST_SYNC_TICK.clear();
        CHUNK_LAST_CONCENTRATION.clear();
        MACHINE_LAST_SYNC_TIME.clear();
        MACHINE_LAST_STATE_HASH.clear();
    }
}
