package com.factorcraft.module.creature;

/**
 * 怪物系统模块 (占位实现)
 * 
 * TODO: 待 Minecraft 实体注册 API 调研完成后实现
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
     */
    public void initialize() {
        // TODO: 注册怪物实体
        System.out.println("[CreatureModule] 怪物系统已初始化 (占位)");
    }
}
