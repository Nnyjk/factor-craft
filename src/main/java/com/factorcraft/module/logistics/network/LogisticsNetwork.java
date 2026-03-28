package com.factorcraft.module.logistics.network;

import com.factorcraft.api.IFactorNetworkNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * 物流网络管理器
 * 
 * 功能：
 * - 管理多个物流网络（频道分离）
 * - 路由计算
 * - 自动请求处理
 */
public class LogisticsNetwork {
    
    private static LogisticsNetwork instance;
    
    /** 所有网络列表 */
    private final Map<Integer, NetworkChannel> networks = new HashMap<>();
    
    /** 下一个可用的网络 ID */
    private int nextNetworkId = 1;
    
    /** 位置到网络的映射 */
    private final Map<BlockPos, Integer> positionToNetwork = new HashMap<>();
    
    private LogisticsNetwork() {}
    
    public static void initialize() {
        instance = new LogisticsNetwork();
    }
    
    public static LogisticsNetwork getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LogisticsNetwork not initialized");
        }
        return instance;
    }
    
    /**
     * 创建新的物流网络
     */
    public int createNetwork(String name) {
        int id = nextNetworkId++;
        NetworkChannel channel = new NetworkChannel(id, name);
        networks.put(id, channel);
        return id;
    }
    
    /**
     * 获取指定 ID 的网络
     */
    public NetworkChannel getNetwork(int id) {
        return networks.get(id);
    }
    
    /**
     * 将节点添加到网络
     */
    public void addNodeToNetwork(int networkId, IFactorNetworkNode node) {
        NetworkChannel channel = networks.get(networkId);
        if (channel != null) {
            channel.addNode(node);
            positionToNetwork.put(node.getNodePos(), networkId);
        }
    }
    
    /**
     * 从网络移除节点
     */
    public void removeNodeFromNetwork(IFactorNetworkNode node) {
        Integer networkId = positionToNetwork.get(node.getNodePos());
        if (networkId != null) {
            NetworkChannel channel = networks.get(networkId);
            if (channel != null) {
                channel.removeNode(node);
            }
            positionToNetwork.remove(node.getNodePos());
        }
    }
    
    /**
     * 获取节点所在的网络 ID
     */
    public Integer getNetworkId(BlockPos pos) {
        return positionToNetwork.get(pos);
    }
    
    /**
     * 计算两点之间的路由
     */
    public List<BlockPos> calculateRoute(BlockPos from, BlockPos to) {
        Integer networkId = positionToNetwork.get(from);
        if (networkId == null || !networkId.equals(positionToNetwork.get(to))) {
            return Collections.emptyList(); // 不在同一网络
        }
        
        NetworkChannel channel = networks.get(networkId);
        return channel != null ? channel.calculateRoute(from, to) : Collections.emptyList();
    }
    
    /**
     * 每 tick 更新所有网络
     */
    public void tick(World world) {
        for (NetworkChannel channel : networks.values()) {
            channel.tick(world);
        }
    }
    
    /**
     * 从 NBT 加载网络数据
     */
    public void readNbt(net.minecraft.nbt.NbtCompound nbt) {
        // TODO: 实现 NBT 序列化
    }
    
    /**
     * 保存网络数据到 NBT
     */
    public net.minecraft.nbt.NbtCompound writeNbt() {
        net.minecraft.nbt.NbtCompound nbt = new net.minecraft.nbt.NbtCompound();
        // TODO: 实现 NBT 序列化
        return nbt;
    }
}
