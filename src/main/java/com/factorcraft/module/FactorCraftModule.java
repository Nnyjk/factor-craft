package com.factorcraft.module;

import java.util.List;

/**
 * 项目模块统一接口：用于明确模块边界，并统一初始化顺序。
 */
public interface FactorCraftModule {
    String moduleId();

    default List<String> dependencies() {
        return List.of();
    }

    void initialize();

    default void reload() {
        // optional
    }

    default void shutdown() {
        // optional
    }
}
