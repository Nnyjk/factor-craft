package com.factorcraft.module.technology.machine;

import com.factorcraft.config.MultiplayerBalanceConfig;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * R3.4 机器平衡管理器
 * 
 * 管理多机器协作时的产出平衡
 * 实现机器效率递减机制，防止过度堆叠
 */
public class MachineBalanceManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MachineBalanceManager.class);
    private static MachineBalanceManager instance;
    
    private final MultiplayerBalanceConfig config;
    
    /** 已注册的机器位置 (chunkKey -> 机器数量) */
    private final Long2IntMap machineCounts;
    
    /** 机器效率缓存 (chunkKey -> 效率倍数) */
    private final Map<Long, Double> efficiencyCache;
    
    private MachineBalanceManager() {
        this.config = MultiplayerBalanceConfig.getInstance();
        this.machineCounts = new Long2IntOpenHashMap();
        this.efficiencyCache = new ConcurrentHashMap<>();
    }
    
    /**
     * 获取实例
     */
    public static MachineBalanceManager getInstance() {
        if (instance == null) {
            instance = new MachineBalanceManager();
        }
        return instance;
    }
    
    /**
     * 注册机器位置
     * 
     * @param pos 机器位置
     */
    public void registerMachine(BlockPos pos) {
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        
        int count = machineCounts.getOrDefault(chunkKey, 0);
        machineCounts.put(chunkKey, count + 1);
        
        // 清除效率缓存
        efficiencyCache.remove(chunkKey);
        
        LOGGER.debug("注册机器：{} (区块机器数：{})", pos, count + 1);
    }
    
    /**
     * 注销机器位置
     * 
     * @param pos 机器位置
     */
    public void unregisterMachine(BlockPos pos) {
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        
        int count = machineCounts.getOrDefault(chunkKey, 0);
        if (count > 0) {
            machineCounts.put(chunkKey, count - 1);
            
            // 清除效率缓存
            efficiencyCache.remove(chunkKey);
            
            LOGGER.debug("注销机器：{} (区块机器数：{})", pos, count - 1);
        }
    }
    
    /**
     * 获取机器数量
     * 
     * @param pos 位置
     * @return 该区块的机器数量
     */
    public int getMachineCount(BlockPos pos) {
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        return machineCounts.getOrDefault(chunkKey, 0);
    }
    
    /**
     * 计算机器效率倍数
     * 
     * @param pos 机器位置
     * @return 效率倍数 (0.5-1.0)
     */
    public double getEfficiencyMultiplier(BlockPos pos) {
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        
        // 检查缓存
        Double cached = efficiencyCache.get(chunkKey);
        if (cached != null) {
            return cached;
        }
        
        if (!config.isEnabled()) {
            efficiencyCache.put(chunkKey, 1.0);
            return 1.0;
        }
        
        int machineCount = machineCounts.getOrDefault(chunkKey, 0);
        
        if (machineCount <= 1) {
            efficiencyCache.put(chunkKey, 1.0);
            return 1.0;
        }
        
        // 效率递减计算
        // 第 1 台：100%
        // 第 2 台：90%
        // 第 3 台：80%
        // ...
        // 最低 50%
        double decay = config.getMachineEfficiencyDecayRate();
        double minEfficiency = config.getMinEfficiencyMultiplier();
        
        double efficiency = 1.0 - (machineCount - 1) * decay;
        efficiency = Math.max(minEfficiency, efficiency);
        
        efficiencyCache.put(chunkKey, efficiency);
        
        LOGGER.debug("计算效率倍数：机器数={}, 效率={:.2f}", machineCount, efficiency);
        
        return efficiency;
    }
    
    /**
     * 计算多机器产出
     * 
     * @param baseOutput 基础产出
     * @param pos 机器位置
     * @return 调整后的产出
     */
    public int calculateOutput(int baseOutput, BlockPos pos) {
        double efficiency = getEfficiencyMultiplier(pos);
        return (int) Math.round(baseOutput * efficiency);
    }
    
    /**
     * 检查是否可以放置新机器
     * 
     * @param pos 位置
     * @return 是否可以放置
     */
    public boolean canPlaceMachine(BlockPos pos) {
        if (!config.isEnabled()) {
            return true;
        }
        
        long chunkKey = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
        int count = machineCounts.getOrDefault(chunkKey, 0);
        
        boolean canPlace = count < config.getMaxMachinesInRadius();
        
        if (!canPlace) {
            LOGGER.warn("机器密度过高，无法在 {} 放置新机器 (当前：{}, 最大：{})", 
                pos, count, config.getMaxMachinesInRadius());
        }
        
        return canPlace;
    }
    
    /**
     * 获取区域内的机器列表
     * 
     * @param center 中心位置
     * @param radius 半径
     * @return 机器位置列表
     */
    public List<BlockPos> getMachinesInRadius(BlockPos center, int radius) {
        List<BlockPos> machines = new ArrayList<>();
        
        // 遍历周围区块
        int chunkRadius = (radius >> 4) + 1;
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;
        
        for (int cx = centerChunkX - chunkRadius; cx <= centerChunkX + chunkRadius; cx++) {
            for (int cz = centerChunkZ - chunkRadius; cz <= centerChunkZ + chunkRadius; cz++) {
                long chunkKey = ChunkPos.toLong(cx, cz);
                int count = machineCounts.getOrDefault(chunkKey, 0);
                
                if (count > 0) {
                    // 这里可以扩展为返回具体机器位置
                    // 目前只返回区块中心
                    machines.add(new BlockPos(cx << 4, 0, cz << 4));
                }
            }
        }
        
        return machines;
    }
    
    /**
     * 清除所有注册信息
     */
    public void clear() {
        machineCounts.clear();
        efficiencyCache.clear();
        LOGGER.info("机器平衡管理器已重置");
    }
    
    /**
     * 获取统计信息
     * 
     * @return 统计信息字符串
     */
    public String getStats() {
        int totalMachines = machineCounts.values().intStream().sum();
        int chunksWithMachines = machineCounts.size();
        
        return String.format("机器平衡统计：总机器数=%d, 有机器区块数=%d, 缓存大小=%d",
            totalMachines, chunksWithMachines, efficiencyCache.size());
    }
}
