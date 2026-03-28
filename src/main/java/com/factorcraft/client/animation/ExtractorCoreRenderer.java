package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.ExtractorCoreBlockEntity;
import com.factorcraft.module.vfx.animation.AnimationManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.UUID;

/**
 * 提取器核心渲染器
 * 
 * 使用动画系统渲染提取器工作效果
 */
public class ExtractorCoreRenderer implements BlockEntityRenderer<ExtractorCoreBlockEntity> {
    
    public ExtractorCoreRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    
    @Override
    public void render(ExtractorCoreBlockEntity entity, float tickDelta, MatrixStack matrices, 
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // 获取位置唯一的动画 ID
        BlockPos pos = entity.getPos();
        UUID animId = UUID.nameUUIDFromBytes(Long.toString(pos.asLong()).getBytes());
        
        // 获取动画实例
        var animation = AnimationManager.getInstance().getExtractorAnimation(animId);
        
        // 如果正在提取，应用动画
        if (entity.isExtracting()) {
            animation.tick(tickDelta);
            
            matrices.push();
            
            // 应用机械臂伸缩动画
            float armExtension = animation.getArmExtendProgress(tickDelta);
            matrices.translate(0, armExtension, 0);
            
            // 应用钻头旋转动画
            float drillRotation = animation.getDrillSpinAngle(tickDelta);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(drillRotation));
            
            matrices.pop();
        }
    }
}
