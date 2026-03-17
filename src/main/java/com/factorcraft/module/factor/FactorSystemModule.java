package com.factorcraft.module.factor;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.creature.CreatureMutationModule;
import com.factorcraft.module.cycle.network.FactorNetworkManager;
import com.factorcraft.module.event.FactorTierChangeEvent;
import com.factorcraft.module.event.FactorTideEvent;
import com.factorcraft.module.event.bus.EventPriority;
import com.factorcraft.module.event.bus.SimpleFactorEventBus;
import com.factorcraft.module.factor.api.FactorApiProvider;
import com.factorcraft.module.factor.management.ChunkFactorEventHandler;
import com.factorcraft.module.factor.management.DiffusionSystem;
import com.factorcraft.performance.OptimizedDiffusion;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Factor 系统模块
 * 
 * 核心功能：
 * - 实时 Factor 更新（基于潮汐系统）
 * - 日切结算（Tier 变更）
 * - 区块级 Factor 扩散
 * - 潮汐事件触发
 */
public final class FactorSystemModule implements FactorCraftModule {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static final FactorService SERVICE = new FactorService();
    private static FactorSystemModule instance;
    
    // 扩散检查间隔 (100 ticks = 5秒)
    private static final long DIFFUSION_INTERVAL = 100;
    
    
    // 使用优化扩散算法 (默认启用)
    public static final boolean USE_OPTIMIZED_DIFFUSION = true;
    public static FactorSystemModule getInstance() {
        if (instance == null) instance = new FactorSystemModule();
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "factor_system";
    }

    @Override
    public void initialize() {
        FactorApiProvider.set(SERVICE);
        
        if (REGISTERED.compareAndSet(false, true)) {
            // 注册区块 Factor 事件处理器
            ChunkFactorEventHandler.register();
            
            // 注册世界 tick 处理
            ServerTickEvents.END_WORLD_TICK.register(world -> {
                // Factor 系统核心 tick
                SERVICE.tick(world);
                
                // 潮汐效果管理器 tick（玩家效果、机器效率修正等）
                TideEffectManager.getInstance().tick(world);
                
                // Factor 网络传输 tick
                FactorNetworkManager.getInstance().tick(world);
                
                // 生物变异系统 tick
                CreatureMutationModule.tick(world);
                
                // 区块扩散处理
                long time = world.getTime();
                if (time % DIFFUSION_INTERVAL == 0) {
                    if (USE_OPTIMIZED_DIFFUSION) {
                        OptimizedDiffusion.process(world);
                    } else {
                        DiffusionSystem.processAllDiffusion(world);
                    }
                }
                
                // 调试日志
                if (time % 1200 == 0) {
                    FactorCraftMod.LOGGER.debug("[FactorCraft:Factor] {} {}", 
                        world.getRegistryKey().getValue(), 
                        SERVICE.debugHudLine(world));
                }
            });

            // Tier 变更事件监听
            SimpleFactorEventBus.getInstance().subscribe(
                FactorTierChangeEvent.class, 
                EventPriority.NORMAL, 
                event -> FactorCraftMod.LOGGER.info(
                    "[FactorCraft:Factor] 日切 Tier 变更: world={}, day={}, {} -> {}",
                    event.world().getRegistryKey().getValue(),
                    event.dayIndex(),
                    event.previousTier(),
                    event.currentTier()
                )
            );
            
            // 潮汐事件监听（示例）
            SimpleFactorEventBus.getInstance().subscribe(
                FactorTideEvent.class,
                EventPriority.NORMAL,
                event -> {
                    if (event.status().shouldTriggerEffects()) {
                        FactorCraftMod.LOGGER.debug(
                            "[FactorCraft:Factor] 潮汐状态: world={}, status={}, deviation={:.1f}%",
                            event.world().getRegistryKey().getValue(),
                            event.status().getName(),
                            event.deviation() * 100
                        );
                    }
                }
            );
        }

        FactorCraftMod.LOGGER.info(
            "[FactorCraft:Factor] 因子系统已启用 " +
            "(实时Factor/潮汐系统/日切Tier/区块扩散/阈值事件)");
    }

    @Override
    public void shutdown() {
        FactorApiProvider.reset();
    }
    
    /**
     * 获取 FactorService 实例
     */
    public static FactorService getService() {
        return SERVICE;
    }
}