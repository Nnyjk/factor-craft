package com.factorcraft.module.vfx.animation;

/**
 * 提取器动画
 * 模拟机械臂的伸缩和旋转动作
 */
public class ExtractorAnimation {
    private final MachineAnimator armExtend;      // 机械臂伸缩
    private final MachineAnimator armRotate;      // 机械臂旋转
    private final MachineAnimator drillSpin;      // 钻头旋转
    
    public ExtractorAnimation() {
        this.armExtend = new MachineAnimator();
        this.armRotate = new MachineAnimator();
        this.drillSpin = new MachineAnimator();
        
        // 设置动画速度
        this.armExtend.setAnimationSpeed(0.15f);
        this.armRotate.setAnimationSpeed(0.1f);
        this.drillSpin.setAnimationSpeed(0.3f);
    }
    
    /**
     * 更新动画
     */
    public void tick(float delta) {
        armExtend.tick(delta);
        armRotate.tick(delta);
        drillSpin.tick(delta);
    }
    
    /**
     * 开始工作动画
     */
    public void startWorking() {
        armExtend.setTargetProgress(1.0f);  // 伸出
        drillSpin.setTargetProgress(1.0f);   // 开始旋转
    }
    
    /**
     * 停止工作
     */
    public void stopWorking() {
        armExtend.setTargetProgress(0.0f);  // 收回
        drillSpin.setTargetProgress(0.0f);   // 停止旋转
    }
    
    /**
     * 获取机械臂伸缩进度（0-1）
     */
    public float getArmExtendProgress(float tickDelta) {
        return armExtend.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取机械臂旋转角度（弧度）
     */
    public float getArmRotateAngle(float tickDelta) {
        float progress = armRotate.getRenderProgress(tickDelta);
        return progress * (float)Math.PI * 2;
    }
    
    /**
     * 获取钻头旋转角度（弧度）
     */
    public float getDrillSpinAngle(float tickDelta) {
        float progress = drillSpin.getRenderProgress(tickDelta);
        return progress * (float)Math.PI * 10;  // 快速旋转
    }
    
    /**
     * 获取钻头缩放（模拟震动效果）
     */
    public float getDrillScale(float tickDelta, long time) {
        float baseScale = 1.0f;
        float vibration = (float)Math.sin(time * 0.5) * 0.05f;
        return baseScale + vibration * drillSpin.getRenderProgress(tickDelta);
    }
}
