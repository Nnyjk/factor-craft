package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * 消耗核心 GUI 界面
 * 
 * 显示 Factor 产出、消耗进度、物品槽
 */
public class ConsumerCoreScreen extends HandledScreen<ConsumerCoreScreenHandler> {
    
    private static final Identifier BACKGROUND = Identifier.of("factorcraft", "textures/gui/consumer_core.png");
    private static final Identifier SLOT_TEXTURE = Identifier.of("minecraft", "textures/gui/container/slot.png");
    
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    
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
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        drawPanel(context);
        drawStructureInfo(context);
        drawFactorStorage(context);
        drawConsumeProgress(context);
        drawSlotIndicators(context);
        
        super.render(context, mouseX, mouseY, delta);
        
        // 渲染提示
        drawTooltips(context, mouseX, mouseY);
    }
    
    private void drawPanel(DrawContext context) {
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        int borderColor = getTierColor(handler.getTier());
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, borderColor);
    }
    
    private void drawStructureInfo(DrawContext context) {
        int statusY = y + 18;
        
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
    }
    
    private void drawFactorStorage(DrawContext context) {
        int storageY = y + 35;
        
        // Factor 存储条（垂直显示在右侧）
        int barX = x + 150;
        int barY = storageY;
        int barWidth = 16;
        int barHeight = 40;
        
        double percentage = handler.getStoragePercentage();
        int fillColor = getBarColor(percentage);
        
        // 背景
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        
        // 填充（从下往上）
        int fillHeight = (int) (barHeight * percentage / 100.0);
        context.fill(barX, barY + barHeight - fillHeight, barX + barWidth, barY + barHeight, fillColor);
        
        // 边框
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        // 标签
        Text label = Text.literal("F");
        context.drawCenteredTextWithShadow(this.textRenderer, label, barX + barWidth / 2, barY - 10, 0xAAAAAA);
        
        // 数值
        Text valueText = Text.literal(String.format("%.0f", handler.getFactorStorage()));
        context.drawCenteredTextWithShadow(this.textRenderer, valueText, barX + barWidth / 2, barY + barHeight + 4, 0xFFFFFF);
    }
    
    private void drawConsumeProgress(DrawContext context) {
        int progressY = y + 58;
        
        if (handler.isConsuming()) {
            // 进度条（在物品槽之间）
            int barX = x + 76;
            int barY = progressY;
            int barWidth = 24;
            int barHeight = 8;
            
            double progress = handler.getConsumeProgressPercentage();
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            int fillWidth = (int) (barWidth * progress / 100.0);
            context.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFFFF8800);
            context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
            
            // 箭头动画
            Text arrow = Text.literal("→");
            context.drawCenteredTextWithShadow(this.textRenderer, arrow, barX + barWidth / 2, barY, 0xFFFFFF);
            
            // 产出信息
            Text outputText = Text.translatable("factorcraft.gui.consumer.output", 
                String.format("%.1f", handler.getFactorToOutput()));
            context.drawTextWithShadow(this.textRenderer, outputText, x + 10, progressY + 15, 0x55FF55);
        } else {
            Text idleText = Text.translatable("factorcraft.gui.consumer.idle");
            context.drawTextWithShadow(this.textRenderer, idleText, x + 50, progressY, 0x888888);
        }
    }
    
    private void drawSlotIndicators(DrawContext context) {
        // 输入槽标签
        Text inputLabel = Text.translatable("factorcraft.gui.slot.input");
        context.drawCenteredTextWithShadow(this.textRenderer, inputLabel, x + 56, y + 55, 0xAAAAAA);
        
        // 输出槽标签
        Text outputLabel = Text.translatable("factorcraft.gui.slot.output");
        context.drawCenteredTextWithShadow(this.textRenderer, outputLabel, x + 116, y + 55, 0xAAAAAA);
    }
    
    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        // Factor 存储提示
        int barX = x + 150;
        int barY = y + 35;
        int barWidth = 16;
        int barHeight = 40;
        
        if (mouseX >= barX && mouseX < barX + barWidth && 
            mouseY >= barY && mouseY < barY + barHeight) {
            java.util.List<Text> tooltip = new java.util.ArrayList<>();
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.storage"));
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.current", 
                String.format("%.2f", handler.getFactorStorage())));
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.max", 
                String.format("%.0f", handler.getMaxStorage())));
            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
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
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 不使用纹理背景
    }
}