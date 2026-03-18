package com.factorcraft.module.creature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生物模块 API
 * 
 * @deprecated 使用 {@link com.factorcraft.module.creature.mutation.MutationManager} 和 
 * {@link com.factorcraft.module.creature.mutation.MutationDropRegistry} 替代
 */
@Deprecated
public class CreatureApi {
    
    public static final Logger LOGGER = LoggerFactory.getLogger("factorcraft/creature");
    
    /**
     * @deprecated 功能已迁移到 {@link com.factorcraft.module.creature.mutation.MutationManager}
     */
    @Deprecated
    public static void registerSpawnRules() {
        // 功能已迁移到 MutationManager
        LOGGER.debug("registerSpawnRules() called - functionality moved to MutationManager");
    }
    
    /**
     * @deprecated 功能已迁移到 {@link com.factorcraft.module.creature.mutation.MutationDropRegistry}
     */
    @Deprecated
    public static void registerDropPools() {
        // 功能已迁移到 MutationDropRegistry
        LOGGER.debug("registerDropPools() called - functionality moved to MutationDropRegistry");
    }
}
