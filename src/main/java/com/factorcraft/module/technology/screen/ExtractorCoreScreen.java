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
 * 提取核心 GUI 界面
 * 
 * 显示多方块结构信息、Factor 存储状态、效率数据
 * 使用 GuiRenderHelper 实现视觉效果
 */
public class ExtractorCoreScreen extends HandledScreen<ExtractorCoreScreenHandler> {
    
    private static final Identifier BACKGROUND = Identifier.of("factorcraft", "textures/gui/extractor_core.png");
    
    private static final int WIDTH = 220;
    private static final int HEIGHT = 180;
    
    // 动画管理器
    private final GuiAnimationManager animManager = GuiAnimationManager.getInstance();
    private final String machineId;
    
    // 缓存的动画值
    private double animatedProgress = 0;
    private double animatedStorage = 0;
    private double animatedEfficiency = 0;
    
    public ExtractorCoreScreen(ExtractorCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
        this.machineId = "extractor_" + handler.hashCode();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 标题位置调整
        this.titleX = 10;
        this.titleY = 6;
        
        // 关闭按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.close"),
            button -> this.close()
        ).dimensions(x + WIDTH - 60, y + HEIGHT - 20, 50, 16).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 更新动画
        updateAnimations();
        
        // 渲染背景
        renderBackground(context, mouseX, mouseY, delta);
        
        // 渲染面板
        drawPanel(context);
        
        // 渲染结构信息
        drawStructureInfo(context);
        
        // 渲染 Factor 存储
        drawFactorStorage(context);
        
        // 渲染效率信息
        drawEfficiencyInfo(context);
        
        // 渲染进度条
        drawProgressBar(context);
        
        // 渲染状态指示器
        drawStatusIndicator(context);
        
        // 渲染提示
        drawTooltips(context, mouseX, mouseY);
        
        // 渲染子组件
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void updateAnimations() {
        // 平滑动画进度
        animatedProgress = animManager.animateProgress(machineId, handler.getProgressPercentage() / 100.0);
        animatedStorage = animManager.animateFactorStorage(machineId, handler.getStoragePercentage() / 100.0);
        animatedEfficiency = animManager.animateEfficiency(machineId, handler.getEfficiency());
    }
    
    private void drawPanel(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        
        // 边框 - 根据 Tier 着色
        int borderColor = getTierColor(handler.getTier());
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        
        // 顶部高亮
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, borderColor);
    }
    
    private void drawStructureInfo(DrawContext context) {
        // 结构状态
        boolean valid = handler.isStructureValid();
        int statusY = y + 20;
        
        // 状态图标和文本
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
        int tier = handler.getTier();
        int tierColor = getTierColor(tier);
        
        context.fill(badgeX, statusY - 2, badgeX + 40, statusY + 12, tierColor);
        Text tierText = Text.translatable("factorcraft.gui.tier", tier);
        context.drawCenteredTextWithShadow(this.textRenderer, tierText, badgeX + 20, statusY, 0xFFFFFF);
        
        // 结构名称
        if (valid) {
            Text structName = Text.translatable("factorcraft.structure.extractor.t" + tier);
            context.drawTextWithShadow(this.textRenderer, structName, x + 24, statusY + 15, 0xAAAAAA);
        }
    }
    
    private void drawFactorStorage(DrawContext context) {
        int storageY = y + 50;
        
        // 使用 GuiRenderHelper 渲染 Factor 存储
        GuiRenderHelper.drawFactorStorage(
            context, 
            x + 10, storageY + 12, 
            WIDTH - 20, 20,
            handler.getFactorStorage(), 
            handler.getMaxStorage(),
            "Factor 存储"
        );
        
        // 流量指示器
        double inputRate = handler.getExtractRate();
        GuiRenderHelper.drawFlowIndicator(
            context, 
            x + WIDTH - 70, storageY + 40,
            inputRate, 0
        );
    }
    
    private void drawEfficiencyInfo(DrawContext context) {
        int infoY = y + 85;
        
        // 结构效率 - 带动画
        double eff = animatedEfficiency * 100;
        Text effText = Text.translatable("factorcraft.gui.efficiency.structure", 
            String.format("%.0f%%", eff));
        int effColor = eff >= 80 ? 0x55FF55 : eff >= 50 ? 0xFFFF55 : 0xFF5555;
        context.drawTextWithShadow(this.textRenderer, effText, x + 10, infoY, effColor);
        
        // 效率条
        GuiRenderHelper.drawProgressBar(
            context, 
            x + 100, infoY, 
            WIDTH - 110, 8, 
            animatedEfficiency, 
            true
        );
        
        // 维度效率
        double dimEff = handler.getDimensionEfficiency();
        int dimEffColor = dimEff >= 1.0 ? 0x55FF55 : dimEff >= 0.5 ? 0xFFFF55 : 0xFF5555;
        Text dimEffText = Text.translatable("factorcraft.gui.efficiency.dimension", 
            String.format("%.0f%%", dimEff * 100));
        context.drawTextWithShadow(this.textRenderer, dimEffText, x + 10, infoY + 12, dimEffColor);
        
        // 当前维度
        String dimName = getDimensionDisplayName(handler.getDimension());
        Text dimText = Text.translatable("factorcraft.gui.dimension.current", dimName);
        context.drawTextWithShadow(this.textRenderer, dimText, x + 10, infoY + 24, 0x888888);
        
        // 推荐维度
        String recDim = handler.getRecommendedDimension();
        if (recDim != null && !recDim.equals(handler.getDimension())) {
            String recDimName = getDimensionDisplayName(recDim);
            Text recText = Text.translatable("factorcraft.gui.dimension.recommended", recDimName);
            context.drawTextWithShadow(this.textRenderer, recText, x + 10, infoY + 36, 0xFFAA00);
        }
    }
    
    private void drawProgressBar(DrawContext context) {
        int barY = y + 145;
        
        // 提取速率
        double rate = handler.getExtractRate();
        Text rateText = Text.translatable("factorcraft.gui.extractor.rate", 
            String.format("%.2f", rate));
        context.drawTextWithShadow(this.textRenderer, rateText, x + 10, barY, 0x55FFFF);
        
        // 进度条 - 带标签
        Text progressLabel = Text.translatable("factorcraft.gui.progress");
        GuiRenderHelper.drawLabeledProgressBar(
            context, 
            x + 100, barY, 
            WIDTH - 110, 8, 
            animatedProgress,
            progressLabel,
            true
        );
    }
    
    private void drawStatusIndicator(DrawContext context) {
        // 确定机器状态
        GuiRenderHelper.MachineStatus status;
        String detail = "";
        
        if (!handler.isStructureValid()) {
            status = GuiRenderHelper.MachineStatus.ERROR;
            detail = "结构不完整";
        } else if (handler.getStoragePercentage() >= 95) {
            status = GuiRenderHelper.MachineStatus.WARNING;
            detail = "存储即将满";
        } else if (handler.getExtractRate() > 0) {
            status = GuiRenderHelper.MachineStatus.WORKING;
            detail = String.format("提取中: %.2f/t", handler.getExtractRate());
        } else {
            status = GuiRenderHelper.MachineStatus.IDLE;
            detail = "等待资源";
        }
        
        // 渲染状态指示器
        GuiRenderHelper.drawStatusText(context, x + 10, y + HEIGHT - 25, status, detail);
    }
    
    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        // Factor 存储提示
        int barX = x + 10;
        int barY = y + 62;
        int barWidth = WIDTH - 20;
        int barHeight = 20;
        
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
        int progressX = x + 100;
        int progressY = y + 145;
        if (mouseX >= progressX && mouseX < progressX + WIDTH - 110 && 
            mouseY >= progressY && mouseY < progressY + 8) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.translatable("factorcraft.tooltip.progress"));
            tooltip.add(Text.literal(String.format("%.1f%%", animatedProgress * 100)));
            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }
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
    
    private String getDimensionDisplayName(String dimension) {
        if (dimension == null) return "未知";
        return switch (dimension) {
            case "minecraft:overworld" -> "主世界";
            case "minecraft:the_nether" -> "下界";
            case "minecraft:the_end" -> "末地";
            default -> dimension.replace("minecraft:", "");
        };
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 不使用纹理背景
    }
}