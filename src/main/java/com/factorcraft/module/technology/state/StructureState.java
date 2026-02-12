package com.factorcraft.module.technology.state;

import java.util.Map;

/**
 * docs/06.1: 多方块完整度、模块配置、功率预算。
 */
public record StructureState(
        boolean complete,
        Map<String, Integer> moduleLevels,
        int powerBudget
) {}
