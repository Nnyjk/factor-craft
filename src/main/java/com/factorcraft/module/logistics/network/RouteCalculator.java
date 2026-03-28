package com.factorcraft.module.logistics.network;

import com.factorcraft.api.IFactorNetworkNode;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * 物流网络路由计算器
 * 
 * 功能：
 * - 计算两点之间的最优路径
 * - 考虑管道优先级
 * - 缓存路由结果
 */
public class RouteCalculator {
    
    /** 路由缓存 */
    private static final Map<String, List<BlockPos>> ROUTE_CACHE = new HashMap<>();
    
    /** 缓存有效期（毫秒） */
    private static final long CACHE_EXPIRY_MS = 5000L;
    
    /** 缓存时间戳 */
    private static final Map<String, Long> CACHE_TIMESTAMPS = new HashMap<>();
    
    /**
     * 计算从起点到终点的路由
     * 
     * @param network 物流网络
     * @param start 起点
     * @param end 终点
     * @return 路径（方块位置列表），如果无法到达则返回空列表
     */
    public static List<BlockPos> calculateRoute(LogisticsNetwork network, BlockPos start, BlockPos end) {
        String cacheKey = start.toShortString() + "->" + end.toShortString();
        
        // 检查缓存
        if (isCacheValid(cacheKey)) {
            return ROUTE_CACHE.get(cacheKey);
        }
        
        // 使用 BFS 寻找路径
        List<BlockPos> path = findPath(network, start, end);
        
        // 缓存结果
        if (!path.isEmpty()) {
            ROUTE_CACHE.put(cacheKey, path);
            CACHE_TIMESTAMPS.put(cacheKey, System.currentTimeMillis());
        }
        
        return path;
    }
    
    /**
     * 检查缓存是否有效
     */
    private static boolean isCacheValid(String cacheKey) {
        if (!ROUTE_CACHE.containsKey(cacheKey)) {
            return false;
        }
        
        Long timestamp = CACHE_TIMESTAMPS.get(cacheKey);
        if (timestamp == null) {
            return false;
        }
        
        return System.currentTimeMillis() - timestamp < CACHE_EXPIRY_MS;
    }
    
    /**
     * 使用 BFS 寻找路径
     */
    private static List<BlockPos> findPath(LogisticsNetwork network, BlockPos start, BlockPos end) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>();
        
        queue.offer(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            
            if (current.equals(end)) {
                // 找到目标，重建路径
                return reconstructPath(parentMap, start, end);
            }
            
            // 获取相邻的已连接节点
            for (BlockPos neighbor : getConnectedNeighbors(network, current)) {
                if (!visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }
        
        // 无法到达
        return Collections.emptyList();
    }
    
    /**
     * 获取已连接的相邻节点
     */
    private static List<BlockPos> getConnectedNeighbors(LogisticsNetwork network, BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        
        // 检查六个方向
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 1) continue; // 只考虑直接相邻
                    
                    BlockPos neighbor = pos.add(dx, dy, dz);
                    // 检查邻居节点是否存在于同一网络中
                    Integer networkId = network.getNetworkId(pos);
                    Integer neighborNetworkId = network.getNetworkId(neighbor);
                    if (networkId != null && networkId.equals(neighborNetworkId)) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        
        return neighbors;
    }
    
    /**
     * 重建路径
     */
    private static List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> parentMap, 
                                                   BlockPos start, BlockPos end) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = end;
        
        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }
        
        Collections.reverse(path);
        return path;
    }
    
    /**
     * 计算路径总长度
     */
    public static int getPathLength(List<BlockPos> path) {
        if (path.isEmpty()) {
            return 0;
        }
        
        int length = 0;
        for (int i = 1; i < path.size(); i++) {
            length += path.get(i - 1).getManhattanDistance(path.get(i));
        }
        
        return length;
    }
    
    /**
     * 清除缓存
     */
    public static void clearCache() {
        ROUTE_CACHE.clear();
        CACHE_TIMESTAMPS.clear();
    }
    
    /**
     * 清除过期缓存
     */
    public static void cleanupExpiredCache() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = CACHE_TIMESTAMPS.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (now - entry.getValue() >= CACHE_EXPIRY_MS) {
                iterator.remove();
                ROUTE_CACHE.remove(entry.getKey());
            }
        }
    }
}
