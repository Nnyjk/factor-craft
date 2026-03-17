package com.factorcraft.module.creature;

import com.factorcraft.module.creature.mutation.MutationManager;
import com.factorcraft.module.creature.mutation.MutationRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * 生物变异模块
 * 
 * 管理生物变异系统，包括：
 * - 高浓度区域变异生物生成
 * - 变异状态管理
 * - 变异效果应用
 */
public class CreatureMutationModule {
    
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
        
        // 注册生物生成事件监听器
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (world instanceof ServerWorld serverWorld && entity instanceof LivingEntity livingEntity) {
                // 生物加载时检查是否变异
                mutationManager.tryApplyMutation(livingEntity, serverWorld);
            }
        });
        
        CreatureApi.LOGGER.info("Creature mutation module initialized");
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
     * 检查并应用生物变异
     */
    public static void tryMutateCreature(LivingEntity creature, ServerWorld world) {
        if (mutationManager != null) {
            mutationManager.tryApplyMutation(creature, world);
        }
    }
    
    /**
     * 获取变异管理器
     */
    public static MutationManager getMutationManager() {
        return mutationManager;
    }
    
    /**
     * 移除生物变异
     */
    public static void removeCreatureMutations(LivingEntity creature) {
        if (mutationManager != null) {
            mutationManager.removeMutations(creature);
        }
    }
}
