package com.factorcraft.module.ui.screen;

import com.factorcraft.module.ui.handler.CultivationScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CultivationScreen extends HandledScreen<CultivationScreenHandler> {
    private static final int BG_COLOR = 0xCC222222;
    
    public CultivationScreen(CultivationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        
        context.fill(x, y, x + this.backgroundWidth, y + this.backgroundHeight, BG_COLOR);
        context.drawBorder(x, y, this.backgroundWidth, this.backgroundHeight, 0xFF555555);
        
        // 进度条
        drawProgressBar(context, x, y);
    }
    
    private void drawProgressBar(DrawContext context, int x, int y) {
        // 简化的进度条
        int barX = x + 50;
        int barY = y + 50;
        int barWidth = 76;
        int barHeight = 10;
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0x333333);
        
        // 假设进度为 50%
        int progress = barWidth / 2;
        context.fill(barX, barY, barX + progress, barY + barHeight, 0x55FF55);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0xFFFFFF, false);
    }
}