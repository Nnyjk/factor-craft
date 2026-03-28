package com.factorcraft.module.logistics.network;

import com.factorcraft.api.IFactorNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * 网络频道
 * 
 * 功能：
 * - 管理一组连接的物流节点
 * - 路由计算
 * - 优先级排序
 */
public class NetworkChannel {
    
    private final int id;
    private final String name;
    
    /** 网络中的所有节点 */
    private final Set<IFactorNetworkNode> nodes = new HashSet<>();
    
    /** 节点位置映射 */
    private final Map<BlockPos, IFactorNetworkNode> positionMap = new HashMap<>();
    
    /** 路由缓存 */
    private final Map<String, List<BlockPos>> routeCache = new HashMap<>();
    
    public NetworkChannel(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * 添加节点到网络
     */
    public void addNode(IFactorNetworkNode node) {
        nodes.add(node);
        positionMap.put(node.getNodePos(), node);
        clearCache();
    }
    
    /**
     * 从网络移除节点
     */
    public void removeNode(IFactorNetworkNode node) {
        nodes.remove(node);
        positionMap.remove(node.getNodePos());
        clearCache();
    }
    
    /**
     * 获取所有节点
     */
    public Set<IFactorNetworkNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }
    
    /**
     * 获取指定位置的节点
     */
    public IFactorNetworkNode getNodeAt(BlockPos pos) {
        return positionMap.get(pos);
    }
    
    /**
     * 计算两点之间的路由（BFS 算法）
     */
    public List<BlockPos> calculateRoute(BlockPos from, BlockPos to) {
        String cacheKey = from.toShortString() + "->" + to.toShortString();
        if (routeCache.containsKey(cacheKey)) {
            return routeCache.get(cacheKey);
        }
        
        List<BlockPos> route = bfsRoute(from, to);
        routeCache.put(cacheKey, route);
        return route;
    }
    
    private List<BlockPos> bfsRoute(BlockPos from, BlockPos to) {
        if (!positionMap.containsKey(from) || !positionMap.containsKey(to)) {
            return Collections.emptyList();
        }
        
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
        
        queue.offer(from);
        cameFrom.put(from, null);
        
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            
            if (current.equals(to)) {
                return reconstructPath(cameFrom, to);
            }
            
            // 检查相邻节点
            for (BlockPos neighbor : getNeighbors(current)) {
                if (positionMap.containsKey(neighbor) && !cameFrom.containsKey(neighbor)) {
                    queue.offer(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }
        
        return Collections.emptyList(); // 无路径
    }
    
    private List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
            neighbors.add(pos.offset(dir));
        }
        return neighbors;
    }
    
    private List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos end) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = end;
        
        while (current != null) {
            path.add(0, current);
            current = cameFrom.get(current);
        }
        
        return path;
    }
    
    /**
     * 清除路由缓存
     */
    public void clearCache() {
        routeCache.clear();
    }
    
    /**
     * 每 tick 更新
     */
    public void tick(World world) {
        // 定期清理缓存
        if (world.getTime() % 100 == 0) {
            clearCache();
        }
    }
}
