package com.factorcraft.module.creature;

/**
 * 怪物系统模块 (占位实现)
 * 
 * 待实现: Minecraft 实体注册 API 调研完成后实现完整的怪物系统
 */
public class CreatureModule {
    
    private static CreatureModule instance;
    
    private CreatureModule() {}
    
    public static CreatureModule getInstance() {
        if (instance == null) {
            instance = new CreatureModule();
        }
        return instance;
    }
    
    /**
     * 初始化怪物系统
     * 
     * 待实现: 注册怪物实体
     */
    public void initialize() {
        System.out.println("[CreatureModule] 怪物系统已初始化 (占位)");
    }
}