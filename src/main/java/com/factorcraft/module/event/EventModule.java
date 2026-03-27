package com.factorcraft.module.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件模块主类
 * 
 * 负责初始化和注册事件系统相关组件
 */
public class EventModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventModule.class);
    private static EventModule instance;
    
    private RandomEventManager randomEventManager;
    
    private EventModule() {
    }
    
    public static EventModule getInstance() {
        if (instance == null) {
            instance = new EventModule();
        }
        return instance;
    }
    
    /**
     * 初始化事件模块
     */
    public void initialize() {
        LOGGER.info("初始化事件模块...");
        
        this.randomEventManager = RandomEventManager.getInstance();
        
        // 注册服务器 tick 事件
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        
        // 注册命令
        EventCommands.register();
        
        LOGGER.info("事件模块初始化完成");
    }
    
    /**
     * 服务器 tick 回调
     */
    private void onServerTick(MinecraftServer server) {
        if (randomEventManager != null) {
            randomEventManager.tick(server);
        }
    }
    
    /**
     * 获取随机事件管理器
     */
    public RandomEventManager getRandomEventManager() {
        return randomEventManager;
    }
    
    /**
     * 手动触发事件（供命令使用）
     */
    public void triggerEvent(MinecraftServer server, net.minecraft.server.world.ServerWorld world, EventType type) {
        if (randomEventManager != null) {
            randomEventManager.triggerEvent(server, world, type);
        }
    }
}
