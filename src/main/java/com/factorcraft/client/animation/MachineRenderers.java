package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

/**
 * 机器渲染器注册
 */
public class MachineRenderers {
    
    /**
     * 注册所有机器渲染器
     */
    public static void register(BlockEntityRendererFactory.Context context) {
        // 提取器核心
        // BlockEntityRendererRegistry.register(ModBlockEntities.EXTRACTOR_CORE, ExtractorCoreRenderer::new);
        
        // 合成器核心
        // BlockEntityRendererRegistry.register(ModBlockEntities.SYNTHESIZER_CORE, SynthesizerCoreRenderer::new);
        
        // 培育器核心
        // BlockEntityRendererRegistry.register(ModBlockEntities.CULTIVATOR_CORE, CultivatorCoreRenderer::new);
        
        // 传递器
        // BlockEntityRendererRegistry.register(ModBlockEntities.TRANSMITTER, TransmitterCoreRenderer::new);
    }
}
