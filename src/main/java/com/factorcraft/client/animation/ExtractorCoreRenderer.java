package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.ExtractorCoreBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

/**
 * 提取器核心渲染器
 * 
 * 动画效果：
 * - 核心方块旋转（工作时加速）
 * - 能量脉冲光效
 */
public class ExtractorCoreRenderer extends MachineBlockEntityRenderer<ExtractorCoreBlockEntity> {
    
    public ExtractorCoreRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
    
    @Override
    protected void applyAnimations(ExtractorCoreBlockEntity entity, float tickDelta, MatrixStack matrices) {
        long time = getMachineTime(entity);
        
        // 始终缓慢旋转
        applyRotation(matrices, tickDelta, time, 0.01f);
        
        // 始终添加浮动效果
        applyFloating(matrices, tickDelta, time, 0.03f, 0.015f);
    }
    
    @Override
    protected void renderModel(ExtractorCoreBlockEntity entity, float tickDelta, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // 简化实现 - 后续使用真正的模型
    }
}
