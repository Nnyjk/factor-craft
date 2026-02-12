package com.factorcraft.module.factor.state;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * docs/06.1: 事件触发冷却。
 */
public record EventCooldownState(Map<String, Long> cooldownEndsAtTick) {
    public EventCooldownState {
        cooldownEndsAtTick = Collections.unmodifiableMap(new LinkedHashMap<>(cooldownEndsAtTick));
    }

    public boolean isCoolingDown(String eventId, long currentTick) {
        return cooldownEndsAtTick.getOrDefault(eventId, 0L) > currentTick;
    }
}
