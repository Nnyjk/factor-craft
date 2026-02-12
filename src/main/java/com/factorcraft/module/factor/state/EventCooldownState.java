package com.factorcraft.module.factor.state;

import java.util.Map;

/**
 * docs/06.1: 事件触发冷却。
 */
public record EventCooldownState(Map<String, Long> cooldownEndsAtTick) {}
