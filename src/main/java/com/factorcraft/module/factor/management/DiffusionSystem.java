package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import com.factorcraft.performance.OptimizedDiffusion;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factor 扩散系统
 * 处理区块之间的 Factor 浓度扩散
 * 
 * 接入方式：在 ServerTickEvents 中调用 processAllDiffusion()
 * 
 * @see com.factorcraft.performance.OptimizedDiffusion 高性能版本
 */
public class DiffusionSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffusionSystem.class);
    private static final double DIFFUSION_THRESHOLD = 20.0;
    private static final double DIFFUSION_COEFFICIENT = 0.1;
    
    /** 是否启用优化扩散算法 */
    private static final boolean USE_OPTIMIZED_DIFFUSION = true;
    
    /** 扩散处理间隔（tick） */
    private static final int DIFFUSION_INTERVAL = 100;
    
    /** 上次扩散处理的 tick */
    private static long lastDiffusionTick = 0;
    
    /**
     * 处理单个区块的扩散
     */
    public static void processDiffusion(World world, ChunkPos centerPos) {
        ChunkFactorState centerState = ChunkFactorManager.getState(centerPos).orElse(null);
        if (centerState == null) return;
        
        double centerConcentration = centerState.getCurrentConcentration();
        if (centerConcentration < DIFFUSION_THRESHOLD) return;
        
        // 检查相邻区块
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                
                ChunkPos neighborPos = new ChunkPos(centerPos.x + dx, centerPos.z + dz);
                ChunkFactorState neighborState = ChunkFactorManager.getOrCreateState(world, neighborPos);
                
                double neighborConcentration = neighborState.getCurrentConcentration();
                double difference = centerConcentration - neighborConcentration;
                
                if (difference > 0) {
                    double transfer = difference * DIFFUSION_COEFFICIENT;
                    centerState.setCurrentConcentration(centerConcentration - transfer);
                    neighborState.setCurrentConcentration(neighborConcentration + transfer * 0.8);
                }
            }
        }
    }
    
    /**
     * 处理所有已加载区块的扩散
     * 根据配置使用标准或优化算法
     */
    public static void processAllDiffusion(World world) {
        long currentTick = world.getTime();
        
        // 检查是否到达扩散间隔
        if (currentTick - lastDiffusionTick < DIFFUSION_INTERVAL) {
            return;
        }
        lastDiffusionTick = currentTick;
        
        if (USE_OPTIMIZED_DIFFUSION) {
            processOptimizedDiffusion(world);
        } else {
            processStandardDiffusion(world);
        }
        
        LOGGER.debug("[Diffusion] 完成扩散处理 @ tick {}", currentTick);
    }
    
    /**
     * 标准扩散处理（遍历所有区块）
     */
    private static void processStandardDiffusion(World world) {
        ChunkFactorManager.getAllLoadedChunks().forEach(pos -> processDiffusion(world, pos));
    }
    
    /**
     * 优化扩散处理（使用批量和优先级队列）
     */
    private static void processOptimizedDiffusion(World world) {
        if (!(world instanceof net.minecraft.server.world.ServerWorld serverWorld)) {
            return;
        }
        
        // 获取持久化存储中的所有区块
        ChunkFactorStorage storage = ChunkFactorStorage.get(serverWorld);
        Map<ChunkPos, ChunkFactorState> chunks = storage.getAllLoadedChunks().stream()
            .collect(Collectors.toMap(
                pos -> pos,
                pos -> storage.getState(pos).orElse(null),
                (a, b) -> a != null ? a : b
            ));
        
        // 移除空值
        chunks.entrySet().removeIf(e -> e.getValue() == null);
        
        if (chunks.isEmpty()) return;
        
        // 使用优先队列处理（优先处理高浓度区块）
        OptimizedDiffusion.processPriorityBased(chunks);
        
        // 同步回存储
        for (Map.Entry<ChunkPos, ChunkFactorState> entry : chunks.entrySet()) {
            storage.updateState(entry.getKey(), entry.getValue());
        }
    }
}