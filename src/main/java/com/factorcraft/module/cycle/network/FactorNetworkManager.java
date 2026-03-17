package com.factorcraft.module.cycle.network;

import com.factorcraft.api.IFactorNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Factor 网络管理器
 * 
 * 管理 Factor 网络拓扑、路由和传输
 * 
 * 功能：
 * - 网络节点注册/注销
 * - 网络拓扑检测（连通性分析）
 * - 路由计算（最短路径）
 * - 流量控制和传输
 */
public class FactorNetworkManager {
    
    private static FactorNetworkManager instance;
    
    /** 所有注册的节点，按维度分组 */
    private final Map<String, Map<BlockPos, IFactorNetworkNode>> nodesByDimension;
    
    /** 网络连接关系：节点 ID -> 相邻节点 ID 列表 */
    private final Map<String, Set<String>> connections;
    
    /** 网络缓存：源节点 -> 可到达的汇节点列表 */
    private final Map<String, List<String>> routeCache;
    
    /** 网络更新标记 */
    private boolean networkDirty;
    
    /** 最大传输距离（格） */
    private static final double MAX_TRANSFER_DISTANCE = 64.0;
    
    /** 缓存过期时间（ticks） */
    private static final int CACHE_EXPIRY_TICKS = 100;
    
    /** 上次网络更新时间 */
    private long lastUpdateTime;
    
    private FactorNetworkManager() {
        this.nodesByDimension = new HashMap<>();
        this.connections = new HashMap<>();
        this.routeCache = new HashMap<>();
        this.networkDirty = true;
        this.lastUpdateTime = 0;
    }
    
    public static FactorNetworkManager getInstance() {
        if (instance == null) {
            instance = new FactorNetworkManager();
        }
        return instance;
    }
    
    /**
     * 注册网络节点
     * 
     * @param world 世界
     * @param node 网络节点
     */
    public void registerNode(World world, IFactorNetworkNode node) {
        String dimension = getDimensionId(world);
        nodesByDimension
            .computeIfAbsent(dimension, k -> new HashMap<>())
            .put(node.getNodePos(), node);
        
        networkDirty = true;
    }
    
    /**
     * 注销网络节点
     * 
     * @param world 世界
     * @param pos 节点位置
     */
    public void unregisterNode(World world, BlockPos pos) {
        String dimension = getDimensionId(world);
        Map<BlockPos, IFactorNetworkNode> dimNodes = nodesByDimension.get(dimension);
        if (dimNodes != null) {
            IFactorNetworkNode removed = dimNodes.remove(pos);
            if (removed != null) {
                connections.remove(removed.getNodeId());
                // 移除其他节点到此节点的连接
                connections.values().forEach(conn -> conn.remove(removed.getNodeId()));
                networkDirty = true;
            }
        }
    }
    
    /**
     * 连接两个节点
     * 
     * @param node1 节点 1
     * @param node2 节点 2
     */
    public void connectNodes(IFactorNetworkNode node1, IFactorNetworkNode node2) {
        connections
            .computeIfAbsent(node1.getNodeId(), k -> new HashSet<>())
            .add(node2.getNodeId());
        connections
            .computeIfAbsent(node2.getNodeId(), k -> new HashSet<>())
            .add(node1.getNodeId());
        networkDirty = true;
    }
    
    /**
     * 断开节点连接
     * 
     * @param node1 节点 1
     * @param node2 节点 2
     */
    public void disconnectNodes(IFactorNetworkNode node1, IFactorNetworkNode node2) {
        Set<String> conn1 = connections.get(node1.getNodeId());
        if (conn1 != null) {
            conn1.remove(node2.getNodeId());
        }
        Set<String> conn2 = connections.get(node2.getNodeId());
        if (conn2 != null) {
            conn2.remove(node1.getNodeId());
        }
        networkDirty = true;
    }
    
    /**
     * 每 tick 调用，处理网络传输
     * 
     * @param world 世界
     */
    public void tick(World world) {
        String dimension = getDimensionId(world);
        Map<BlockPos, IFactorNetworkNode> dimNodes = nodesByDimension.get(dimension);
        
        if (dimNodes == null || dimNodes.isEmpty()) {
            return;
        }
        
        // 检查是否需要重建网络拓扑
        if (networkDirty || System.currentTimeMillis() - lastUpdateTime > CACHE_EXPIRY_TICKS * 50) {
            rebuildNetworkTopology(dimension);
        }
        
        // 处理每个源节点的传输
        dimNodes.values().stream()
            .filter(node -> node.getNodeType() == IFactorNetworkNode.NodeType.SOURCE)
            .forEach(source -> processSourceTransfer(source, dimension));
    }
    
    /**
     * 处理源节点的 Factor 传输
     */
    private void processSourceTransfer(IFactorNetworkNode source, String dimension) {
        if (!source.canExtractFactor()) {
            return;
        }
        
        // 找到可到达的汇节点
        List<String> reachableSinks = findReachableSinks(source, dimension);
        if (reachableSinks.isEmpty()) {
            return;
        }
        
        // 计算可传输量
        double transferRate = source.getTransferRate();
        double availableFactor = Math.min(source.getFactorStorage(), transferRate);
        
        if (availableFactor <= 0) {
            return;
        }
        
        // 平均分配给所有可达汇节点
        double amountPerSink = availableFactor / reachableSinks.size();
        
        for (String sinkId : reachableSinks) {
            IFactorNetworkNode sink = getNodeById(dimension, sinkId);
            if (sink != null && sink.canReceiveFactor()) {
                double extracted = source.extractFactor(amountPerSink, sinkId);
                sink.addFactor(extracted, source.getNodeId());
            }
        }
    }
    
    /**
     * 查找从源节点可到达的所有汇节点
     */
    private List<String> findReachableSinks(IFactorNetworkNode source, String dimension) {
        // 检查缓存
        if (routeCache.containsKey(source.getNodeId())) {
            return routeCache.get(source.getNodeId());
        }
        
        List<String> reachableSinks = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        
        queue.add(source.getNodeId());
        visited.add(source.getNodeId());
        
        while (!queue.isEmpty()) {
            String nodeId = queue.poll();
            IFactorNetworkNode node = getNodeById(dimension, nodeId);
            
            if (node == null) {
                continue;
            }
            
            if (node.getNodeType() == IFactorNetworkNode.NodeType.SINK) {
                reachableSinks.add(nodeId);
            }
            
            // 遍历相邻节点
            Set<String> neighbors = connections.get(nodeId);
            if (neighbors != null) {
                for (String neighborId : neighbors) {
                    if (!visited.contains(neighborId)) {
                        visited.add(neighborId);
                        queue.add(neighborId);
                    }
                }
            }
        }
        
        // 缓存结果
        routeCache.put(source.getNodeId(), reachableSinks);
        return reachableSinks;
    }
    
    /**
     * 重建网络拓扑
     */
    private void rebuildNetworkTopology(String dimension) {
        Map<BlockPos, IFactorNetworkNode> dimNodes = nodesByDimension.get(dimension);
        if (dimNodes == null) {
            return;
        }
        
        // 清除旧连接
        connections.clear();
        routeCache.clear();
        
        // 自动检测相邻节点并建立连接
        List<IFactorNetworkNode> nodeList = new ArrayList<>(dimNodes.values());
        for (int i = 0; i < nodeList.size(); i++) {
            for (int j = i + 1; j < nodeList.size(); j++) {
                IFactorNetworkNode node1 = nodeList.get(i);
                IFactorNetworkNode node2 = nodeList.get(j);
                
                if (areNodesConnected(node1, node2)) {
                    connectNodes(node1, node2);
                }
            }
        }
        
        networkDirty = false;
        lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * 检查两个节点是否可以连接（在有效距离内）
     */
    private boolean areNodesConnected(IFactorNetworkNode node1, IFactorNetworkNode node2) {
        double distance = node1.getNodePos().getSquaredDistance(node2.getNodePos());
        return distance <= MAX_TRANSFER_DISTANCE * MAX_TRANSFER_DISTANCE;
    }
    
    /**
     * 根据 ID 获取节点
     */
    @Nullable
    private IFactorNetworkNode getNodeById(String dimension, String nodeId) {
        Map<BlockPos, IFactorNetworkNode> dimNodes = nodesByDimension.get(dimension);
        if (dimNodes == null) {
            return null;
        }
        return dimNodes.values().stream()
            .filter(node -> node.getNodeId().equals(nodeId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 获取维度 ID
     */
    private String getDimensionId(World world) {
        return world.getRegistryKey().getValue().toString();
    }
    
    /**
     * 获取网络统计信息
     */
    public NetworkStats getNetworkStats(String dimension) {
        Map<BlockPos, IFactorNetworkNode> dimNodes = nodesByDimension.get(dimension);
        if (dimNodes == null) {
            return new NetworkStats(0, 0, 0, 0, 0);
        }
        
        int sourceCount = 0;
        int sinkCount = 0;
        int transmitterCount = 0;
        double totalFactor = 0;
        double totalCapacity = 0;
        
        for (IFactorNetworkNode node : dimNodes.values()) {
            totalFactor += node.getFactorStorage();
            totalCapacity += node.getMaxFactorStorage();
            
            switch (node.getNodeType()) {
                case SOURCE -> sourceCount++;
                case SINK -> sinkCount++;
                case TRANSMITTER -> transmitterCount++;
            }
        }
        
        return new NetworkStats(sourceCount, sinkCount, transmitterCount, totalFactor, totalCapacity);
    }
    
    /**
     * 网络统计信息
     */
    public record NetworkStats(
        int sourceCount,
        int sinkCount,
        int transmitterCount,
        double totalFactor,
        double totalCapacity
    ) {}
}
