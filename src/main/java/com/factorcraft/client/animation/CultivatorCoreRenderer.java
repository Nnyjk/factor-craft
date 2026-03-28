package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.CultivatorCoreBlockEntity;
import com.factorcraft.module.vfx.animation.AnimationManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

/**
 * 培育器核心渲染器
 * 
 * 使用动画系统渲染培育器工作效果
 */
public class CultivatorCoreRenderer implements BlockEntityRenderer<CultivatorCoreBlockEntity> {
    
    public CultivatorCoreRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    
    @Override
    public void render(CultivatorCoreBlockEntity entity, float tickDelta, MatrixStack matrices, 
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // 获取位置唯一的动画 ID
        BlockPos pos = entity.getPos();
        UUID animId = UUID.nameUUIDFromBytes(Long.toString(pos.asLong()).getBytes());
        
        // 获取动画实例（使用 ConverterAnimation 作为基础）
        var animation = AnimationManager.getInstance().getConverterAnimation(animId);
        
        // 如果正在注入特性，应用动画
        if (entity.isInfusing()) {
            animation.tick(tickDelta);
            
            matrices.push();
            
            // 应用能量流效果（上下浮动）
            float inputFlow = animation.getInputFlowProgress(tickDelta);
            float floatOffset = (float)Math.sin(inputFlow * Math.PI * 2) * 0.2f;
            matrices.translate(0, floatOffset, 0);
            
            // 应用转换循环旋转效果
            float transformCycle = animation.getTransformCycleProgress(tickDelta);
            float rotation = transformCycle * 360.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            
            matrices.pop();
        }
    }
}
