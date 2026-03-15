package com.factorcraft.module.technology.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器状态屏幕 - 通用 UI
 * 
 * 显示：
 * - 当前 Tier
 * - Factor 存储
 * - 工作进度
 * - 维度效率
 * - 结构状态
 */
public class MachineStatusScreen extends Screen {
    
    private static final Identifier BACKGROUND = Identifier.of("factorcraft", "textures/gui/machine_status.png");
    private static final int WIDTH = 200;
    private static final int HEIGHT = 180;
    
    // 状态数据
    private final String machineName;
    private final int tier;
    private final double factorStored;
    private final double factorMax;
    private final double progress;
    private final String currentTask;
    private final double dimensionEfficiency;
    private final boolean structureValid;
    private final String recommendedDimension;
    private final String currentDimension;
    
    // UI 位置
    private int x;
    private int y;
    
    public MachineStatusScreen(MachineStatusData data) {
        super(Text.literal(data.machineName()));
        this.machineName = data.machineName();
        this.tier = data.tier();
        this.factorStored = data.factorStored();
        this.factorMax = data.factorMax();
        this.progress = data.progress();
        this.currentTask = data.currentTask();
        this.dimensionEfficiency = data.dimensionEfficiency();
        this.structureValid = data.structureValid();
        this.recommendedDimension = data.recommendedDimension();
        this.currentDimension = data.currentDimension();
    }
    
    @Override
    protected void init() {
        super.init();
        this.x = (width - WIDTH) / 2;
        this.y = (height - HEIGHT) / 2;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        // 背景
        drawBackground(context);
        
        // 标题
        drawTitle(context);
        
        // Tier 徽章
        drawTierBadge(context);
        
        // Factor 存储条
        drawFactorBar(context);
        
        // 进度条
        drawProgressBar(context);
        
        // 当前任务
        drawCurrentTask(context);
        
        // 维度效率
        drawDimensionInfo(context);
        
        // 结构状态
        drawStructureStatus(context);
        
        // 提示文本
        drawTooltips(context, mouseX, mouseY);
    }
    
    private void drawBackground(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xCC000000);
        
        // 边框
        int borderColor = getTierColor(tier);
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        
        // 顶部渐变
        for (int i = 0; i < 20; i++) {
            int alpha = (int) (80 * (1 - i / 20.0));
            // 简化：直接使用带有 alpha 的颜色
            int color = (alpha << 24) | (borderColor & 0x00FFFFFF);
            context.fill(x + 1, y + 1 + i, x + WIDTH - 1, y + 2 + i, color);
        }
    }
    
    private void drawTitle(DrawContext context) {
        // 机器名称
        context.drawCenteredTextWithShadow(textRenderer, machineName, x + WIDTH / 2, y + 8, 0xFFFFFF);
    }
    
    private void drawTierBadge(DrawContext context) {
        // Tier 徽章背景
        int badgeX = x + WIDTH - 45;
        int badgeY = y + 6;
        int badgeWidth = 38;
        int badgeHeight = 14;
        
        int tierColor = getTierColor(tier);
        context.fill(badgeX, badgeY, badgeX + badgeWidth, badgeY + badgeHeight, tierColor);
        context.drawBorder(badgeX, badgeY, badgeWidth, badgeHeight, 0xFF000000);
        
        // Tier 文本
        String tierText = "T" + tier;
        context.drawCenteredTextWithShadow(textRenderer, tierText, badgeX + badgeWidth / 2, badgeY + 3, 0xFFFFFF);
    }
    
    private void drawFactorBar(DrawContext context) {
        int barX = x + 10;
        int barY = y + 28;
        int barWidth = WIDTH - 20;
        int barHeight = 18;
        
        // 标签
        context.drawTextWithShadow(textRenderer, "Factor 存储", barX, barY - 10, 0xAAAAAA);
        
        // 背景
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        
        // 填充
        double fillPercent = factorMax > 0 ? factorStored / factorMax : 0;
        int fillWidth = (int) (barWidth * fillPercent);
        int fillColor = getFactorBarColor(fillPercent);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, fillColor);
        
        // 边框
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        // 数值文本
        String valueText = String.format("%.0f / %.0f", factorStored, factorMax);
        context.drawCenteredTextWithShadow(textRenderer, valueText, barX + barWidth / 2, barY + 5, 0xFFFFFF);
    }
    
    private void drawProgressBar(DrawContext context) {
        if (progress <= 0 && currentTask == null) {
            return;
        }
        
        int barX = x + 10;
        int barY = y + 58;
        int barWidth = WIDTH - 20;
        int barHeight = 12;
        
        // 标签
        context.drawTextWithShadow(textRenderer, "工作进度", barX, barY - 10, 0xAAAAAA);
        
        // 背景
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        
        // 填充
        int fillWidth = (int) (barWidth * Math.min(1.0, progress));
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFF00AA00);
        
        // 边框
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        // 百分比
        String percentText = String.format("%.1f%%", progress * 100);
        context.drawCenteredTextWithShadow(textRenderer, percentText, barX + barWidth / 2, barY + 2, 0xFFFFFF);
    }
    
    private void drawCurrentTask(DrawContext context) {
        if (currentTask != null && !currentTask.isEmpty()) {
            int taskY = y + 78;
            context.drawTextWithShadow(textRenderer, "当前: " + currentTask, x + 10, taskY, 0xFFFF00);
        } else {
            int taskY = y + 78;
            context.drawTextWithShadow(textRenderer, "状态: 空闲", x + 10, taskY, 0xAAAAAA);
        }
    }
    
    private void drawDimensionInfo(DrawContext context) {
        int infoY = y + 98;
        
        // 维度效率
        String efficiencyText = String.format("维度效率: %.0f%%", dimensionEfficiency * 100);
        int efficiencyColor = dimensionEfficiency >= 1.0 ? 0x00FF00 : 
                              dimensionEfficiency >= 0.5 ? 0xFFFF00 : 0xFF6600;
        context.drawTextWithShadow(textRenderer, efficiencyText, x + 10, infoY, efficiencyColor);
        
        // 当前维度
        String dimName = getDimensionDisplayName(currentDimension);
        context.drawTextWithShadow(textRenderer, "当前: " + dimName, x + 10, infoY + 12, 0xAAAAAA);
        
        // 推荐维度
        if (recommendedDimension != null && !recommendedDimension.equals(currentDimension)) {
            String recDimName = getDimensionDisplayName(recommendedDimension);
            context.drawTextWithShadow(textRenderer, "推荐: " + recDimName, x + 10, infoY + 24, 0xFF9900);
        }
    }
    
    private void drawStructureStatus(DrawContext context) {
        int statusY = y + 140;
        
        if (structureValid) {
            context.drawTextWithShadow(textRenderer, "✓ 结构完整", x + 10, statusY, 0x00FF00);
        } else {
            context.drawTextWithShadow(textRenderer, "✗ 结构不完整", x + 10, statusY, 0xFF0000);
        }
    }
    
    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        // Factor 存储提示
        if (isHovering(x + 10, y + 28, WIDTH - 20, 18, mouseX, mouseY)) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.literal("Factor 存储"));
            tooltip.add(Text.literal(String.format("当前: %.2f", factorStored)));
            tooltip.add(Text.literal(String.format("容量: %.0f", factorMax)));
            tooltip.add(Text.literal(String.format("填充: %.1f%%", factorMax > 0 ? (factorStored / factorMax * 100) : 0)));
            context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
        }
        
        // Tier 提示
        if (isHovering(x + WIDTH - 45, y + 6, 38, 14, mouseX, mouseY)) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.literal("结构等级: T" + tier));
            tooltip.add(Text.literal(getTierDescription(tier)));
            context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
        }
    }
    
    private boolean isHovering(int rectX, int rectY, int width, int height, int mouseX, int mouseY) {
        return mouseX >= rectX && mouseX < rectX + width && mouseY >= rectY && mouseY < rectY + height;
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
    
    private String getDimensionDisplayName(String dimension) {
        if (dimension == null) return "未知";
        return switch (dimension) {
            case "minecraft:overworld" -> "主世界";
            case "minecraft:the_nether" -> "下界";
            case "minecraft:the_end" -> "末地";
            default -> dimension.replace("minecraft:", "");
        };
    }
    
    private String getTierDescription(int tier) {
        return switch (tier) {
            case 1 -> "基础结构";
            case 2 -> "工业结构";
            case 3 -> "维度结构";
            case 4 -> "远古结构";
            case 5 -> "仲裁结构";
            default -> "未知等级";
        };
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    /**
     * 机器状态数据记录
     */
    public record MachineStatusData(
        String machineName,
        int tier,
        double factorStored,
        double factorMax,
        double progress,
        String currentTask,
        double dimensionEfficiency,
        boolean structureValid,
        String recommendedDimension,
        String currentDimension
    ) {
        /**
         * 创建空闲状态
         */
        public static MachineStatusData idle(String name, int tier, double stored, double max, String dimension) {
            return new MachineStatusData(name, tier, stored, max, 0, null, 1.0, true, dimension, dimension);
        }
        
        /**
         * 创建工作状态
         */
        public static MachineStatusData working(String name, int tier, double stored, double max, 
                                                  double progress, String task, String dimension) {
            return new MachineStatusData(name, tier, stored, max, progress, task, 1.0, true, dimension, dimension);
        }
    }
}