package com.factorcraft.module.vfx.particle;

/**
 * Factor 粒子效果配置
 * 
 * 控制粒子系统的性能和行为
 */
public class FactorParticleConfig {
    
    /** 是否启用粒子效果 */
    public static boolean ENABLED = true;
    
    /** 每 tick 最大粒子生成数量 */
    public static int MAX_PARTICLES_PER_TICK = 50;
    
    /** 粒子渲染距离（方块） */
    public static int RENDER_DISTANCE = 32;
    
    /** 粒子密度倍率（0.0-1.0） */
    public static double DENSITY_MULTIPLIER = 1.0;
    
    /** 是否显示高性能模式（减少粒子数量） */
    public static boolean LOW_PERFORMANCE_MODE = false;
    
    /**
     * 检查是否应该生成粒子
     */
    public static boolean shouldSpawn(double distance) {
        if (!ENABLED) return false;
        if (distance > RENDER_DISTANCE) return false;
        return true;
    }
    
    /**
     * 获取实际粒子数量（考虑密度和性能模式）
     */
    public static int getActualCount(int baseCount) {
        if (LOW_PERFORMANCE_MODE) {
            return Math.max(1, baseCount / 4);
        }
        return (int) (baseCount * DENSITY_MULTIPLIER);
    }
    
    /**
     * 设置性能模式
     */
    public static void setLowPerformanceMode(boolean lowPerf) {
        LOW_PERFORMANCE_MODE = lowPerf;
        if (lowPerf) {
            DENSITY_MULTIPLIER = 0.25;
        }
    }
}
