package com.factorcraft.module.loot;

/**
 * 掉落物系统模块
 */
public class LootModule {
    
    private static LootModule instance;
    
    private LootModule() {}
    
    public static LootModule getInstance() {
        if (instance == null) {
            instance = new LootModule();
        }
        return instance;
    }
    
    /**
     * 初始化掉落物系统
     */
    public void initialize() {
        // 注册所有掉落物
        FactorShardItem.registerAll();
        ResonanceCoreItem.register();
        
        System.out.println("[LootModule] 掉落物系统已初始化");
    }
}
