package com.factorcraft.api;

/**
 * Factor 容器接口
 * 
 * 用于标准化的 Factor 存储和传输操作
 * 实现此接口的方块/BlockEntity 可以与 Factor 电池交互
 */
public interface IFactorContainer {
    
    /**
     * 向容器添加 Factor
     * 
     * @param amount 添加量
     * @return 实际添加的量
     */
    double addFactor(double amount);
    
    /**
     * 从容器抽取 Factor
     * 
     * @param amount 抽取量
     * @return 实际抽取的量
     */
    double extractFactor(double amount);
    
    /**
     * 获取当前 Factor 存储量
     * 
     * @return 当前存储量
     */
    double getFactorStorage();
    
    /**
     * 获取最大 Factor 存储容量
     * 
     * @return 最大容量
     */
    double getMaxFactorStorage();
    
    /**
     * 检查容器是否可以接收 Factor
     * 
     * @return 是否可以接收
     */
    default boolean canReceiveFactor() {
        return getFactorStorage() < getMaxFactorStorage();
    }
    
    /**
     * 检查容器是否可以提取 Factor
     * 
     * @return 是否可以提取
     */
    default boolean canExtractFactor() {
        return getFactorStorage() > 0;
    }
}
