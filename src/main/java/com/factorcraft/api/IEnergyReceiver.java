package com.factorcraft.api;

/**
 * 能量接收器接口
 * 
 * 用于可以接收能量的方块/BlockEntity
 * 支持能量传输和存储
 */
public interface IEnergyReceiver {
    
    /**
     * 接收能量
     * 
     * @param maxReceive 最大接收量
     * @param simulate 是否模拟（不实际存储）
     * @return 实际接收的能量
     */
    int receiveEnergy(int maxReceive, boolean simulate);
    
    /**
     * 获取当前存储的能量
     */
    int getEnergyStored();
    
    /**
     * 获取最大能量容量
     */
    int getMaxEnergyStored();
    
    /**
     * 检查是否可以接收能量
     */
    boolean canReceive();
}