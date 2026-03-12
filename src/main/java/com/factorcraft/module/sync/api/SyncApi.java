package com.factorcraft.module.sync.api;

import com.factorcraft.module.sync.config.SyncConfig;

/**
 * 跨服同步 API
 */
public interface SyncApi {
    SyncConfig getSyncConfig();
    boolean isEnabled();
}