package com.factorcraft.module.material.api;

public final class MaterialApiProvider {
    private static final MaterialApi NOOP = new NoopMaterialApi();
    private static volatile MaterialApi active = NOOP;

    private MaterialApiProvider() {}

    public static MaterialApi get() {
        return active;
    }

    public static void set(MaterialApi materialApi) {
        active = materialApi == null ? NOOP : materialApi;
    }

    public static void reset() {
        active = NOOP;
    }
}
