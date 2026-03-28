package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.SynthesizerCoreBlockEntity;
import com.factorcraft.module.vfx.animation.AnimationManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

/**
 * 合成器核心渲染器
 * 
 * 使用动画系统渲染合成器工作效果
 */
public class SynthesizerCoreRenderer implements BlockEntityRenderer<SynthesizerCoreBlockEntity> {
    
    public SynthesizerCoreRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    
    @Override
    public void render(SynthesizerCoreBlockEntity entity, float tickDelta, MatrixStack matrices, 
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // 获取位置唯一的动画 ID
        BlockPos pos = entity.getPos();
        UUID animId = UUID.nameUUIDFromBytes(Long.toString(pos.asLong()).getBytes());
        
        // 获取动画实例
        var animation = AnimationManager.getInstance().getSynthesizerAnimation(animId);
        
        // 如果正在合成，应用动画
        if (entity.isCrafting()) {
            animation.tick(tickDelta);
            
            matrices.push();
            
            // 应用能量汇聚动画（缩放效果）
            float energyGather = animation.getEnergyGatherProgress(tickDelta);
            float scale = 0.5f + energyGather * 0.5f; // 0.5 ~ 1.0
            matrices.scale(scale, scale, scale);
            
            // 应用能量环旋转动画
            float ringExpand = animation.getRingExpandProgress(tickDelta);
            float rotation = ringExpand * 360.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            
            matrices.pop();
        }
    }
}
