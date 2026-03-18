package com.factorcraft.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Factor 网络节点接口
 * 
 * 实现此接口的 BlockEntity 可以加入 Factor 网络
 */
public interface IFactorNetworkNode {
    
    /**
     * 获取节点唯一 ID
     * 
     * @return 节点 ID
     */
    String getNodeId();
    
    /**
     * 获取节点位置
     * 
     * @return 方块位置
     */
    BlockPos getNodePos();
    
    /**
     * 获取节点类型
     * 
     * @return 节点类型
     */
    NodeType getNodeType();
    
    /**
     * 获取当前 Factor 存储量
     * 
     * @return 存储量
     */
    double getFactorStorage();
    
    /**
     * 获取最大 Factor 存储容量
     * 
     * @return 最大容量
     */
    double getMaxFactorStorage();
    
    /**
     * 向节点添加 Factor
     * 
     * @param amount 添加量
     * @param from 来源节点 ID
     * @return 实际添加的量
     */
    double addFactor(double amount, String from);
    
    /**
     * 从节点抽取 Factor
     * 
     * @param amount 抽取量
     * @param to 目标节点 ID
     * @return 实际抽取的量
     */
    double extractFactor(double amount, String to);
    
    /**
     * 获取节点传输速率（每 tick）
     * 
     * @return 传输速率
     */
    double getTransferRate();
    
    /**
     * 检查节点是否可以接收 Factor
     * 
     * @return 是否可以接收
     */
    default boolean canReceiveFactor() {
        return getFactorStorage() < getMaxFactorStorage();
    }
    
    /**
     * 检查节点是否可以提取 Factor
     * 
     * @return 是否可以提取
     */
    default boolean canExtractFactor() {
        return getFactorStorage() > 0;
    }
    
    /**
     * 节点类型枚举
     */
    enum NodeType {
        SOURCE("源"),      // 从世界提取 Factor
        SINK("汇"),        // 输出 Factor 到机器
        TRANSMITTER("传递器"); // 网络传输节点
        
        private final String displayName;
        
        NodeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
