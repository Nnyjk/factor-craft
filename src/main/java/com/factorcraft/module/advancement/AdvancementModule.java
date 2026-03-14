package com.factorcraft.module.advancement;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

import java.util.List;

/**
 * 进度模块 - 管理游戏进度系统
 * 
 * 功能:
 * - 自定义进度定义
 * - 进度触发器
 * - 进度奖励
 */
public final class AdvancementModule implements FactorCraftModule {
    
    private static AdvancementModule instance;
    
    public AdvancementModule() {
        instance = this;
    }
    
    public static AdvancementModule getInstance() {
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "advancement";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of(); // 无依赖
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Advancement] 进度模块已加载");
    }
}