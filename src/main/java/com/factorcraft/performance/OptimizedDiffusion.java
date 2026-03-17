package com.factorcraft.performance;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * 优化的扩散算法
 * 使用 BFS 和批量处理提高性能
 * 
 * 作为 DiffusionSystem 的高性能替代方案
 * 在 FactorSystemModule 中通过 USE_OPTIMIZED_DIFFUSION 配置启用
 * 
 * @see com.factorcraft.module.factor.management.DiffusionSystem
 * @see com.factorcraft.module.factor.FactorSystemModule
 */
public class OptimizedDiffusion {
    private static final int MAX_DIFFUSION_RADIUS = 3;
    
    /**
     * 处理所有区块的扩散
     * 包装 processBatch 方法，从 ChunkFactorManager 获取区块数据
     */
    public static void process(World world) {
        Map<ChunkPos, ChunkFactorState> chunks = ChunkFactorManager.getAllLoadedChunks()
            .stream()
            .collect(java.util.stream.Collectors.toMap(
                pos -> pos,
                pos -> ChunkFactorManager.getOrCreateState(world, pos)
            ));
        processBatch(chunks);
    }
    
    private static final double DIFFUSION_RATE = 0.1;
    private static final int BATCH_SIZE = 64;
    
    /**
     * 批量扩散处理
     * 减少迭代次数，提高性能
     */
    public static void processBatch(Map<ChunkPos, ChunkFactorState> chunks) {
        if (chunks.isEmpty()) return;
        
        // 收集所有需要扩散的区块
        List<DiffusionTask> tasks = new ArrayList<>();
        
        for (Map.Entry<ChunkPos, ChunkFactorState> entry : chunks.entrySet()) {
            ChunkPos pos = entry.getKey();
            ChunkFactorState state = entry.getValue();
            
            if (state.getCurrentConcentration() > state.getConcentrationFloor()) {
                tasks.add(new DiffusionTask(pos, state.getCurrentConcentration()));
            }
        }
        
        // 批量处理
        for (int i = 0; i < tasks.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, tasks.size());
            List<DiffusionTask> batch = tasks.subList(i, end);
            processDiffusionBatch(chunks, batch);
        }
    }
    
    private static void processDiffusionBatch(Map<ChunkPos, ChunkFactorState> chunks, List<DiffusionTask> batch) {
        for (DiffusionTask task : batch) {
            ChunkFactorState source = chunks.get(task.pos);
            if (source == null) continue;
            
            double concentration = task.concentration;
            double toDiffuse = concentration * DIFFUSION_RATE;
            
            // 4 方向扩散
            ChunkPos[] neighbors = {
                new ChunkPos(task.pos.x + 1, task.pos.z),
                new ChunkPos(task.pos.x - 1, task.pos.z),
                new ChunkPos(task.pos.x, task.pos.z + 1),
                new ChunkPos(task.pos.x, task.pos.z - 1)
            };
            
            double each = toDiffuse / 4.0;
            
            for (ChunkPos neighborPos : neighbors) {
                ChunkFactorState neighbor = chunks.get(neighborPos);
                if (neighbor != null && neighbor.getCurrentConcentration() < concentration) {
                    double diff = each * 0.5; // 扩散效率
                    neighbor.setCurrentConcentration(neighbor.getCurrentConcentration() + diff);
                    source.setCurrentConcentration(source.getCurrentConcentration() - diff);
                }
            }
        }
    }
    
    /**
     * 使用优先队列的智能扩散
     * 优先处理高浓度区块
     */
    public static void processPriorityBased(Map<ChunkPos, ChunkFactorState> chunks) {
        PriorityQueue<DiffusionTask> queue = new PriorityQueue<>(
            (a, b) -> Double.compare(b.concentration, a.concentration)
        );
        
        // 收集任务
        for (Map.Entry<ChunkPos, ChunkFactorState> entry : chunks.entrySet()) {
            double conc = entry.getValue().getCurrentConcentration();
            if (conc > entry.getValue().getConcentrationFloor()) {
                queue.offer(new DiffusionTask(entry.getKey(), conc));
            }
        }
        
        // 处理队列
        int processed = 0;
        while (!queue.isEmpty() && processed < 100) {
            DiffusionTask task = queue.poll();
            ChunkFactorState source = chunks.get(task.pos);
            
            if (source != null && source.getCurrentConcentration() > source.getConcentrationFloor()) {
                diffuseToNeighbors(chunks, task.pos, source);
                processed++;
            }
        }
    }
    
    private static void diffuseToNeighbors(Map<ChunkPos, ChunkFactorState> chunks, ChunkPos pos, ChunkFactorState source) {
        double concentration = source.getCurrentConcentration();
        double toDiffuse = concentration * DIFFUSION_RATE;
        
        ChunkPos[] neighbors = {
            new ChunkPos(pos.x + 1, pos.z),
            new ChunkPos(pos.x - 1, pos.z),
            new ChunkPos(pos.x, pos.z + 1),
            new ChunkPos(pos.x, pos.z - 1)
        };
        
        for (ChunkPos neighborPos : neighbors) {
            ChunkFactorState neighbor = chunks.get(neighborPos);
            if (neighbor != null && neighbor.getCurrentConcentration() < concentration) {
                double diff = toDiffuse * 0.25 * 0.5;
                neighbor.setCurrentConcentration(neighbor.getCurrentConcentration() + diff);
            }
        }
        
        source.setCurrentConcentration(concentration - toDiffuse * 0.5);
    }
    
    private record DiffusionTask(ChunkPos pos, double concentration) {}
}