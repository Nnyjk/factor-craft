package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 培育核心 GUI 界面
 * 
 * 显示特性注入进度、槽位信息
 */
public class CultivatorCoreScreen extends HandledScreen<CultivatorCoreScreenHandler> {
    
    private static final int WIDTH = 200;
    private static final int HEIGHT = 160;
    
    public CultivatorCoreScreen(CultivatorCoreScreenHandler handler, PlayerInventory inventory, Text title) {
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
        drawTraitSlots(context);
        drawInfusionProgress(context);
        
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
        
        Text statusText = Text.translatable("factorcraft.gui.structure.valid");
        context.drawTextWithShadow(this.textRenderer, statusText, x + 10, statusY, 0x55FF55);
        
        // Tier 徽章
        int badgeX = x + WIDTH - 50;
        int tierColor = getTierColor(handler.getTier());
        context.fill(badgeX, statusY - 2, badgeX + 40, statusY + 12, tierColor);
        Text tierText = Text.translatable("factorcraft.gui.tier", handler.getTier());
        context.drawCenteredTextWithShadow(this.textRenderer, tierText, badgeX + 20, statusY, 0xFFFFFF);
        
        // 结构名称
        Text structName = Text.literal(handler.getStructureName());
        context.drawTextWithShadow(this.textRenderer, structName, x + 10, statusY + 15, 0xAAAAAA);
    }
    
    private void drawTraitSlots(DrawContext context) {
        int slotsY = y + 50;
        
        Text slotsLabel = Text.translatable("factorcraft.gui.cultivator.trait_slots", handler.getTraitSlots());
        context.drawTextWithShadow(this.textRenderer, slotsLabel, x + 10, slotsY, 0xAAAAAA);
        
        // 特性槽位图标
        int slotSize = 20;
        int startX = x + 10;
        for (int i = 0; i < handler.getTraitSlots(); i++) {
            int slotX = startX + i * (slotSize + 4);
            
            // 空槽背景
            context.fill(slotX, slotsY + 15, slotX + slotSize, slotsY + 15 + slotSize, 0xFF333333);
            context.drawBorder(slotX, slotsY + 15, slotSize, slotSize, 0xFF666666);
            
            // 槽位编号
            Text slotNum = Text.literal(String.valueOf(i + 1));
            context.drawCenteredTextWithShadow(this.textRenderer, slotNum, slotX + slotSize / 2, slotsY + 21, 0x888888);
        }
    }
    
    private void drawInfusionProgress(DrawContext context) {
        int progressY = y + 100;
        
        // Factor 缓冲区
        Text bufferLabel = Text.translatable("factorcraft.gui.cultivator.buffer");
        context.drawTextWithShadow(this.textRenderer, bufferLabel, x + 10, progressY, 0xAAAAAA);
        
        // 进度信息（简化版，待完善）
        Text progressText = Text.translatable("factorcraft.gui.cultivator.ready");
        context.drawTextWithShadow(this.textRenderer, progressText, x + 10, progressY + 15, 0x55FFFF);
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
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {}
}