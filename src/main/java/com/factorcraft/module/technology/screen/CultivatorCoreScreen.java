package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 培育核心 GUI 界面
 * 
 * 显示特性注入进度、槽位信息
 * 使用 GuiRenderHelper 实现视觉效果
 */
public class CultivatorCoreScreen extends HandledScreen<CultivatorCoreScreenHandler> {
    
    private static final int WIDTH = 200;
    private static final int HEIGHT = 160;
    
    // 动画管理器
    private final GuiAnimationManager animManager = GuiAnimationManager.getInstance();
    private final String machineId;
    
    // 缓存的动画值
    private double animatedProgress = 0;
    private double animatedBuffer = 0;
    
    public CultivatorCoreScreen(CultivatorCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
        this.machineId = "cultivator_" + handler.hashCode();
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
        // 更新动画
        updateAnimations();
        
        renderBackground(context, mouseX, mouseY, delta);
        
        drawPanel(context);
        drawStructureInfo(context);
        drawTraitSlots(context, mouseX, mouseY);
        drawInfusionProgress(context);
        drawStatusIndicator(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void updateAnimations() {
        animatedProgress = animManager.animateProgress(machineId, handler.getInfusionProgress() / 100.0);
        animatedBuffer = animManager.animateFactorStorage(machineId, handler.getBufferPercentage() / 100.0);
    }
    
    private void drawPanel(DrawContext context) {
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        int borderColor = getTierColor(handler.getTier());
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, borderColor);
    }
    
    private void drawStructureInfo(DrawContext context) {
        int statusY = y + 20;
        
        // 使用 GuiRenderHelper 绘制状态
        boolean valid = handler.isStructureValid();
        GuiRenderHelper.MachineStatus status = valid 
            ? GuiRenderHelper.MachineStatus.COMPLETE 
            : GuiRenderHelper.MachineStatus.ERROR;
        GuiRenderHelper.drawStatusIndicator(context, x + 10, statusY, status);
        
        Text statusText = valid 
            ? Text.translatable("factorcraft.gui.structure.valid")
            : Text.translatable("factorcraft.gui.structure.invalid");
        int statusColor = valid ? 0x55FF55 : 0xFF5555;
        context.drawTextWithShadow(this.textRenderer, statusText, x + 24, statusY, statusColor);
        
        // Tier 徽章
        int badgeX = x + WIDTH - 50;
        int tierColor = getTierColor(handler.getTier());
        context.fill(badgeX, statusY - 2, badgeX + 40, statusY + 12, tierColor);
        Text tierText = Text.translatable("factorcraft.gui.tier", handler.getTier());
        context.drawCenteredTextWithShadow(this.textRenderer, tierText, badgeX + 20, statusY, 0xFFFFFF);
        
        // 结构名称
        if (valid) {
            Text structName = Text.literal(handler.getStructureName());
            context.drawTextWithShadow(this.textRenderer, structName, x + 24, statusY + 15, 0xAAAAAA);
        }
    }
    
    private void drawTraitSlots(DrawContext context, int mouseX, int mouseY) {
        int slotsY = y + 50;
        
        Text slotsLabel = Text.translatable("factorcraft.gui.cultivator.trait_slots", handler.getTraitSlots());
        context.drawTextWithShadow(this.textRenderer, slotsLabel, x + 10, slotsY, 0xAAAAAA);
        
        // 特性槽位图标
        int slotSize = 20;
        int startX = x + 10;
        
        for (int i = 0; i < handler.getTraitSlots(); i++) {
            int slotX = startX + i * (slotSize + 4);
            int slotY = slotsY + 15;
            
            boolean hovered = mouseX >= slotX && mouseX < slotX + slotSize &&
                              mouseY >= slotY && mouseY < slotY + slotSize;
            boolean hasTrait = handler.hasTraitInSlot(i);
            
            // 使用 GuiRenderHelper 绘制按钮状态
            GuiRenderHelper.ButtonState btnState = hasTrait 
                ? GuiRenderHelper.ButtonState.PRESSED 
                : hovered 
                    ? GuiRenderHelper.ButtonState.HOVERED 
                    : GuiRenderHelper.ButtonState.NORMAL;
            
            // 槽背景
            context.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 
                hasTrait ? 0xFF446644 : 0xFF333333);
            
            // 边框
            int borderColor = hasTrait ? 0xFF55FF55 : hovered ? 0xFF888888 : 0xFF666666;
            context.drawBorder(slotX, slotY, slotSize, slotSize, borderColor);
            
            // 槽位编号或特性图标
            if (hasTrait) {
                Text traitIcon = Text.literal("◆");
                context.drawCenteredTextWithShadow(this.textRenderer, traitIcon, 
                    slotX + slotSize / 2, slotY + 6, 0x55FF55);
            } else {
                Text slotNum = Text.literal(String.valueOf(i + 1));
                context.drawCenteredTextWithShadow(this.textRenderer, slotNum, 
                    slotX + slotSize / 2, slotY + 6, 0x888888);
            }
        }
    }
    
    private void drawInfusionProgress(DrawContext context) {
        int progressY = y + 95;
        
        // Factor 缓冲区
        GuiRenderHelper.drawFactorStorage(
            context, 
            x + 10, progressY + 12, 
            WIDTH - 20, 12,
            handler.getFactorBuffer(), 
            handler.getMaxBuffer(),
            "Factor 缓冲"
        );
        
        // 注入进度
        if (handler.isInfusing()) {
            int barY = progressY + 30;
            
            Text progressLabel = Text.translatable("factorcraft.gui.cultivator.infusing");
            context.drawTextWithShadow(this.textRenderer, progressLabel, x + 10, barY, 0x55FFFF);
            
            // 使用 GuiRenderHelper 渲染进度条
            GuiRenderHelper.drawLabeledProgressBar(
                context, 
                x + 10, barY + 12, 
                WIDTH - 20, 8, 
                animatedProgress,
                null,
                true
            );
        } else {
            Text readyText = Text.translatable("factorcraft.gui.cultivator.ready");
            context.drawTextWithShadow(this.textRenderer, readyText, x + 10, progressY + 35, 0x55FFFF);
        }
    }
    
    private void drawStatusIndicator(DrawContext context) {
        // 确定机器状态
        GuiRenderHelper.MachineStatus status;
        String detail = "";
        
        if (!handler.isStructureValid()) {
            status = GuiRenderHelper.MachineStatus.ERROR;
            detail = "结构不完整";
        } else if (handler.isInfusing()) {
            status = GuiRenderHelper.MachineStatus.WORKING;
            detail = String.format("注入中: %.0f%%", animatedProgress * 100);
        } else if (handler.getBufferPercentage() < 10) {
            status = GuiRenderHelper.MachineStatus.WARNING;
            detail = "Factor 不足";
        } else {
            status = GuiRenderHelper.MachineStatus.IDLE;
            detail = "等待操作";
        }
        
        // 渲染状态指示器
        GuiRenderHelper.drawStatusText(context, x + 10, y + HEIGHT - 25, status, detail);
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