package com.factorcraft.module.cycle.energy.component;

/**
 * Factor 消费者组件接口
 * 
 * 定义机器消耗 Factor 浓度的通用接口
 * 
 * 实现此接口的 BlockEntity 可以消耗 Factor 浓度来运行
 */
public interface FactorConsumerComponent {
    
    /**
     * 消耗 Factor 浓度
     * 
     * @param amount 要消耗的 Factor 浓度量 (0.0 - 1.0)
     * @return 实际消耗的量
     */
    double consumeFactor(double amount);
    
    /**
     * 获取消耗速率
     * 
     * @return 每 tick 消耗的 Factor 浓度量
     */
    double getConsumptionRate();
    
    /**
     * 检查是否可以运行
     * 
     * @param concentration 当前区域的 Factor 浓度
     * @return 如果浓度足够返回 true
     */
    boolean canOperate(double concentration);
    
    /**
     * 获取最低工作浓度阈值
     * 
     * @return 最低浓度阈值 (0.0 - 1.0)
     */
    default double getMinConcentrationThreshold() {
        return 0.1; // 默认 10% 浓度
    }
    
    /**
     * 机器运行时调用
     * 
     * @param concentration 当前浓度
     */
    default void onOperate(double concentration) {
        consumeFactor(getConsumptionRate());
    }
}
