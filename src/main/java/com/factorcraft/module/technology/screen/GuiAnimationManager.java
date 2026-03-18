package com.factorcraft.module.technology.screen;

import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * GUI 动画管理器
 * 
 * 提供：
 * - 平滑过渡动画
 * - 缓动函数
 * - 动画状态管理
 */
public final class GuiAnimationManager {
    private static final GuiAnimationManager INSTANCE = new GuiAnimationManager();
    
    // 动画状态存储
    private final Map<String, AnimationState> animations = new HashMap<>();
    
    // 缓动函数
    public enum Easing {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        BOUNCE,
        ELASTIC
    }
    
    /**
     * 动画状态
     */
    public static class AnimationState {
        public double startValue;
        public double endValue;
        public double currentValue;
        public long startTime;
        public long duration;
        public Easing easing;
        public boolean complete;
        
        public AnimationState(double start, double end, long duration, Easing easing) {
            this.startValue = start;
            this.endValue = end;
            this.currentValue = start;
            this.duration = duration;
            this.easing = easing;
            this.startTime = System.currentTimeMillis();
            this.complete = false;
        }
        
        public void update() {
            long elapsed = System.currentTimeMillis() - startTime;
            double t = MathHelper.clamp((double) elapsed / duration, 0.0, 1.0);
            
            currentValue = applyEasing(startValue, endValue, t, easing);
            
            if (t >= 1.0) {
                complete = true;
                currentValue = endValue;
            }
        }
        
        private double applyEasing(double start, double end, double t, Easing easing) {
            double delta = end - start;
            
            return switch (easing) {
                case LINEAR -> start + delta * t;
                case EASE_IN -> start + delta * t * t;
                case EASE_OUT -> start + delta * (1 - (1 - t) * (1 - t));
                case EASE_IN_OUT -> {
                    double t2 = t * 2;
                    yield start + delta * (t2 < 1 ? 0.5 * t2 * t2 : 0.5 * (1 - (1 - t2 + 2) * (1 - t2)));
                }
                case BOUNCE -> {
                    if (t < 1 / 2.75) {
                        yield start + delta * (7.5625 * t * t);
                    } else if (t < 2 / 2.75) {
                        double t2 = t - 1.5 / 2.75;
                        yield start + delta * (7.5625 * t2 * t2 + 0.75);
                    } else if (t < 2.5 / 2.75) {
                        double t2 = t - 2.25 / 2.75;
                        yield start + delta * (7.5625 * t2 * t2 + 0.9375);
                    } else {
                        double t2 = t - 2.625 / 2.75;
                        yield start + delta * (7.5625 * t2 * t2 + 0.984375);
                    }
                }
                case ELASTIC -> {
                    if (t == 0) yield start;
                    if (t == 1) yield end;
                    double p = 0.3;
                    double s = p / 4;
                    yield start + delta * (Math.pow(2, -10 * t) * Math.sin((t - s) * (2 * Math.PI) / p) + 1);
                }
            };
        }
    }
    
    private GuiAnimationManager() {}
    
    public static GuiAnimationManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * 开始或更新动画
     */
    public void animate(String key, double startValue, double endValue, 
                        long durationMs, Easing easing) {
        AnimationState existing = animations.get(key);
        
        if (existing != null && !existing.complete) {
            // 继续现有动画，更新目标值
            existing.startValue = existing.currentValue;
            existing.endValue = endValue;
            existing.startTime = System.currentTimeMillis();
            existing.duration = durationMs;
            existing.complete = false;
        } else {
            // 创建新动画
            animations.put(key, new AnimationState(startValue, endValue, durationMs, easing));
        }
    }
    
    /**
     * 平滑过渡到目标值
     */
    public void smoothTransition(String key, double targetValue, long durationMs) {
        AnimationState existing = animations.get(key);
        double currentValue = existing != null ? existing.currentValue : targetValue;
        
        animate(key, currentValue, targetValue, durationMs, Easing.EASE_OUT);
    }
    
    /**
     * 获取当前动画值
     */
    public double getValue(String key, double defaultValue) {
        AnimationState state = animations.get(key);
        if (state == null) return defaultValue;
        
        state.update();
        return state.currentValue;
    }
    
    /**
     * 检查动画是否完成
     */
    public boolean isComplete(String key) {
        AnimationState state = animations.get(key);
        return state == null || state.complete;
    }
    
    /**
     * 清理已完成的动画
     */
    public void cleanup() {
        animations.entrySet().removeIf(entry -> entry.getValue().complete);
    }
    
    /**
     * 清除所有动画
     */
    public void clear() {
        animations.clear();
    }
    
    // ==================== 预定义动画 ====================
    
    /**
     * 进度条平滑动画
     */
    public double animateProgress(String machineId, double targetProgress) {
        String key = "progress_" + machineId;
        smoothTransition(key, targetProgress, 300);
        return getValue(key, targetProgress);
    }
    
    /**
     * Factor 存储动画
     */
    public double animateFactorStorage(String machineId, double targetPercentage) {
        String key = "factor_" + machineId;
        smoothTransition(key, targetPercentage, 500);
        return getValue(key, targetPercentage);
    }
    
    /**
     * 效率指示器动画
     */
    public double animateEfficiency(String machineId, double targetEfficiency) {
        String key = "efficiency_" + machineId;
        smoothTransition(key, targetEfficiency, 400);
        return getValue(key, targetEfficiency);
    }
    
    /**
     * 按钮点击反馈动画
     */
    public double animateButtonPress(String buttonId) {
        String key = "button_" + buttonId;
        AnimationState state = animations.get(key);
        
        if (state == null || state.complete) {
            // 开始新动画
            animate(key, 1.0, 0.0, 150, Easing.EASE_OUT);
        }
        
        return getValue(key, 0.0);
    }
    
    /**
     * 状态变化动画
     */
    public double animateStatusChange(String machineId, int fromStatus, int toStatus) {
        String key = "status_" + machineId;
        animate(key, fromStatus, toStatus, 200, Easing.EASE_IN_OUT);
        return getValue(key, toStatus);
    }
    
    /**
     * 警告闪烁动画
     */
    public double getWarningFlash(String machineId) {
        String key = "warning_" + machineId;
        AnimationState state = animations.get(key);
        
        if (state == null) {
            // 创建循环闪烁
            animate(key, 0.0, 1.0, 500, Easing.EASE_IN_OUT);
        } else if (state.complete) {
            // 反向动画
            animate(key, state.endValue, state.startValue, 500, Easing.EASE_IN_OUT);
        }
        
        return getValue(key, 0.5);
    }
}