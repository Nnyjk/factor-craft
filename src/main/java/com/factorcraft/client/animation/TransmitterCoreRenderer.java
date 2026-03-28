package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.TransmitterBlockEntity;
import com.factorcraft.module.vfx.animation.AnimationManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

/**
 * 传递器核心渲染器
 * 
 * 使用动画系统渲染传递器传输效果
 */
public class TransmitterCoreRenderer implements BlockEntityRenderer<TransmitterBlockEntity> {
    
    public TransmitterCoreRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    
    @Override
    public void render(TransmitterBlockEntity entity, float tickDelta, MatrixStack matrices, 
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // 获取位置唯一的动画 ID
        BlockPos pos = entity.getPos();
        UUID animId = UUID.nameUUIDFromBytes(Long.toString(pos.asLong()).getBytes());
        
        // 获取动画实例（使用 ConverterAnimation 作为基础）
        var animation = AnimationManager.getInstance().getConverterAnimation(animId);
        
        // 如果正在传输，应用动画（cooldownRemaining > 0 表示正在冷却/传输中）
        if (entity.getCooldownRemaining() > 0) {
            animation.tick(tickDelta);
            
            matrices.push();
            
            // 应用能量环旋转效果
            float transformCycle = animation.getTransformCycleProgress(tickDelta);
            float rotation = transformCycle * 360.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            
            // 应用发光强度缩放效果
            float glowIntensity = animation.getGlowIntensity(tickDelta);
            float scale = 0.8f + glowIntensity * 0.4f; // 0.8 ~ 1.2
            matrices.scale(scale, scale, scale);
            
            matrices.pop();
        }
    }
}
