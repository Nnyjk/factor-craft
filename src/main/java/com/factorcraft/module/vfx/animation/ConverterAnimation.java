package com.factorcraft.module.vfx.animation;

/**
 * 转换器动画
 * 模拟物质转换效果
 */
public class ConverterAnimation {
    private final MachineAnimator inputFlow;      // 输入流
    private final MachineAnimator transformCycle; // 转换循环
    private final MachineAnimator outputFlow;     // 输出流
    private final MachineAnimator glowIntensity;  // 发光强度
    
    private int transformTimer;
    private int transformDuration;
    
    public ConverterAnimation() {
        this.inputFlow = new MachineAnimator();
        this.transformCycle = new MachineAnimator();
        this.outputFlow = new MachineAnimator();
        this.glowIntensity = new MachineAnimator();
        
        this.inputFlow.setAnimationSpeed(0.1f);
        this.transformCycle.setAnimationSpeed(0.05f);
        this.outputFlow.setAnimationSpeed(0.1f);
        this.glowIntensity.setAnimationSpeed(0.15f);
        
        this.transformTimer = 0;
        this.transformDuration = 100;
    }
    
    /**
     * 更新动画
     */
    public void tick(float delta) {
        inputFlow.tick(delta);
        transformCycle.tick(delta);
        outputFlow.tick(delta);
        glowIntensity.tick(delta);
        
        if (transformTimer > 0) {
            transformTimer--;
            float progress = 1.0f - (float)transformTimer / transformDuration;
            
            inputFlow.setTargetProgress(Math.max(0, 1.0f - progress * 1.5f));
            transformCycle.setTargetProgress(progress);
            
            if (progress < 0.5f) {
                glowIntensity.setTargetProgress(progress * 2);
            } else {
                glowIntensity.setTargetProgress((1.0f - progress) * 2);
            }
            
            if (progress > 0.7f) {
                outputFlow.setTargetProgress((progress - 0.7f) / 0.3f);
            }
        } else {
            inputFlow.setTargetProgress(0);
            transformCycle.setTargetProgress(0);
            outputFlow.setTargetProgress(Math.max(0, outputFlow.getProgress() - 0.05f));
            glowIntensity.setTargetProgress(Math.max(0, glowIntensity.getProgress() - 0.1f));
        }
    }
    
    /**
     * 开始转换
     */
    public void startTransform(int duration) {
        this.transformDuration = duration;
        this.transformTimer = duration;
        inputFlow.setTargetProgress(1.0f);
    }
    
    /**
     * 获取输入流强度
     */
    public float getInputFlowProgress(float tickDelta) {
        return inputFlow.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取转换循环进度
     */
    public float getTransformCycleProgress(float tickDelta) {
        return transformCycle.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取输出流强度
     */
    public float getOutputFlowProgress(float tickDelta) {
        return outputFlow.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取发光强度
     */
    public float getGlowIntensity(float tickDelta) {
        return glowIntensity.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取转换进度（0-1）
     */
    public float getTransformProgress() {
        if (transformDuration <= 0) return 0;
        return 1.0f - (float)transformTimer / transformDuration;
    }
    
    /**
     * 获取旋转角度（用于螺旋效果）
     */
    public float getRotationAngle(long time) {
        return (float)time * 0.1f;
    }
    
    /**
     * 是否正在转换
     */
    public boolean isTransforming() {
        return transformTimer > 0;
    }
}
