package com.factorcraft.module;

/**
 * 项目模块统一接口：用于明确模块边界，并统一初始化顺序。
 */
public interface FactorCraftModule {
    String moduleId();

    void initialize();
}
