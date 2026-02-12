package com.factorcraft.module.integration.api;

/**
 * 第三方包实现该接口，向主模组注册非核心内容。
 */
public interface NonCoreContentProvider {
    String providerId();

    void register(NonCoreRegistrar registrar);
}
