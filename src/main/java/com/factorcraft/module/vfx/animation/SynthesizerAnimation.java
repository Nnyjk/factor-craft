package com.factorcraft.module.vfx.animation;

/**
 * 合成器动画
 * 模拟能量汇聚和闪光效果
 */
public class SynthesizerAnimation {
    private final MachineAnimator energyGather;   // 能量汇聚
    private final MachineAnimator flashIntensity; // 闪光强度
    private final MachineAnimator ringExpand;     // 能量环扩张
    
    private int craftTimer;
    private int craftDuration;
    
    public SynthesizerAnimation() {
        this.energyGather = new MachineAnimator();
        this.flashIntensity = new MachineAnimator();
        this.ringExpand = new MachineAnimator();
        
        this.energyGather.setAnimationSpeed(0.08f);
        this.flashIntensity.setAnimationSpeed(0.2f);
        this.ringExpand.setAnimationSpeed(0.1f);
        
        this.craftTimer = 0;
        this.craftDuration = 200;
    }
    
    /**
     * 更新动画
     */
    public void tick(float delta) {
        energyGather.tick(delta);
        flashIntensity.tick(delta);
        ringExpand.tick(delta);
        
        if (craftTimer > 0) {
            craftTimer--;
            
            float progress = 1.0f - (float)craftTimer / craftDuration;
            energyGather.setTargetProgress(progress);
            
            if (progress > 0.8f) {
                flashIntensity.setTargetProgress((progress - 0.8f) * 5.0f);
            }
            
            if (craftTimer == 0) {
                flashIntensity.setTargetProgress(1.0f);
                ringExpand.setTargetProgress(1.0f);
            }
        } else {
            if (energyGather.getProgress() > 0) {
                energyGather.setTargetProgress(Math.max(0, energyGather.getProgress() - 0.02f));
            }
            if (flashIntensity.getProgress() > 0) {
                flashIntensity.setTargetProgress(Math.max(0, flashIntensity.getProgress() - 0.1f));
            }
            if (ringExpand.getProgress() > 0) {
                ringExpand.setTargetProgress(Math.max(0, ringExpand.getProgress() - 0.05f));
            }
        }
    }
    
    /**
     * 开始合成
     */
    public void startCrafting(int duration) {
        this.craftDuration = duration;
        this.craftTimer = duration;
        energyGather.setTargetProgress(0.3f);
    }
    
    /**
     * 获取能量汇聚进度
     */
    public float getEnergyGatherProgress(float tickDelta) {
        return energyGather.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取闪光强度
     */
    public float getFlashIntensity(float tickDelta) {
        return flashIntensity.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取能量环扩张进度
     */
    public float getRingExpandProgress(float tickDelta) {
        return ringExpand.getRenderProgress(tickDelta);
    }
    
    /**
     * 获取合成进度（0-1）
     */
    public float getCraftProgress() {
        if (craftDuration <= 0) return 0;
        return 1.0f - (float)craftTimer / craftDuration;
    }
    
    /**
     * 是否正在合成
     */
    public boolean isCrafting() {
        return craftTimer > 0;
    }
}
