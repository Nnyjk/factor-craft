package com.factorcraft.module.technology.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

/**
 * GUI 渲染工具类
 * 
 * 提供统一的视觉效果渲染方法：
 * - 进度条动画
 * - 能量/Factor 存储
 * - 状态指示器
 * - 按钮视觉反馈
 */
public final class GuiRenderHelper {
    
    // 颜色常量
    public static final int COLOR_SUCCESS = 0xFF55FF55;
    public static final int COLOR_WARNING = 0xFFFFFF00;
    public static final int COLOR_ERROR = 0xFFFF5555;
    public static final int COLOR_INFO = 0xFF55FFFF;
    public static final int COLOR_IDLE = 0xFF666666;
    public static final int COLOR_WORKING = 0xFF00AA00;
    public static final int COLOR_COMPLETE = 0xFF00FF00;
    
    // 进度条颜色渐变
    private static final int[] PROGRESS_COLORS = {
        0xFF00AA00,  // 0-20%: 深绿
        0xFF00CC00,  // 20-40%: 绿色
        0xFF00EE00,  // 40-60%: 亮绿
        0xFF00FF00,  // 60-80%: 明绿
        0xFF55FF55   // 80-100%: 亮绿
    };
    
    // 动画状态
    private static long tickCounter = 0;
    
    /**
     * 更新动画状态（每帧调用）
     */
    public static void tick() {
        tickCounter++;
    }
    
    /**
     * 获取动画时间
     */
    public static float getAnimTime() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.world != null ? client.world.getTime() : 0;
    }
    
    // ==================== 进度条渲染 ====================
    
    /**
     * 渲染进度条（带动画）
     * 
     * @param context 绘制上下文
     * @param x 左上角 X
     * @param y 左上角 Y
     * @param width 宽度
     * @param height 高度
     * @param progress 进度 (0.0 - 1.0)
     * @param animated 是否启用动画
     */
    public static void drawProgressBar(DrawContext context, int x, int y, 
                                        int width, int height, 
                                        double progress, boolean animated) {
        double clampedProgress = MathHelper.clamp(progress, 0.0, 1.0);
        
        // 背景
        context.fill(x, y, x + width, y + height, 0xFF333333);
        
        // 填充
        int fillWidth = (int) (width * clampedProgress);
        if (fillWidth > 0) {
            int color = getProgressColor(clampedProgress);
            
            if (animated) {
                // 动画效果：闪烁边缘
                float animPhase = (getAnimTime() * 0.1f) % 1.0f;
                if (clampedProgress > 0.95f) {
                    // 完成时闪烁
                    float flash = (float) Math.sin(animPhase * Math.PI * 4) * 0.3f + 0.7f;
                    color = lerpColor(0xFF00FF00, 0xFFAAFFAA, flash);
                } else {
                    // 工作中：移动渐变
                    int glowX = (int) (fillWidth * animPhase);
                    drawGradientBar(context, x, y, fillWidth, height, glowX);
                    return;
                }
            }
            
            context.fill(x, y, x + fillWidth, y + height, color);
        }
        
        // 边框
        context.drawBorder(x, y, width, height, 0xFF666666);
    }
    
    /**
     * 渲染渐变进度条
     */
    private static void drawGradientBar(DrawContext context, int x, int y, 
                                         int width, int height, int glowX) {
        // 基础填充
        int baseColor = getProgressColor((double) width / 100.0);
        context.fill(x, y, x + width, y + height, baseColor);
        
        // 高光效果
        int glowStart = Math.max(0, glowX - 10);
        int glowEnd = Math.min(width, glowX + 10);
        if (glowEnd > glowStart) {
            for (int i = glowStart; i < glowEnd; i++) {
                float intensity = 1.0f - Math.abs(i - glowX) / 10.0f;
                int glowColor = lerpColor(baseColor, 0xFFFFFFFF, intensity * 0.3f);
                context.fill(x + i, y, x + i + 1, y + height, glowColor);
            }
        }
    }
    
    /**
     * 渲染带标签的进度条
     */
    public static void drawLabeledProgressBar(DrawContext context, int x, int y,
                                               int width, int height,
                                               double progress, Text label,
                                               boolean showPercentage) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        // 进度条
        drawProgressBar(context, x, y, width, height, progress, true);
        
        // 标签
        if (label != null) {
            context.drawTextWithShadow(textRenderer, label, x + 2, y + height + 3, 0xAAAAAA);
        }
        
        // 百分比
        if (showPercentage) {
            String percentText = String.format("%.0f%%", progress * 100);
            int textWidth = textRenderer.getWidth(percentText);
            context.drawCenteredTextWithShadow(textRenderer, percentText, 
                x + width / 2, y + (height - 8) / 2, 0xFFFFFF);
        }
    }
    
    // ==================== Factor 存储渲染 ====================
    
    /**
     * 渲染 Factor 存储条
     */
    public static void drawFactorStorage(DrawContext context, int x, int y,
                                          int width, int height,
                                          double current, double max,
                                          String label) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        double percentage = max > 0 ? current / max : 0;
        
        // 标签
        if (label != null) {
            context.drawTextWithShadow(textRenderer, label, x, y - 12, 0xAAAAAA);
        }
        
        // 背景（液体槽样式）
        context.fill(x, y, x + width, y + height, 0xFF222222);
        
        // 填充
        int fillHeight = (int) (height * percentage);
        if (fillHeight > 0) {
            int fillColor = getFactorColor(percentage);
            
            // 波动动画
            float waveOffset = (float) Math.sin(getAnimTime() * 0.05) * 2;
            
            // 渐变填充
            for (int i = 0; i < fillHeight; i++) {
                float intensity = 1.0f - (i / (float) height) * 0.3f;
                int pixelColor = lerpColor(fillColor, 0xFF000000, 1 - intensity);
                int pixelY = y + height - i - 1;
                context.fill(x + 1, pixelY, x + width - 1, pixelY + 1, pixelColor);
            }
        }
        
        // 边框（发光效果）
        int borderColor = percentage < 0.2 ? 0xFFFF5500 : 
                          percentage > 0.8 ? 0xFF00FF00 : 0xFF666666;
        context.drawBorder(x, y, width, height, borderColor);
        
        // 数值
        String valueText = String.format("%.0f / %.0f", current, max);
        context.drawCenteredTextWithShadow(textRenderer, valueText, 
            x + width / 2, y + height / 2 - 4, 0xFFFFFF);
    }
    
    /**
     * 渲染 Factor 流量指示器
     */
    public static void drawFlowIndicator(DrawContext context, int x, int y,
                                          double inputRate, double outputRate) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        // 输入（向下箭头）
        context.drawTextWithShadow(textRenderer, "▼", x, y, 
            inputRate > 0 ? COLOR_SUCCESS : COLOR_IDLE);
        String inputText = String.format("%.1f", inputRate);
        context.drawTextWithShadow(textRenderer, inputText, x + 12, y, 0xAAAAAA);
        
        // 输出（向上箭头）
        context.drawTextWithShadow(textRenderer, "▲", x, y + 12, 
            outputRate > 0 ? COLOR_INFO : COLOR_IDLE);
        String outputText = String.format("%.1f", outputRate);
        context.drawTextWithShadow(textRenderer, outputText, x + 12, y + 12, 0xAAAAAA);
        
        // 净流量
        double net = inputRate - outputRate;
        int netColor = net > 0 ? COLOR_SUCCESS : net < 0 ? COLOR_WARNING : COLOR_IDLE;
        String netText = String.format("净: %+.1f", net);
        context.drawTextWithShadow(textRenderer, netText, x, y + 26, netColor);
    }
    
    // ==================== 状态指示器 ====================
    
    /**
     * 渲染状态图标
     */
    public static void drawStatusIndicator(DrawContext context, int x, int y,
                                            MachineStatus status) {
        // 状态图标
        String icon = getStatusIcon(status);
        int color = getStatusColor(status);
        
        // 背景圆
        context.fill(x - 1, y - 1, x + 10, y + 10, 0x80000000);
        
        // 状态字符
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawTextWithShadow(textRenderer, icon, x, y, color);
        
        // 警告/错误闪烁
        if (status == MachineStatus.WARNING || status == MachineStatus.ERROR) {
            float flash = (float) Math.sin(getAnimTime() * 0.2) * 0.5f + 0.5f;
            int flashColor = lerpColor(0x00000000, color, flash);
            context.fill(x - 2, y - 2, x + 11, y + 11, flashColor & 0x40FFFFFF);
        }
    }
    
    /**
     * 渲染状态文本
     */
    public static void drawStatusText(DrawContext context, int x, int y,
                                       MachineStatus status, String detail) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        
        Text statusText = Text.translatable("factorcraft.status." + status.name().toLowerCase());
        int color = getStatusColor(status);
        
        context.drawTextWithShadow(textRenderer, statusText, x, y, color);
        
        if (detail != null && !detail.isEmpty()) {
            context.drawTextWithShadow(textRenderer, detail, x, y + 12, 0x888888);
        }
    }
    
    // ==================== 按钮与控件 ====================
    
    /**
     * 渲染按钮状态
     */
    public static void drawButtonState(DrawContext context, int x, int y,
                                        int width, int height,
                                        ButtonState state, String label) {
        int bgColor = getButtonBgColor(state);
        int borderColor = getButtonBorderColor(state);
        int textColor = getButtonTextColor(state);
        
        // 背景
        context.fill(x, y, x + width, y + height, bgColor);
        
        // 边框
        context.drawBorder(x, y, width, height, borderColor);
        
        // 悬停高亮
        if (state == ButtonState.HOVERED) {
            context.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0x20FFFFFF);
        }
        
        // 文本
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textX = x + (width - textRenderer.getWidth(label)) / 2;
        int textY = y + (height - 8) / 2;
        context.drawTextWithShadow(textRenderer, label, textX, textY, textColor);
    }
    
    /**
     * 渲染工具提示
     */
    public static void drawTooltip(DrawContext context, int x, int y,
                                    List<Text> lines, int mouseX, int mouseY,
                                    int areaX, int areaY, int areaWidth, int areaHeight) {
        if (mouseX >= areaX && mouseX < areaX + areaWidth &&
            mouseY >= areaY && mouseY < areaY + areaHeight) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawTooltip(textRenderer, lines, mouseX, mouseY);
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private static int getProgressColor(double progress) {
        int index = (int) (progress * (PROGRESS_COLORS.length - 1));
        index = MathHelper.clamp(index, 0, PROGRESS_COLORS.length - 1);
        return PROGRESS_COLORS[index];
    }
    
    private static int getFactorColor(double percentage) {
        if (percentage >= 0.9) return 0xFF00FF00;
        if (percentage >= 0.7) return 0xFF88FF00;
        if (percentage >= 0.5) return 0xFFFFFF00;
        if (percentage >= 0.3) return 0xFFFFAA00;
        return 0xFFFF5500;
    }
    
    private static String getStatusIcon(MachineStatus status) {
        return switch (status) {
            case IDLE -> "○";
            case WORKING -> "◉";
            case COMPLETE -> "✓";
            case WARNING -> "⚠";
            case ERROR -> "✗";
            case DISABLED -> "○";
        };
    }
    
    private static int getStatusColor(MachineStatus status) {
        return switch (status) {
            case IDLE -> COLOR_IDLE;
            case WORKING -> COLOR_WORKING;
            case COMPLETE -> COLOR_COMPLETE;
            case WARNING -> COLOR_WARNING;
            case ERROR -> COLOR_ERROR;
            case DISABLED -> 0xFF444444;
        };
    }
    
    private static int getButtonBgColor(ButtonState state) {
        return switch (state) {
            case NORMAL -> 0xFF444444;
            case HOVERED -> 0xFF555555;
            case PRESSED -> 0xFF333333;
            case DISABLED -> 0xFF222222;
        };
    }
    
    private static int getButtonBorderColor(ButtonState state) {
        return switch (state) {
            case NORMAL -> 0xFF666666;
            case HOVERED -> 0xFF888888;
            case PRESSED -> 0xFF00AA00;
            case DISABLED -> 0xFF333333;
        };
    }
    
    private static int getButtonTextColor(ButtonState state) {
        return switch (state) {
            case NORMAL -> 0xFFFFFFFF;
            case HOVERED -> 0xFFFFFFFF;
            case PRESSED -> 0xFFAAFFAA;
            case DISABLED -> 0xFF666666;
        };
    }
    
    /**
     * 颜色插值
     */
    private static int lerpColor(int color1, int color2, float t) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);
        
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
    
    // 枚举
    
    /**
     * 机器状态枚举
     */
    public enum MachineStatus {
        IDLE,       // 空闲
        WORKING,    // 工作中
        COMPLETE,   // 完成
        WARNING,    // 警告
        ERROR,      // 错误
        DISABLED    // 禁用
    }
    
    /**
     * 按钮状态枚举
     */
    public enum ButtonState {
        NORMAL,     // 正常
        HOVERED,    // 悬停
        PRESSED,    // 按下
        DISABLED    // 禁用
    }
}