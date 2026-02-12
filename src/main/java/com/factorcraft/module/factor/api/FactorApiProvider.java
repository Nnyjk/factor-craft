package com.factorcraft.module.factor.api;

/**
 * 当前阶段先提供占位实现，后续可替换为真实服务实例。
 */
public final class FactorApiProvider {
    private static final FactorApi NOOP = new NoopFactorApi();

    private FactorApiProvider() {}

    public static FactorApi get() {
        return NOOP;
    }
}
