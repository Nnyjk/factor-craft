package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 消耗核心 GUI 界面
 * 
 * 显示 Factor 产出、消耗进度
 */
public class ConsumerCoreScreen extends HandledScreen<ConsumerCoreScreenHandler> {
    
    private static final int WIDTH = 200;
    private static final int HEIGHT = 160;
    
    public ConsumerCoreScreen(ConsumerCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.titleX = 10;
        this.titleY = 6;
        
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.close"),
            button -> this.close()
        ).dimensions(x + WIDTH - 60, y + HEIGHT - 20, 50, 16).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        drawPanel(context);
        drawStructureInfo(context);
        drawFactorStorage(context);
        drawConsumeProgress(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawPanel(DrawContext context) {
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        int borderColor = getTierColor(handler.getTier());
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, borderColor);
    }
    
    private void drawStructureInfo(DrawContext context) {
        int statusY = y + 20;
        
        boolean valid = handler.isStructureValid();
        Text statusText = valid 
            ? Text.translatable("factorcraft.gui.structure.valid")
            : Text.translatable("factorcraft.gui.structure.invalid");
        int statusColor = valid ? 0x55FF55 : 0xFF5555;
        
        context.drawTextWithShadow(this.textRenderer, statusText, x + 10, statusY, statusColor);
        
        // Tier 徽章
        int badgeX = x + WIDTH - 50;
        int tierColor = getTierColor(handler.getTier());
        context.fill(badgeX, statusY - 2, badgeX + 40, statusY + 12, tierColor);
        Text tierText = Text.translatable("factorcraft.gui.tier", handler.getTier());
        context.drawCenteredTextWithShadow(this.textRenderer, tierText, badgeX + 20, statusY, 0xFFFFFF);
        
        // 结构名称
        if (valid) {
            Text structName = Text.literal(handler.getStructureName());
            context.drawTextWithShadow(this.textRenderer, structName, x + 10, statusY + 15, 0xAAAAAA);
        }
    }
    
    private void drawFactorStorage(DrawContext context) {
        int storageY = y + 50;
        
        Text label = Text.translatable("factorcraft.gui.factor.storage");
        context.drawTextWithShadow(this.textRenderer, label, x + 10, storageY, 0xAAAAAA);
        
        int barX = x + 10;
        int barY = storageY + 12;
        int barWidth = WIDTH - 20;
        int barHeight = 18;
        
        double percentage = handler.getStoragePercentage();
        int fillColor = getBarColor(percentage);
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        int fillWidth = (int) (barWidth * percentage / 100.0);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, fillColor);
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        Text valueText = Text.literal(String.format("%.0f / %.0f", 
            handler.getFactorStorage(), handler.getMaxStorage()));
        context.drawCenteredTextWithShadow(this.textRenderer, valueText, barX + barWidth / 2, barY + 5, 0xFFFFFF);
    }
    
    private void drawConsumeProgress(DrawContext context) {
        int progressY = y + 95;
        
        if (handler.isConsuming()) {
            Text recipeLabel = Text.translatable("factorcraft.gui.consumer.consuming");
            context.drawTextWithShadow(this.textRenderer, recipeLabel, x + 10, progressY, 0xFFAA00);
            
            // 进度条
            int barX = x + 10;
            int barY = progressY + 12;
            int barWidth = WIDTH - 20;
            int barHeight = 12;
            
            double progress = handler.getConsumeProgressPercentage();
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            int fillWidth = (int) (barWidth * progress / 100.0);
            context.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFFFF8800);
            context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
            
            Text progressText = Text.literal(String.format("%.0f%%", progress));
            context.drawCenteredTextWithShadow(this.textRenderer, progressText, barX + barWidth / 2, barY + 2, 0xFFFFFF);
            
            // 产出信息
            Text outputText = Text.translatable("factorcraft.gui.consumer.output", 
                String.format("%.1f", handler.getFactorToOutput()));
            context.drawTextWithShadow(this.textRenderer, outputText, x + 10, progressY + 28, 0x55FF55);
        } else {
            Text idleText = Text.translatable("factorcraft.gui.consumer.idle");
            context.drawTextWithShadow(this.textRenderer, idleText, x + 10, progressY, 0x888888);
        }
    }
    
    private int getTierColor(int tier) {
        return switch (tier) {
            case 1 -> 0xFF8B4513;
            case 2 -> 0xFF708090;
            case 3 -> 0xFF4169E1;
            case 4 -> 0xFF9932CC;
            case 5 -> 0xFFFFD700;
            default -> 0xFF666666;
        };
    }
    
    private int getBarColor(double percentage) {
        if (percentage >= 90) return 0xFF00FF00;
        if (percentage >= 70) return 0xFF88FF00;
        if (percentage >= 50) return 0xFFFFFF00;
        if (percentage >= 30) return 0xFFFFAA00;
        return 0xFFFF5500;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {}
}