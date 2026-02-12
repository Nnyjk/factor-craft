package com.factorcraft.module.factor.api;

/**
 * M1 后通过模块初始化注入真实服务，默认回退 noop。
 */
public final class FactorApiProvider {
    private static final FactorApi NOOP = new NoopFactorApi();
    private static volatile FactorApi active = NOOP;

    private FactorApiProvider() {}

    public static FactorApi get() {
        return active;
    }

    public static void set(FactorApi factorApi) {
        active = factorApi == null ? NOOP : factorApi;
    }

    public static void reset() {
        active = NOOP;
    }
}
