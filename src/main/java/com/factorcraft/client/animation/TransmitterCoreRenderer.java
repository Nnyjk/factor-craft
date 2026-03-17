package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.TransmitterBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

/**
 * 传递器渲染器
 * 
 * 动画效果：
 * - 核心旋转
 * - 能量束流动动画
 */
public class TransmitterCoreRenderer extends MachineBlockEntityRenderer<TransmitterBlockEntity> {
    
    public TransmitterCoreRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
    
    @Override
    protected void applyAnimations(TransmitterBlockEntity entity, float tickDelta, MatrixStack matrices) {
        long time = getMachineTime(entity);
        
        // 旋转
        applyRotation(matrices, tickDelta, time, 0.025f);
        
        // 浮动
        applyFloating(matrices, tickDelta, time, 0.02f, 0.01f);
    }
    
    @Override
    protected void renderModel(TransmitterBlockEntity entity, float tickDelta, MatrixStack matrices,
                               VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // 简化实现
    }
}
