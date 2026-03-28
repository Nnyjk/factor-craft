package com.factorcraft.module.vfx.animation;

import net.minecraft.util.math.MathHelper;

/**
 * 机器动画管理器
 * 用于管理机器的工作动画状态
 */
public class MachineAnimator {
    private float animationProgress;
    private float targetProgress;
    private float animationSpeed;
    private boolean isAnimating;
    
    public MachineAnimator() {
        this.animationProgress = 0.0f;
        this.targetProgress = 0.0f;
        this.animationSpeed = 0.1f;
        this.isAnimating = false;
    }
    
    /**
     * 更新动画状态
     * @param delta 帧间隔时间
     */
    public void tick(float delta) {
        if (isAnimating) {
            // 平滑过渡到目标进度
            float diff = targetProgress - animationProgress;
            animationProgress += diff * animationSpeed * delta;
            
            // 检查是否完成
            if (Math.abs(diff) < 0.01f) {
                animationProgress = targetProgress;
                isAnimating = false;
            }
        }
    }
    
    /**
     * 设置目标进度
     */
    public void setTargetProgress(float progress) {
        this.targetProgress = MathHelper.clamp(progress, 0.0f, 1.0f);
        this.isAnimating = true;
    }
    
    /**
     * 设置动画速度
     */
    public void setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
    }
    
    /**
     * 获取当前进度
     */
    public float getProgress() {
        return animationProgress;
    }
    
    /**
     * 获取渲染用的插值进度
     */
    public float getRenderProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, animationProgress, targetProgress);
    }
    
    /**
     * 重置动画
     */
    public void reset() {
        this.animationProgress = 0.0f;
        this.targetProgress = 0.0f;
        this.isAnimating = false;
    }
    
    /**
     * 是否正在动画中
     */
    public boolean isAnimating() {
        return isAnimating;
    }
}
