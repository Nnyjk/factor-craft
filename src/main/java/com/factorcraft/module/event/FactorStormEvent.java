package com.factorcraft.module.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/**
 * Factor 风暴事件
 * 
 * 指定区域 Factor 浓度临时提升 50%
 */
public class FactorStormEvent extends BaseEvent {
    private final double originalConcentration;
    private double currentConcentration;
    
    public FactorStormEvent(Identifier worldKey, int duration) {
        super(EventType.FACTOR_STORM, duration, worldKey);
        this.originalConcentration = 0.0;
        this.currentConcentration = 0.0;
    }
    
    @Override
    public void onStart(MinecraftServer server, ServerWorld world) {
        super.onStart(server, world);
        // 保存原始浓度并提升
        // 实际浓度修改由 TideEffectManager 处理
        this.currentConcentration = 0.8; // 提升到高能水平
    }
    
    @Override
    public void onTick(MinecraftServer server, ServerWorld world, int remainingTicks) {
        // 维持高浓度状态
        // 可以在这里添加粒子效果等视觉反馈
    }
    
    @Override
    public void onEnd(MinecraftServer server, ServerWorld world) {
        // 恢复原始浓度
        this.currentConcentration = originalConcentration;
        super.onEnd(server, world);
    }
    
    public double getCurrentConcentration() {
        return currentConcentration;
    }
}
