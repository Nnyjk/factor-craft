package com.factorcraft.module.technology;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

/**
 * 科技系统模块
 * 
 * 包含：
 * - 多方块结构检测
 * - 科技树系统
 * - 多方块蓝图
 */
public final class TechnologyModule implements FactorCraftModule {
    
    @Override
    public String moduleId() {
        return "technology";
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[TechnologyModule] 科技系统已初始化");
    }
}
