package com.factorcraft.module.vfx;

import com.factorcraft.FactorCraftMod;
import com.factorcraft.module.FactorCraftModule;
import com.factorcraft.module.vfx.particle.FactorParticleTypes;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * VFX 视觉效果模块
 * 
 * 功能：
 * - Factor 元素类型系统
 * - 手持效果渲染
 * - 使用效果渲染
 * - 高浓度区域效果
 * - 过载特效
 */
public final class VfxModule implements FactorCraftModule {
    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static VfxModule instance;
    
    public static VfxModule getInstance() {
        if (instance == null) {
            instance = new VfxModule();
        }
        return instance;
    }
    
    @Override
    public String moduleId() {
        return "vfx";
    }
    
    @Override
    public void initialize() {
        if (REGISTERED.compareAndSet(false, true)) {
            // 注册粒子类型
            FactorParticleTypes.register();
            
            // 服务端 tick 处理
            ServerTickEvents.END_WORLD_TICK.register(world -> {
                FactorVisualEffectManager.getInstance().tickServer(world);
            });
            
            FactorCraftMod.LOGGER.info(
                "[FactorCraft:VFX] 视觉效果系统已启用 " +
                "(5 种元素类型/手持效果/区域效果/过载特效)");
        }
    }
    
    /**
     * 客户端初始化
     */
    public void initializeClient() {
        // 客户端 tick 处理
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.player != null) {
                FactorVisualEffectManager.getInstance().tickClient(client.world, client.player);
            }
        });
    }
    
    @Override
    public void shutdown() {
        // 清理资源
    }
}