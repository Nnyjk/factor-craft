package com.factorcraft.client;

import com.factorcraft.client.animation.*;
import com.factorcraft.module.guide.GuideSystem;
import com.factorcraft.module.technology.machine.ModMachines;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

/**
 * 客户端初始化
 */
public class FactorCraftClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // 初始化引导系统
        GuideSystem.initialize();
        
        // 注册机器渲染器
        registerBlockEntityRenderers();
    }
    
    private void registerBlockEntityRenderers() {
        // 提取器核心渲染器
        BlockEntityRendererRegistry.register(ModMachines.EXTRACTOR_CORE, ExtractorCoreRenderer::new);
        
        // 合成器核心渲染器
        BlockEntityRendererRegistry.register(ModMachines.SYNTHESIZER_CORE, SynthesizerCoreRenderer::new);
        
        // 培育器核心渲染器
        BlockEntityRendererRegistry.register(ModMachines.CULTIVATOR_CORE, CultivatorCoreRenderer::new);
        
        // 传递器渲染器
        BlockEntityRendererRegistry.register(ModMachines.TRANSMITTER, TransmitterCoreRenderer::new);
    }
}