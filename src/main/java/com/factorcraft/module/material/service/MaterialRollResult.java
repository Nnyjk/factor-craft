package com.factorcraft.module.material.service;

import java.util.List;

/**
 * 一条可演示链路：材料 -> 词条 -> 附魔 -> 状态。
 */
public record MaterialRollResult(
        long seed,
        String dimension,
        int tierLevel,
        String materialId,
        List<String> traitIds,
        List<String> enchantIds,
        List<StatusRoll> statuses
) {}
