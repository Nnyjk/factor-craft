package com.factorcraft.module.cycle.energy;

import com.factorcraft.module.cycle.energy.block.entity.FactorEnergyBlockEntities;
import com.factorcraft.module.cycle.energy.item.FactorEnergyItems;
import com.factorcraft.module.cycle.energy.screen.FactorEnergyScreenHandlers;
import net.minecraft.util.Identifier;

/**
 * Factor 能源模块
 * 
 * 负责 Factor 浓度驱动系统的核心功能：
 * - Factor 浓度消耗机制
 * - Factor 晶体储能系统
 * - Factor 泵（加速传输）
 * - 机器 Factor 消耗配置框架
 * 
 * 设计原则：
 * - Factor 作为唯一能源形式，不引入传统 FE 系统
 * - 机器直接消耗 Factor 浓度运行
 * - 浓度低于阈值时机器停止，恢复后自动重启
 */
public class FactorEnergyModule {
    
    private static boolean initialized = false;
    
    /**
     * 初始化能源模块
     * 
     * 调用顺序：
     * 1. 注册方块
     * 2. 注册物品
     * 3. 注册 BlockEntity
     * 4. 注册 ScreenHandler
     * 5. 注册组件
     */
    public static void init() {
        if (initialized) {
            return;
        }
        
        // 注册方块
        FactorEnergyBlocks.init();
        
        // 注册物品
        FactorEnergyItems.init();
        
        // 注册 BlockEntity
        FactorEnergyBlockEntities.init();
        
        // 注册 ScreenHandler
        FactorEnergyScreenHandlers.init();
        
        initialized = true;
        
        Identifier id = Identifier.of("factorcraft", "factor_energy");
        com.factorcraft.FactorCraftMod.LOGGER.info("Factor Energy Module initialized: {}", id);
    }
    
    /**
     * 检查模块是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * 客户端初始化
     */
    public static void initClient() {
        // 客户端特定的初始化逻辑
        // 如注册屏幕处理器、渲染器等
    }
}
