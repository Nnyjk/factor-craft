package com.factorcraft.client.animation;

/**
 * 机器动画配置
 * 
 * 定义动画系统的配置参数
 */
public class MachineAnimationConfig {
    
    /** 是否启用动画效果 */
    public static boolean ENABLED = true;
    
    /** 动画速度倍率 */
    public static float ANIMATION_SPEED = 1.0f;
    
    /** 是否启用粒子效果 */
    public static boolean PARTICLES_ENABLED = true;
    
    /** 粒子密度（0.0-1.0） */
    public static float PARTICLE_DENSITY = 1.0f;
    
    /** 最大渲染距离（方块） */
    public static int MAX_RENDER_DISTANCE = 64;
}
