package com.factorcraft.module.gear;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;

/**
 * Factor 装备模块
 * 
 * 提供 Factor 动力的工具和装备系统：
 * - Factor 工具：镐、斧、铲、剑（消耗 Factor 提升效率/伤害）
 * - Factor 护甲：头盔、胸甲、护腿、靴子（提供 Factor 相关能力）
 * - T1-T5 等级系统
 * - 充能机制
 */
public final class GearModule implements FactorCraftModule {
    
    @Override
    public String moduleId() {
        return "gear";
    }

    @Override
    public void initialize() {
        // 注册所有装备
        GearRegistry.register();
        
        FactorCraftMod.LOGGER.info("[FactorCraft:Gear] Factor 装备系统已加载");
    }
}