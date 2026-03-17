package com.factorcraft.module.ui.screen;

import com.factorcraft.module.ui.handler.CultivationScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 培育核心 GUI 屏幕
 */
public class CultivationScreen extends HandledScreen<CultivationScreenHandler> {
    
    private static final int BG_COLOR = 0xCC222222;
    private static final int PROGRESS_COLOR = 0xFF44AA44;
    private static final int PROGRESS_BG_COLOR = 0xFF333333;
    
    public CultivationScreen(CultivationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        
        // 背景
        context.fill(x, y, x + this.backgroundWidth, y + this.backgroundHeight, BG_COLOR);
        context.drawBorder(x, y, this.backgroundWidth, this.backgroundHeight, 0xFF555555);
        
        // 标题
        context.drawText(this.textRenderer, this.title, x + 8, y + 8, 0xFFFFFF, false);
        
        // 进度条背景
        int barX = x + 50;
        int barY = y + 60;
        int barWidth = 76;
        int barHeight = 14;
        context.fill(barX, barY, barX + barWidth, barY + barHeight, PROGRESS_BG_COLOR);
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        // 进度条填充
        int progress = getProgressPixels(barWidth);
        if (progress > 0) {
            context.fill(barX, barY, barX + progress, barY + barHeight, PROGRESS_COLOR);
        }
        
        // 进度百分比文字
        int progressPercent = getProgressPercent();
        String progressText = progressPercent + "%";
        int textWidth = this.textRenderer.getWidth(progressText);
        context.drawText(this.textRenderer, progressText, x + (this.backgroundWidth - textWidth) / 2, y + 80, 0xFFFFFF, false);
        
        // 物品槽标签
        context.drawText(this.textRenderer, "目标物品", x + 8, y + 35, 0xAAAAAA, false);
    }
    
    /**
     * 获取进度像素
     */
    private int getProgressPixels(int maxWidth) {
        int progress = handler.getProgress();
        int maxProgress = handler.getMaxProgress();
        
        if (maxProgress <= 0) return 0;
        
        return (int) ((float) progress / maxProgress * maxWidth);
    }
    
    /**
     * 获取进度百分比
     */
    private int getProgressPercent() {
        int progress = handler.getProgress();
        int maxProgress = handler.getMaxProgress();
        
        if (maxProgress <= 0) return 0;
        
        return (int) ((float) progress / maxProgress * 100);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 不需要，已在 drawBackground 中绘制
    }
}
