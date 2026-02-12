package com.factorcraft.module.integration.model;

/**
 * 统一阶段窗口：用于描述适配阶段。
 */
public record StandardTierWindow(int minTier, int maxTier) {
    public StandardTierWindow {
        if (minTier < 0 || maxTier < minTier) {
            throw new IllegalArgumentException("invalid tier window");
        }
    }
}
