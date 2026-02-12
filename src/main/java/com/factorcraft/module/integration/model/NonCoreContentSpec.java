package com.factorcraft.module.integration.model;

import java.util.Set;

/**
 * 非核心内容统一标准字段。
 */
public interface NonCoreContentSpec {
    String contentId();

    String sourcePackId();

    String displayName();

    NonCoreCategory category();

    StandardTierWindow tierWindow();

    int conductivityCost();

    Set<String> tags();
}
