package com.factorcraft.module.shared;

import com.factorcraft.FactorCraftMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组日志门面：统一模块级日志命名，便于检索与聚合。
 */
public final class ModuleLoggers {
    private ModuleLoggers() {}

    public static Logger forModule(String moduleId) {
        return LoggerFactory.getLogger(FactorCraftMod.MOD_ID + "." + moduleId);
    }
}
