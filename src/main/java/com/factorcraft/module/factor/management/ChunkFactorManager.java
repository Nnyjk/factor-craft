package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 区块 Factor 管理器
 * 
 * 提供内存缓存和便捷访问方法
 * 持久化由 ChunkFactorStorage 处理
 */
public class ChunkFactorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkFactorManager.class);
    
    /** 内存缓存（用于快速访问，由 ChunkFactorEventHandler 同步） */
    private static final Long2ObjectMap<ChunkFactorState> CHUNK_STATES = new Long2ObjectOpenHashMap<>();
    
    /**
     * 获取或创建区块状态（仅内存缓存）
     * 
     * 注意：推荐使用 ChunkFactorStorage.getChunkState() 来确保持久化
     */
    public static ChunkFactorState getOrCreateState(World world, ChunkPos pos) {
        long key = pos.toLong();
        return CHUNK_STATES.computeIfAbsent(key, k -> {
            double initialConcentration = calculateInitialConcentration(world, pos);
            return new ChunkFactorState(initialConcentration);
        });
    }
    
    /**
     * 获取区块状态（内存缓存）
     */
    public static Optional<ChunkFactorState> getState(ChunkPos pos) {
        return Optional.ofNullable(CHUNK_STATES.get(pos.toLong()));
    }
    
    /**
     * 设置区块状态（内存缓存）
     */
    public static void setState(ChunkPos pos, ChunkFactorState state) {
        CHUNK_STATES.put(pos.toLong(), state);
    }
    
    /**
     * 移除区块状态（内存缓存）
     */
    public static void removeState(ChunkPos pos) {
        CHUNK_STATES.remove(pos.toLong());
    }
    
    /**
     * 计算初始浓度
     */
    private static double calculateInitialConcentration(World world, ChunkPos pos) {
        String dimension = world.getRegistryKey().getValue().toString();
        double baseline = switch (dimension) {
            case "minecraft:the_nether" -> 70.0;
            case "minecraft:the_end" -> 100.0;
            default -> 40.0;
        };
        
        // 基于位置的确定性噪声
        long seed = pos.x * 341873128712L + pos.z * 132897987541L;
        double noise = ((seed % 41) - 20) / 2.0;
        
        return Math.max(0, baseline + noise);
    }
    
    /**
     * 更新区块（供外部调用）
     */
    public static void updateChunk(World world, ChunkPos pos) {
        ChunkFactorState state = getOrCreateState(world, pos);
        long currentTick = world.getTime();
        
        if (currentTick - state.getLastUpdatedTick() > 20) {
            // 自然衰减
            double decay = 0.001;
            state.setCurrentConcentration(state.getCurrentConcentration() - decay);
            state.setLastUpdatedTick(currentTick);
        }
    }
    
    /**
     * 从区块提取 Factor
     */
    public static void extractFactor(World world, ChunkPos pos, double amount) {
        ChunkFactorState state = getOrCreateState(world, pos);
        state.setCurrentConcentration(state.getCurrentConcentration() - amount);
        
        // 同步到持久化存储
        if (world instanceof ServerWorld serverWorld) {
            ChunkFactorStorage.get(serverWorld).updateState(pos, state);
        }
    }
    
    /**
     * 向区块注入 Factor
     */
    public static void injectFactor(World world, ChunkPos pos, double amount) {
        ChunkFactorState state = getOrCreateState(world, pos);
        state.setCurrentConcentration(state.getCurrentConcentration() + amount);
        
        // 同步到持久化存储
        if (world instanceof ServerWorld serverWorld) {
            ChunkFactorStorage.get(serverWorld).updateState(pos, state);
        }
    }
    
    /**
     * 清除内存缓存
     */
    public static void clear() {
        CHUNK_STATES.clear();
    }
    
    /**
     * 获取内存缓存中的区块数量
     */
    public static int getLoadedChunkCount() {
        return CHUNK_STATES.size();
    }
    
    /**
     * 获取所有已缓存区块
     */
    public static java.util.Set<ChunkPos> getAllLoadedChunks() {
        return CHUNK_STATES.keySet().stream()
            .mapToLong(Long::longValue)
            .mapToObj(ChunkPos::new)
            .collect(java.util.stream.Collectors.toSet());
    }
}