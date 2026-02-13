package com.factorcraft.module.material.service;

import com.factorcraft.module.material.model.StatusType;

public record StatusRoll(
        String statusId,
        StatusType type,
        int durationSeconds,
        int stacks,
        String source
) {}
