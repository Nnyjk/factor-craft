package com.factorcraft.module.combat;

import com.factorcraft.module.combat.item.FactorSwordItem;
import com.factorcraft.module.combat.item.DimensionHammerItem;
import com.factorcraft.module.combat.item.ResonanceBowItem;

/**
 * 战斗系统模块
 * 
 * 包含：
 * - 武器系统 (剑/锤/弓)
 * - 怪物系统
 * - 掉落物系统
 */
public class CombatModule {
    
    private static CombatModule instance;
    
    private CombatModule() {}
    
    public static CombatModule getInstance() {
        if (instance == null) {
            instance = new CombatModule();
        }
        return instance;
    }
    
    /**
     * 初始化战斗系统
     */
    public void initialize() {
        // 注册所有武器
        FactorSwordItem.registerAll();
        DimensionHammerItem.registerAll();
        ResonanceBowItem.registerAll();
        
        System.out.println("[CombatModule] 战斗系统已初始化");
    }
}
