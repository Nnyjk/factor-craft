package com.factorcraft.module.creature;

import com.factorcraft.module.creature.mutation.MutationManager;
import com.factorcraft.module.creature.mutation.MutationRegistry;
import net.minecraft.server.world.ServerWorld;

/**
 * 生物模块
 * 
 * 管理生物相关功能（变异、掉落、生成规则）
 */
public class CreatureModule {
    
    /** 变异管理器 */
    private static MutationManager mutationManager;
    
    /**
     * 初始化模块
     */
    public static void init() {
        // 注册变异效果
        MutationRegistry.init();
        
        // 创建管理器
        mutationManager = new MutationManager();
        
        CreatureApi.LOGGER.info("Creature module initialized");
    }
    
    /**
     * Tick 更新
     */
    public static void tick(ServerWorld world) {
        if (mutationManager != null) {
            mutationManager.tick(world);
        }
    }
    
    /**
     * 获取变异管理器
     */
    public static MutationManager getMutationManager() {
        return mutationManager;
    }
}
