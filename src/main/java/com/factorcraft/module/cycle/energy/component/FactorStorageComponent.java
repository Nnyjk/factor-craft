package com.factorcraft.module.cycle.energy.component;

/**
 * Factor 存储组件接口
 * 
 * 定义 Factor 存储的通用接口
 * 
 * 实现此接口的 BlockEntity 可以存储 Factor 浓度
 */
public interface FactorStorageComponent {
    
    /**
     * 插入 Factor
     * 
     * @param amount 要插入的 Factor 量
     * @param simulate 如果为 true，只模拟不实际插入
     * @return 实际插入的量（剩余量）
     */
    double insertFactor(double amount, boolean simulate);
    
    /**
     * 提取 Factor
     * 
     * @param amount 要提取的 Factor 量
     * @param simulate 如果为 true，只模拟不实际提取
     * @return 实际提取的量
     */
    double extractFactor(double amount, boolean simulate);
    
    /**
     * 获取当前存储的 Factor 量
     * 
     * @return 当前存储量
     */
    double getStoredFactor();
    
    /**
     * 获取存储容量
     * 
     * @return 最大容量
     */
    double getCapacity();
    
    /**
     * 检查是否已满
     * 
     * @return 如果已满返回 true
     */
    default boolean isFull() {
        return getStoredFactor() >= getCapacity();
    }
    
    /**
     * 检查是否为空
     * 
     * @return 如果为空返回 true
     */
    default boolean isEmpty() {
        return getStoredFactor() <= 0;
    }
    
    /**
     * 获取填充率
     * 
     * @return 填充率 (0.0 - 1.0)
     */
    default double getFillRatio() {
        return getStoredFactor() / getCapacity();
    }
}
