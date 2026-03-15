package com.factorcraft.module.technology.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * Factor HUD 显示
 * 
 * 在游戏界面上显示 Factor 状态
 * 可通过配置调整位置和显示内容
 */
public class FactorHudDisplay {
    
    private final MinecraftClient client;
    
    // 显示位置
    private int x = 10;
    private int y = 10;
    
    // 数据
    private double currentFactor;
    private double maxFactor;
    private int currentTier;
    private String statusText;
    
    // 显示开关
    private boolean visible = true;
    private boolean showBar = true;
    private boolean showTier = true;
    private boolean showText = true;
    
    public FactorHudDisplay(MinecraftClient client) {
        this.client = client;
    }
    
    /**
     * 更新显示数据
     */
    public void update(double factor, double max, int tier, String status) {
        this.currentFactor = factor;
        this.maxFactor = max;
        this.currentTier = tier;
        this.statusText = status;
    }
    
    /**
     * 渲染 HUD
     */
    public void render(DrawContext context) {
        if (!visible || client.player == null || client.options.hudHidden) {
            return;
        }
        
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        
        int currentY = y;
        
        // Tier 显示
        if (showTier) {
            String tierText = "T" + currentTier;
            int tierColor = getTierColor(currentTier);
            
            // 背景
            int textWidth = client.textRenderer.getWidth(tierText);
            context.fill(x - 2, currentY - 2, x + textWidth + 6, currentY + 12, 0x66000000);
            context.drawTextWithShadow(client.textRenderer, tierText, x, currentY, tierColor);
            
            currentY += 16;
        }
        
        // Factor 条
        if (showBar && maxFactor > 0) {
            int barWidth = 100;
            int barHeight = 10;
            
            // 背景
            context.fill(x, currentY, x + barWidth, currentY + barHeight, 0x66000000);
            
            // 填充
            double percent = currentFactor / maxFactor;
            int fillWidth = (int) (barWidth * percent);
            int fillColor = getFactorBarColor(percent);
            context.fill(x, currentY, x + fillWidth, currentY + barHeight, fillColor);
            
            // 边框
            context.drawBorder(x, currentY, barWidth, barHeight, 0xFF666666);
            
            // 数值
            String valueText = String.format("%.0f", currentFactor);
            context.drawTextWithShadow(client.textRenderer, valueText, x + barWidth + 5, currentY + 1, 0xFFFFFF);
            
            currentY += 15;
        }
        
        // 状态文本
        if (showText && statusText != null && !statusText.isEmpty()) {
            context.drawTextWithShadow(client.textRenderer, statusText, x, currentY, 0xAAAAAA);
        }
        
        matrices.pop();
    }
    
    // ==================== 辅助方法 ====================
    
    private int getTierColor(int tier) {
        return switch (tier) {
            case 1 -> 0xFF8B4513; // 棕色
            case 2 -> 0xFF708090; // 灰色
            case 3 -> 0xFF4169E1; // 蓝色
            case 4 -> 0xFF9932CC; // 紫色
            case 5 -> 0xFFFFD700; // 金色
            default -> 0xFF666666;
        };
    }
    
    private int getFactorBarColor(double percent) {
        if (percent >= 0.9) return 0xFF00FF00;
        if (percent >= 0.7) return 0xFF88FF00;
        if (percent >= 0.5) return 0xFFFFFF00;
        if (percent >= 0.3) return 0xFFFFAA00;
        return 0xFFFF5500;
    }
    
    // ==================== Getter/Setter ====================
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setShowBar(boolean show) {
        this.showBar = show;
    }
    
    public void setShowTier(boolean show) {
        this.showTier = show;
    }
    
    public void setShowText(boolean show) {
        this.showText = show;
    }
    
    /**
     * 在屏幕角落显示简单的 Factor 数值
     */
    public static class Compact {
        
        public static void renderCompact(DrawContext context, MinecraftClient client, 
                                         double factor, int tier) {
            if (client.player == null || client.options.hudHidden) {
                return;
            }
            
            int x = 10;
            int y = 10;
            
            // 简洁显示
            String text = String.format("T%d: %.0f F", tier, factor);
            int tierColor = switch (tier) {
                case 1 -> 0xFF8B4513;
                case 2 -> 0xFF708090;
                case 3 -> 0xFF4169E1;
                case 4 -> 0xFF9932CC;
                case 5 -> 0xFFFFD700;
                default -> 0xFF666666;
            };
            
            // 小背景
            int width = client.textRenderer.getWidth(text);
            context.fill(x - 2, y - 2, x + width + 4, y + 10, 0x66000000);
            context.drawTextWithShadow(client.textRenderer, text, x, y, tierColor);
        }
    }
    
    /**
     * 详细显示面板（用于屏幕右下角）
     */
    public static class Detailed {
        
        public static void renderDetailed(DrawContext context, MinecraftClient client,
                                          double factor, double max, int tier,
                                          String dimension, double efficiency) {
            if (client.player == null || client.options.hudHidden) {
                return;
            }
            
            int width = 120;
            int height = 60;
            int x = client.getWindow().getScaledWidth() - width - 10;
            int y = 10;
            
            // 背景
            context.fill(x, y, x + width, y + height, 0x88000000);
            context.drawBorder(x, y, width, height, 0xFF444444);
            
            // 标题
            String title = "Factor 状态";
            context.drawCenteredTextWithShadow(client.textRenderer, title, x + width / 2, y + 3, 0xFFFFFF);
            
            // Factor 值
            String factorText = String.format("%.0f / %.0f", factor, max);
            context.drawTextWithShadow(client.textRenderer, factorText, x + 5, y + 18, 0xAAAAAA);
            
            // 进度条
            int barY = y + 32;
            int barWidth = width - 10;
            double percent = max > 0 ? factor / max : 0;
            context.fill(x + 5, barY, x + 5 + barWidth, barY + 6, 0xFF333333);
            context.fill(x + 5, barY, x + 5 + (int) (barWidth * percent), barY + 6, 0xFF00AA00);
            
            // 维度和效率
            String dimText = dimension.replace("minecraft:", "");
            String effText = String.format("%.0f%%", efficiency * 100);
            context.drawTextWithShadow(client.textRenderer, dimText, x + 5, y + 42, 0x888888);
            // 效率文本
            int effWidth = client.textRenderer.getWidth(effText);
            context.drawTextWithShadow(client.textRenderer, effText, x + width - 5 - effWidth, y + 42, 
                efficiency >= 1.0 ? 0x00FF00 : 0xFFAA00);
        }
    }
}