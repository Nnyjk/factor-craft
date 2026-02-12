package com.factorcraft.module.event.bus;

import java.util.function.Consumer;

/**
 * 事件总线基础契约（M0.2）：
 * - 仅同步发布
 * - 支持优先级
 * - 支持错误处理策略
 */
public interface FactorEventBus {
    <T> void subscribe(Class<T> eventType, EventPriority priority, Consumer<T> listener);

    void publish(Object event);

    int subscriberCount(Class<?> eventType);

    enum ErrorStrategy {
        FAIL_FAST,
        LOG_AND_CONTINUE
    }
}
