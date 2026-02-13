package com.factorcraft.module.material.state;

import com.factorcraft.module.material.service.StatusRoll;

import java.util.List;

/**
 * M2 日切状态池快照。
 */
public record MaterialDayStatusSnapshot(
        String worldKey,
        long dayIndex,
        int previousTier,
        int currentTier,
        List<StatusRoll> statuses
) {}
