package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.CultivatorCoreBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

/**
 * 培育器核心渲染器
 * 
 * 动画效果：
 * - 核心旋转
 * - 特性粒子环绕效果
 */
public class CultivatorCoreRenderer extends MachineBlockEntityRenderer<CultivatorCoreBlockEntity> {
    
    public CultivatorCoreRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
    
    @Override
    protected void applyAnimations(CultivatorCoreBlockEntity entity, float tickDelta, MatrixStack matrices) {
        long time = getMachineTime(entity);
        
        // 缓慢旋转
        applyRotation(matrices, tickDelta, time, 0.015f);
        
        // 浮动
        applyFloating(matrices, tickDelta, time, 0.02f, 0.01f);
    }
    
    @Override
    protected void renderModel(CultivatorCoreBlockEntity entity, float tickDelta, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // 简化实现
    }
}
