package com.factorcraft.client.animation;

import com.factorcraft.module.technology.machine.MachineBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * 机器 BlockEntity 渲染器基类
 * 
 * 提供通用动画功能：
 * - 旋转动画
 * - 浮动动画
 * - 脉冲光效
 */
public abstract class MachineBlockEntityRenderer<T extends MachineBlockEntity> implements BlockEntityRenderer<T> {
    
    protected final BlockEntityRendererFactory.Context context;
    
    public MachineBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }
    
    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                      int light, int overlay) {
        if (!MachineAnimationConfig.ENABLED) {
            return;
        }
        
        // 检查渲染距离
        double distance = context.getEntityRenderDispatcher().getSquaredDistanceToCamera(
            entity.getPos().getX() + 0.5,
            entity.getPos().getY() + 0.5,
            entity.getPos().getZ() + 0.5
        );
        
        if (!MachineAnimationConfig.shouldRender(Math.sqrt(distance))) {
            return;
        }
        
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        
        // 应用动画
        applyAnimations(entity, tickDelta, matrices);
        
        // 渲染模型
        renderModel(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        
        matrices.pop();
    }
    
    /**
     * 应用动画效果
     */
    protected abstract void applyAnimations(T entity, float tickDelta, MatrixStack matrices);
    
    /**
     * 渲染模型
     */
    protected abstract void renderModel(T entity, float tickDelta, MatrixStack matrices, 
                                       VertexConsumerProvider vertexConsumers, int light, int overlay);
    
    /**
     * 旋转动画
     */
    protected void applyRotation(MatrixStack matrices, float tickDelta, long time, float speed) {
        float angle = (time % 3600) / 3600.0f * 360.0f * speed;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
    }
    
    /**
     * 浮动动画
     */
    protected void applyFloating(MatrixStack matrices, float tickDelta, long time, float amplitude, float speed) {
        float offset = MathHelper.sin((time % 2000) / 2000.0f * (float) Math.PI * 2.0f * speed) * amplitude;
        matrices.translate(0, offset, 0);
    }
    
    /**
     * 脉冲缩放动画
     */
    protected void applyPulseScale(MatrixStack matrices, float tickDelta, long time, float minScale, float maxScale, float speed) {
        float scale = minScale + (MathHelper.sin((time % 1000) / 1000.0f * (float) Math.PI * 2.0f * speed) + 1) / 2.0f * (maxScale - minScale);
        matrices.scale(scale, scale, scale);
    }
    
    /**
     * 获取机器工作时间（tick）
     */
    protected long getMachineTime(MachineBlockEntity entity) {
        return entity.getWorld() != null ? entity.getWorld().getTime() : 0;
    }
}
