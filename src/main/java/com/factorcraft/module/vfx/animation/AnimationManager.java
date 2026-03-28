package com.factorcraft.module.vfx.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 动画管理器
 * 管理所有机器的动画实例
 */
public class AnimationManager {
    private static AnimationManager instance;
    
    private final Map<UUID, ExtractorAnimation> extractorAnimations;
    private final Map<UUID, SynthesizerAnimation> synthesizerAnimations;
    private final Map<UUID, ConverterAnimation> converterAnimations;
    
    private AnimationManager() {
        this.extractorAnimations = new HashMap<>();
        this.synthesizerAnimations = new HashMap<>();
        this.converterAnimations = new HashMap<>();
    }
    
    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }
    
    /**
     * 更新所有动画
     */
    public void tick(float delta) {
        extractorAnimations.values().forEach(anim -> anim.tick(delta));
        synthesizerAnimations.values().forEach(anim -> anim.tick(delta));
        converterAnimations.values().forEach(anim -> anim.tick(delta));
    }
    
    /**
     * 获取或创建提取器动画
     */
    public ExtractorAnimation getExtractorAnimation(UUID pos) {
        return extractorAnimations.computeIfAbsent(pos, k -> new ExtractorAnimation());
    }
    
    /**
     * 获取或创建合成器动画
     */
    public SynthesizerAnimation getSynthesizerAnimation(UUID pos) {
        return synthesizerAnimations.computeIfAbsent(pos, k -> new SynthesizerAnimation());
    }
    
    /**
     * 获取或创建转换器动画
     */
    public ConverterAnimation getConverterAnimation(UUID pos) {
        return converterAnimations.computeIfAbsent(pos, k -> new ConverterAnimation());
    }
    
    /**
     * 移除提取器动画
     */
    public void removeExtractorAnimation(UUID pos) {
        extractorAnimations.remove(pos);
    }
    
    /**
     * 移除合成器动画
     */
    public void removeSynthesizerAnimation(UUID pos) {
        synthesizerAnimations.remove(pos);
    }
    
    /**
     * 移除转换器动画
     */
    public void removeConverterAnimation(UUID pos) {
        converterAnimations.remove(pos);
    }
    
    /**
     * 清除所有动画
     */
    public void clear() {
        extractorAnimations.clear();
        synthesizerAnimations.clear();
        converterAnimations.clear();
    }
}
