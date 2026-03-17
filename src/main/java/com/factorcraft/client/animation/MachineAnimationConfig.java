package com.factorcraft.client.animation;

/**
 * 机器动画配置
 * 
 * 控制动画性能和行为
 */
public class MachineAnimationConfig {
    
    /** 是否启用动画 */
    public static boolean ENABLED = true;
    
    /** 动画渲染距离（方块） */
    public static int RENDER_DISTANCE = 64;
    
    /** 动画最大更新频率（tick） */
    public static int MAX_UPDATE_FREQUENCY = 1;
    
    /** 是否显示调试信息 */
    public static boolean DEBUG = false;
    
    /**
     * 检查是否应该渲染动画
     */
    public static boolean shouldRender(double distance) {
        return ENABLED && distance <= RENDER_DISTANCE;
    }
}
