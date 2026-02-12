package com.factorcraft.module.event.bus;

import com.factorcraft.module.shared.ModuleLoggers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 简单同步事件总线实现。
 */
public final class SimpleFactorEventBus implements FactorEventBus {
    private static final org.slf4j.Logger LOG = ModuleLoggers.forModule("event_bus");
    private static final SimpleFactorEventBus INSTANCE = new SimpleFactorEventBus();

    private final Map<Class<?>, Map<EventPriority, List<Consumer<?>>>> subscribers = new ConcurrentHashMap<>();
    private volatile ErrorStrategy errorStrategy = ErrorStrategy.LOG_AND_CONTINUE;

    private SimpleFactorEventBus() {}

    public static SimpleFactorEventBus getInstance() {
        return INSTANCE;
    }

    public void setErrorStrategy(ErrorStrategy errorStrategy) {
        this.errorStrategy = errorStrategy;
    }

    @Override
    public <T> void subscribe(Class<T> eventType, EventPriority priority, Consumer<T> listener) {
        subscribers.computeIfAbsent(eventType, ignored -> new EnumMap<>(EventPriority.class))
                .computeIfAbsent(priority, ignored -> new ArrayList<>())
                .add(listener);
    }

    @Override
    public void publish(Object event) {
        if (event == null) {
            return;
        }
        Map<EventPriority, List<Consumer<?>>> byPriority = subscribers.get(event.getClass());
        if (byPriority == null) {
            return;
        }

        byPriority.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().ordinal()))
                .forEach(entry -> dispatch(entry.getValue(), event));
    }

    @Override
    public int subscriberCount(Class<?> eventType) {
        Map<EventPriority, List<Consumer<?>>> byPriority = subscribers.get(eventType);
        if (byPriority == null) {
            return 0;
        }
        return byPriority.values().stream().mapToInt(List::size).sum();
    }

    @SuppressWarnings("unchecked")
    private void dispatch(List<Consumer<?>> listeners, Object event) {
        for (Consumer<?> listener : listeners) {
            try {
                ((Consumer<Object>) listener).accept(event);
            } catch (Exception ex) {
                if (errorStrategy == ErrorStrategy.FAIL_FAST) {
                    throw ex;
                }
                LOG.error("事件处理失败: type={}, message={}", event.getClass().getSimpleName(), ex.getMessage(), ex);
            }
        }
    }
}
