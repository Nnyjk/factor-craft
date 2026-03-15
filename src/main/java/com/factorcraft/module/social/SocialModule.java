package com.factorcraft.module.social;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

import java.util.List;

/**
 * 社交模块 - 多人游戏功能
 * 
 * 功能:
 * - 权限管理
 */
public final class SocialModule implements FactorCraftModule {
    
    private static SocialModule instance;
    
    public SocialModule() {
        instance = this;
    }
    
    public static SocialModule getInstance() {
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "social";
    }
    
    @Override
    public List<String> dependencies() {
        return List.of(); // 无依赖
    }
    
    @Override
    public void initialize() {
        FactorCraftMod.LOGGER.info("[FactorCraft:Social] 社交模块已加载");
        FactorCraftMod.LOGGER.info("[FactorCraft:Social] 功能: 权限管理");
    }
}