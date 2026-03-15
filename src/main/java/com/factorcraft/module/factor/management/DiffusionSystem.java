package com.factorcraft.module.factor.management;

import com.factorcraft.module.factor.state.ChunkFactorState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factor 扩散系统
 * 处理区块之间的 Factor 浓度扩散
 * 
 * TODO: 需要接入世界 tick 循环
 * 接入方式：在 ServerTickEvents 中调用 processAllDiffusion()
 * 
 * @see com.factorcraft.performance.OptimizedDiffusion 高性能版本
 */
public class DiffusionSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiffusionSystem.class);
    private static final double DIFFUSION_THRESHOLD = 20.0;
    private static final double DIFFUSION_COEFFICIENT = 0.1;
    
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
    
    public static void processAllDiffusion(World world) {
        ChunkFactorManager.getAllLoadedChunks().forEach(pos -> processDiffusion(world, pos));
    }
}