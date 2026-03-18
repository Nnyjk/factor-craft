package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 消耗核心 GUI 界面
 * 
 * 显示 Factor 产出、消耗进度、物品槽
 * 使用 GuiRenderHelper 实现视觉效果
 */
public class ConsumerCoreScreen extends HandledScreen<ConsumerCoreScreenHandler> {
    
    private static final Identifier BACKGROUND = Identifier.of("factorcraft", "textures/gui/consumer_core.png");
    private static final Identifier SLOT_TEXTURE = Identifier.of("minecraft", "textures/gui/container/slot.png");
    
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    
    // 动画管理器
    private final GuiAnimationManager animManager = GuiAnimationManager.getInstance();
    private final String machineId;
    
    // 缓存的动画值
    private double animatedProgress = 0;
    private double animatedStorage = 0;
    
    public ConsumerCoreScreen(ConsumerCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
        this.machineId = "consumer_" + handler.hashCode();
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.titleX = 10;
        this.titleY = 6;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 更新动画
        updateAnimations();
        
        renderBackground(context, mouseX, mouseY, delta);
        
        drawPanel(context);
        drawStructureInfo(context);
        drawFactorStorage(context);
        drawConsumeProgress(context);
        drawSlotIndicators(context);
        drawStatusIndicator(context);
        
        super.render(context, mouseX, mouseY, delta);
        
        // 渲染提示
        drawTooltips(context, mouseX, mouseY);
    }
    
    private void updateAnimations() {
        animatedProgress = animManager.animateProgress(machineId, handler.getConsumeProgressPercentage() / 100.0);
        animatedStorage = animManager.animateFactorStorage(machineId, handler.getStoragePercentage() / 100.0);
    }
    
    private void drawPanel(DrawContext context) {
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        int borderColor = getTierColor(handler.getTier());
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, borderColor);
    }
    
    private void drawStructureInfo(DrawContext context) {
        int statusY = y + 18;
        
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
    }
    
    private void drawFactorStorage(DrawContext context) {
        int storageY = y + 35;
        
        // 使用 GuiRenderHelper 渲染 Factor 存储（垂直）
        GuiRenderHelper.drawFactorStorage(
            context, 
            x + 150, storageY, 
            16, 40,
            handler.getFactorStorage(), 
            handler.getMaxStorage(),
            null
        );
        
        // 标签
        Text label = Text.literal("F");
        context.drawCenteredTextWithShadow(this.textRenderer, label, x + 158, storageY - 12, 0xAAAAAA);
    }
    
    private void drawConsumeProgress(DrawContext context) {
        int progressY = y + 58;
        
        if (handler.isConsuming()) {
            // 使用 GuiRenderHelper 渲染进度条
            GuiRenderHelper.drawProgressBar(
                context, 
                x + 76, progressY, 
                24, 8, 
                animatedProgress, 
                true
            );
            
            // 箭头动画
            float animPhase = (GuiRenderHelper.getAnimTime() * 0.1f) % 1.0f;
            int arrowOffset = (int) (animPhase * 8);
            Text arrow = Text.literal("→");
            context.drawCenteredTextWithShadow(this.textRenderer, arrow, x + 88 + arrowOffset, progressY, 0xFFFFFF);
            
            // 产出信息
            Text outputText = Text.translatable("factorcraft.gui.consumer.output", 
                String.format("%.1f", handler.getFactorToOutput()));
            context.drawTextWithShadow(this.textRenderer, outputText, x + 10, progressY + 15, 0x55FF55);
            
            // 效率指示器
            double efficiency = handler.getEfficiency();
            Text effText = Text.translatable("factorcraft.gui.efficiency", 
                String.format("%.0f%%", efficiency * 100));
            context.drawTextWithShadow(this.textRenderer, effText, x + 10, progressY + 28, 0xAAAAAA);
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
        
        // 槽位高亮（如果需要）
        // 输入槽边框
        context.drawBorder(x + 38, y + 67, 18, 18, 0xFF666666);
        // 输出槽边框
        context.drawBorder(x + 98, y + 67, 18, 18, 0xFF666666);
    }
    
    private void drawStatusIndicator(DrawContext context) {
        // 确定机器状态
        GuiRenderHelper.MachineStatus status;
        String detail = "";
        
        if (!handler.isStructureValid()) {
            status = GuiRenderHelper.MachineStatus.ERROR;
            detail = "结构不完整";
        } else if (handler.isConsuming()) {
            status = GuiRenderHelper.MachineStatus.WORKING;
            double progress = handler.getConsumeProgressPercentage();
            detail = String.format("消耗中: %.0f%%", progress);
        } else if (handler.getStoragePercentage() >= 95) {
            status = GuiRenderHelper.MachineStatus.WARNING;
            detail = "Factor 存储即将满";
        } else {
            status = GuiRenderHelper.MachineStatus.IDLE;
            detail = "等待物品";
        }
        
        // 渲染状态指示器
        GuiRenderHelper.drawStatusText(context, x + 10, y + HEIGHT - 25, status, detail);
    }
    
    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        // Factor 存储提示
        int barX = x + 150;
        int barY = y + 35;
        int barWidth = 16;
        int barHeight = 40;
        
        if (mouseX >= barX && mouseX < barX + barWidth && 
            mouseY >= barY && mouseY < barY + barHeight) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.storage"));
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.current", 
                String.format("%.2f", handler.getFactorStorage())));
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.max", 
                String.format("%.0f", handler.getMaxStorage())));
            tooltip.add(Text.translatable("factorcraft.tooltip.factor.percentage", 
                String.format("%.1f%%", handler.getStoragePercentage())));
            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }
        
        // 进度条提示
        if (handler.isConsuming()) {
            int progressX = x + 76;
            int progressY = y + 58;
            if (mouseX >= progressX && mouseX < progressX + 24 && 
                mouseY >= progressY && mouseY < progressY + 8) {
                List<Text> tooltip = new ArrayList<>();
                tooltip.add(Text.translatable("factorcraft.tooltip.progress"));
                tooltip.add(Text.literal(String.format("%.1f%%", animatedProgress * 100)));
                context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
            }
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
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 不使用纹理背景
    }
}