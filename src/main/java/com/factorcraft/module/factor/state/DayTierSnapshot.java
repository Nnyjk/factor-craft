package com.factorcraft.module.factor.state;

/**
 * docs/06.1: 日切快照。
 */
public record DayTierSnapshot(
        long dayIndex,
        double dayAverage,
        double trend,
        double hysteresis,
        int nextTier
) {}
