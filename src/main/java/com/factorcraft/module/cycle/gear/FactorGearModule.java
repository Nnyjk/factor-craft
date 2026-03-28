package com.factorcraft.module.cycle.gear;

/**
 * Factor 装备模块
 * 
 * 负责 R2.2 终极装备系统：
 * - 量子工具系列 (稿、斧、铲、锄、剑)
 * - Factor 盔甲系列 (头盔、胸甲、护腿、靴子)
 * - Data Components 注册
 * - 物品注册
 * 
 * 技术规格:
 * - 工具耐久度：10,000
 * - 盔甲耐久度：5,000/件
 * - 盔甲总防护：26 (超越下界合金 20)
 * - 充能系统：使用 DenseFactor 作为能源
 */
public class FactorGearModule {
    
    private static boolean initialized = false;
    
    /**
     * 初始化装备模块
     * 
     * 调用顺序：
     * 1. 注册 Data Components
     * 2. 注册物品
     */
    public static void init() {
        if (initialized) {
            return;
        }
        
        // 注册 Data Components
        FactorGearComponents.register();
        
        // 注册物品
        FactorGearItems.register();
        
        initialized = true;
        System.out.println("[FactorGear] 终极装备模块已初始化");
    }
    
    /**
     * 检查是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
