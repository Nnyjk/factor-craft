package com.factorcraft.module.cycle;

import com.factorcraft.module.cycle.block.CycleBlocks;
import com.factorcraft.module.cycle.block.entity.CycleBlockEntities;

/**
 * Factor 循环模块 - 核心模块
 * 
 * 基于 docs/17_factor_cycle_structures.md
 */
public class CycleModule {
    
    private static CycleModule instance;
    
    private CycleModule() {
        // 初始化循环模块
    }
    
    public static CycleModule getInstance() {
        if (instance == null) {
            instance = new CycleModule();
        }
        return instance;
    }
    
    /**
     * 初始化循环模块
     */
    public void initialize() {
        // 注册方块
        CycleBlocks.register();
        
        // 注册 BlockEntity
        CycleBlockEntities.register();
        
        // TODO: 注册配方
        // TODO: 初始化网络
    }
}
