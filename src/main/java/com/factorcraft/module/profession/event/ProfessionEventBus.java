package com.factorcraft.module.profession.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * 职业事件总线
 */
public class ProfessionEventBus {
    private static final Logger LOGGER = LoggerFactory.getLogger("FactorCraft/ProfessionEventBus");
    private static ProfessionEventBus instance;
    
    private final Map<ProfessionEventType, List<Consumer<ProfessionEvent>>> listeners = new EnumMap<>(ProfessionEventType.class);
    
    private ProfessionEventBus() {}
    
    public static ProfessionEventBus getInstance() {
        if (instance == null) {
            instance = new ProfessionEventBus();
        }
        return instance;
    }
    
    /**
     * 初始化事件总线
     */
    public static void init() {
        LOGGER.info("职业事件总线初始化完成");
    }
    
    /**
     * 订阅事件
     */
    public void subscribe(ProfessionEventType type, Consumer<ProfessionEvent> listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
        LOGGER.debug("订阅事件: {}", type);
    }
    
    /**
     * 取消订阅
     */
    public void unsubscribe(ProfessionEventType type, Consumer<ProfessionEvent> listener) {
        List<Consumer<ProfessionEvent>> list = listeners.get(type);
        if (list != null) {
            list.remove(listener);
        }
    }
    
    /**
     * 发布事件
     * @return 如果事件被取消返回false，否则返回true
     */
    public boolean post(ProfessionEvent event) {
        List<Consumer<ProfessionEvent>> list = listeners.get(event.getType());
        if (list == null || list.isEmpty()) {
            return true;
        }
        
        for (Consumer<ProfessionEvent> listener : list) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                LOGGER.error("处理事件 {} 时发生错误", event.getType(), e);
            }
        }
        
        return !event.isCancelled();
    }
    
    /**
     * 清除所有监听器
     */
    public void clear() {
        listeners.clear();
    }
}