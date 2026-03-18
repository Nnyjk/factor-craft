package com.factorcraft.module.event.worldevent;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Factor 世界事件模块
 * 
 * 功能：
 * - 事件触发与检测
 * - 活跃事件管理
 * - 效果应用
 * - 通知系统
 */
public final class FactorWorldEventModule implements FactorCraftModule {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static FactorWorldEventModule instance;
    
    public static FactorWorldEventModule getInstance() {
        if (instance == null) {
            instance = new FactorWorldEventModule();
        }
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "world_event";
    }
    
    @Override
    public void initialize() {
        if (REGISTERED.compareAndSet(false, true)) {
            // 注册世界 tick 处理
            ServerTickEvents.END_WORLD_TICK.register(world -> {
                WorldEventManager.getInstance().tick(world);
            });
            
            // 注册事件监听器（示例）
            registerEventListeners();
            
            FactorCraftMod.LOGGER.info(
                "[FactorCraft:WorldEvent] 世界事件系统已启用 " +
                "(5 种事件类型/自动触发/效果系统/通知系统)");
        }
    }
    
    @Override
    public void shutdown() {
        // 清理活跃事件
        WorldEventManager.getInstance().getActiveEvents()
            .forEach(e -> e.forceEnd());
    }
    
    /**
     * 注册事件监听器
     */
    private void registerEventListeners() {
        // 监听事件触发
        SimpleFactorEventBus.getInstance().subscribe(
            WorldEventTriggeredEvent.class,
            EventPriority.NORMAL,
            event -> {
                FactorCraftMod.LOGGER.info(
                    "[WorldEvent] Event triggered: {} at {}",
                    event.getType().getDisplayName(),
                    event.event().getCenterPos()
                );
            }
        );
        
        // 监听事件结束
        SimpleFactorEventBus.getInstance().subscribe(
            WorldEventEndedEvent.class,
            EventPriority.NORMAL,
            event -> {
                FactorCraftMod.LOGGER.info(
                    "[WorldEvent] Event ended: {} after {} ticks",
                    event.getType().getDisplayName(),
                    event.getDuration()
                );
            }
        );
    }
}