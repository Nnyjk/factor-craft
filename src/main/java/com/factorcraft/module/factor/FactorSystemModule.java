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
import com.factorcraft.performance.ChunkFactorCache;
import com.factorcraft.performance.OptimizedDiffusion;
import com.factorcraft.performance.PerformanceMonitor;
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
 * - 性能监控集成
 */
public final class FactorSystemModule implements FactorCraftModule {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static final FactorService SERVICE = new FactorService();
    private static FactorSystemModule instance;
    
    // 扩散检查间隔 (100 ticks = 5秒)
    private static final long DIFFUSION_INTERVAL = 100;
    
    // 性能监控开关
    public static boolean ENABLE_PERFORMANCE_MONITORING = true;
    
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
                long tickStart = ENABLE_PERFORMANCE_MONITORING ? System.nanoTime() : 0;
                
                // Factor 系统核心 tick
                PerformanceMonitor.startTracking("factor_tick");
                SERVICE.tick(world);
                PerformanceMonitor.endTracking("factor_tick");
                
                // 潮汐效果管理器 tick（玩家效果、机器效率修正等）
                PerformanceMonitor.startTracking("tide_effects");
                TideEffectManager.getInstance().tick(world);
                PerformanceMonitor.endTracking("tide_effects");
                
                // Factor 网络传输 tick
                PerformanceMonitor.startTracking("network_tick");
                FactorNetworkManager.getInstance().tick(world);
                PerformanceMonitor.endTracking("network_tick");
                
                // 生物变异系统 tick
                PerformanceMonitor.startTracking("creature_mutation");
                CreatureMutationModule.tick(world);
                PerformanceMonitor.endTracking("creature_mutation");
                
                // 区块扩散处理
                long time = world.getTime();
                if (time % DIFFUSION_INTERVAL == 0) {
                    PerformanceMonitor.startTracking("diffusion");
                    if (USE_OPTIMIZED_DIFFUSION) {
                        OptimizedDiffusion.process(world);
                    } else {
                        DiffusionSystem.processAllDiffusion(world);
                    }
                    PerformanceMonitor.endTracking("diffusion");
                }
                
                // 缓存清理
                if (time % 6000 == 0) {
                    ChunkFactorCache.tickCleanup(time);
                }
                
                // 性能监控记录
                if (ENABLE_PERFORMANCE_MONITORING) {
                    long tickTime = System.nanoTime() - tickStart;
                    PerformanceMonitor.recordTick(tickTime);
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