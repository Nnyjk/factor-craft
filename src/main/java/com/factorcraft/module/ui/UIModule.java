package com.factorcraft.module.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * UIModule - 通用 UI 系统
 * 
 * 提供通用 UI 组件渲染工具
 */
public class UIModule {
    
    private static UIModule instance;
    
    public UIModule() {
        instance = this;
    }
    
    public void initialize() {
        // UI 组件已注册为静态工具方法
    }
    
    /**
     * 渲染能量条
     * 
     * @param context 绘制上下文
     * @param x 左上角 X 坐标
     * @param y 左上角 Y 坐标
     * @param width 宽度
     * @param height 高度
     * @param current 当前值
     * @param max 最大值
     * @param color 颜色 (ARGB)
     */
    public static void renderEnergyBar(DrawContext context, int x, int y, int width, int height, 
                                       float current, float max, int color) {
        float percent = Math.max(0.0f, Math.min(1.0f, current / max));
        int filledWidth = (int) (width * percent);
        
        // 背景
        context.fill(x, y, x + width, y + height, 0xFF303030);
        // 填充
        context.fill(x, y, x + filledWidth, y + height, color);
        // 边框
        context.drawHorizontalLine(x, x + width, y, 0xFFFFFFFF);
        context.drawHorizontalLine(x, x + width, y + height, 0xFFFFFFFF);
        context.drawVerticalLine(x, y, y + height, 0xFFFFFFFF);
        context.drawVerticalLine(x + width, y, y + height, 0xFFFFFFFF);
    }
    
    /**
     * 渲染进度条
     * 
     * @param context 绘制上下文
     * @param x 左上角 X 坐标
     * @param y 左上角 Y 坐标
     * @param width 宽度
     * @param height 高度
     * @param progress 进度 (0.0-1.0)
     */
    public static void renderProgressBar(DrawContext context, int x, int y, int width, int height, 
                                       float progress) {
        renderEnergyBar(context, x, y, width, height, progress, 1.0f, 0xFF00AA00);
    }
    
    /**
     * 渲染 Factor 能量条 (带维度颜色)
     * 
     * @param context 绘制上下文
     * @param x 左上角 X 坐标
     * @param y 左上角 Y 坐标
     * @param current 当前 Factor 值
     * @param max 最大 Factor 值
     * @param dimensionType 维度类型 (overworld/nether/end)
     */
    public static void renderFactorBar(DrawContext context, int x, int y, float current, float max, 
                                       String dimensionType) {
        int color = switch (dimensionType) {
            case "nether" -> 0xFFAA0000;
            case "end" -> 0xFFAA00AA;
            default -> 0xFF00AA00;
        };
        renderEnergyBar(context, x, y, 60, 8, current, max, color);
    }
    
    public static UIModule getInstance() {
        return instance;
    }
}
