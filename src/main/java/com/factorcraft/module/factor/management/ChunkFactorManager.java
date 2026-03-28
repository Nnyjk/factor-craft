package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.optimization.FactorCalculationCache;
import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.performance.PerformanceConfig;
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
 * 集成缓存优化（R3.2）
 */
public class ChunkFactorManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkFactorManager.class);
    
    /** 内存缓存（用于快速访问，由 ChunkFactorEventHandler 同步） */
    private static final Long2ObjectMap<ChunkFactorState> CHUNK_STATES = new Long2ObjectOpenHashMap<>();
    
    /** Factor 计算缓存（R3.2 优化） */
    private static FactorCalculationCache calculationCache;
    
    /**
     * 初始化缓存（服务器启动时调用）
     */
    public static void initialize() {
        calculationCache = new FactorCalculationCache(
            PerformanceConfig.getInstance().getFactorCacheSize(),
            (long) PerformanceConfig.getInstance().getFactorCacheExpirySeconds()
        );
        LOGGER.info("ChunkFactorManager initialized with calculation cache");
    }
    
    /**
     * 获取缓存实例
     */
    public static FactorCalculationCache getCache() {
        if (calculationCache == null) {
            calculationCache = new FactorCalculationCache(
                PerformanceConfig.getInstance().getFactorCacheSize(),
                (long) PerformanceConfig.getInstance().getFactorCacheExpirySeconds()
            );
        }
        return calculationCache;
    }
    
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
        if (calculationCache != null) {
            calculationCache.invalidate(pos.toLong());
        }
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
     * R3.2 优化：使用缓存减少重复计算
     */
    public static void updateChunk(World world, ChunkPos pos) {
        ChunkFactorState state = getOrCreateState(world, pos);
        long currentTick = world.getTime();
        long chunkKey = pos.toLong();
        
        // 检查缓存
        if (calculationCache != null) {
            Double cachedDecay = calculationCache.get(chunkKey);
            if (cachedDecay != null) {
                // 使用缓存的衰减值
                state.setCurrentConcentration(state.getCurrentConcentration() - cachedDecay);
                state.setLastUpdatedTick(currentTick);
                return;
            }
        }
        
        if (currentTick - state.getLastUpdatedTick() > 20) {
            // 自然衰减
            double decay = 0.001;
            state.setCurrentConcentration(state.getCurrentConcentration() - decay);
            state.setLastUpdatedTick(currentTick);
            
            // 缓存计算结果
            if (calculationCache != null) {
                calculationCache.put(chunkKey, decay, currentTick);
            }
        }
    }
    
    /**
     * 增量更新区块 Factor 浓度
     * R3.2 新增：支持增量计算
     */
    public static void updateChunkIncremental(World world, ChunkPos pos, double delta) {
        ChunkFactorState state = getOrCreateState(world, pos);
        long currentTick = world.getTime();
        
        state.setCurrentConcentration(state.getCurrentConcentration() + delta);
        state.setLastUpdatedTick(currentTick);
        
        // 使缓存失效
        if (calculationCache != null) {
            calculationCache.invalidate(pos.toLong());
        }
    }
    
    /**
     * 从区块提取 Factor
     */
    public static void extractFactor(World world, ChunkPos pos, double amount) {
        ChunkFactorState state = getOrCreateState(world, pos);
        state.setCurrentConcentration(state.getCurrentConcentration() - amount);
        
        // 使缓存失效
        if (calculationCache != null) {
            calculationCache.invalidate(pos.toLong());
        }
        
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
        
        // 使缓存失效
        if (calculationCache != null) {
            calculationCache.invalidate(pos.toLong());
        }
        
        // 同步到持久化存储
        if (world instanceof ServerWorld serverWorld) {
            ChunkFactorStorage.get(serverWorld).updateState(pos, state);
        }
    }
    
    /**
     * 批量更新区块（R3.2 优化）
     * 限制每 tick 处理的区块数量以避免 TPS 下降
     */
    public static void batchUpdateChunks(ServerWorld world, int maxChunks) {
        PerformanceConfig config = PerformanceConfig.getInstance();
        int limit = config.maxChunksPerTick > 0 ? config.maxChunksPerTick : maxChunks;
        
        int processed = 0;
        long currentTick = world.getTime();
        
        for (long key : CHUNK_STATES.keySet()) {
            if (processed >= limit) {
                break;
            }
            
            ChunkFactorState state = CHUNK_STATES.get(key);
            if (state != null && currentTick - state.getLastUpdatedTick() > 20) {
                double decay = 0.001;
                state.setCurrentConcentration(state.getCurrentConcentration() - decay);
                state.setLastUpdatedTick(currentTick);
                
                // 缓存计算结果
                if (calculationCache != null) {
                    calculationCache.put(key, decay, currentTick);
                }
                
                processed++;
            }
        }
        
        if (processed > 0) {
            LOGGER.debug("Batch updated {} chunks", processed);
        }
    }
    
    /**
     * 清除内存缓存
     */
    public static void clear() {
        CHUNK_STATES.clear();
        if (calculationCache != null) {
            calculationCache.clear();
        }
    }
    
    /**
     * 获取内存缓存中的区块数量
     */
    public static int getLoadedChunkCount() {
        return CHUNK_STATES.size();
    }
    
    /**
     * 获取缓存统计信息
     */
    public static String getCacheStats() {
        if (calculationCache == null) {
            return "Cache not initialized";
        }
        return calculationCache.getStats();
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
