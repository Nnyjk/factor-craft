package com.factorcraft.module.ui.screen;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Factor 监控界面
 * 
 * 按 K 键打开，显示当前区块的 Factor 信息
 */
public class FactorMonitorScreen extends Screen {
    private final ChunkPos chunkPos;
    
    private static final int WIDTH = 260;
    private static final int HEIGHT = 200;
    
    private int x;
    private int y;
    private ChunkFactorState state;
    
    public FactorMonitorScreen(ChunkPos chunkPos) {
        super(Text.translatable("factorcraft.screen.monitor.title"));
        this.chunkPos = chunkPos;
    }
    
    @Override
    protected void init() {
        super.init();
        this.x = (this.width - WIDTH) / 2;
        this.y = (this.height - HEIGHT) / 2;
        
        // 获取区块状态
        this.state = ChunkFactorManager.getState(chunkPos).orElse(null);
        
        // 添加关闭按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.close"),
            button -> this.close()
        ).dimensions(x + WIDTH / 2 - 40, y + HEIGHT - 28, 80, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 渲染背景
        renderBackground(context, mouseX, mouseY, delta);
        
        // 绘制面板背景
        drawPanelBackground(context);
        
        // 绘制标题
        drawTitle(context);
        
        // 绘制区块信息
        drawChunkInfo(context);
        
        if (state != null) {
            // 绘制浓度条
            drawConcentrationBar(context);
            
            // 绘制状态信息
            drawStateInfo(context);
            
            // 绘制锚点信息
            drawAnchorInfo(context);
        } else {
            // 无数据提示
            drawNoDataMessage(context);
        }
        
        // 绘制提示文本
        drawTooltips(context, mouseX, mouseY);
        
        // 渲染子组件（包括关闭按钮）
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawPanelBackground(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        
        // 边框 - 根据浓度等级着色
        int borderColor = state != null ? getConcentrationBorderColor(state.getCurrentConcentration()) : 0xFF666666;
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        
        // 顶部高亮
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, 0xFF555555);
    }
    
    private void drawTitle(DrawContext context) {
        Text title = Text.translatable("factorcraft.screen.monitor.title");
        context.drawCenteredTextWithShadow(this.textRenderer, title, this.width / 2, y + 8, 0xFFFFFF);
    }
    
    private void drawChunkInfo(DrawContext context) {
        // 区块坐标
        Text chunkText = Text.translatable("factorcraft.screen.monitor.chunk", chunkPos.x, chunkPos.z);
        context.drawTextWithShadow(this.textRenderer, chunkText, x + 10, y + 28, 0xAAAAAA);
        
        // 分隔线
        context.fill(x + 10, y + 44, x + WIDTH - 10, y + 45, 0xFF333333);
    }
    
    private void drawConcentrationBar(DrawContext context) {
        double concentration = state.getCurrentConcentration();
        
        int barX = x + 10;
        int barY = y + 55;
        int barWidth = WIDTH - 20;
        int barHeight = 24;
        
        // 标签
        Text label = Text.translatable("factorcraft.screen.monitor.concentration");
        context.drawTextWithShadow(this.textRenderer, label, barX, barY - 12, 0xAAAAAA);
        
        // 背景条
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        
        // 浓度条
        double maxConcentration = 150.0;
        int fillWidth = (int) (barWidth * Math.min(concentration / maxConcentration, 1.0));
        int fillColor = getConcentrationBarColor(concentration);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, fillColor);
        
        // 边框
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        // 数值文本
        Text valueText = Text.translatable("factorcraft.screen.monitor.concentration.value", 
            String.format("%.1f", concentration));
        context.drawCenteredTextWithShadow(this.textRenderer, valueText, barX + barWidth / 2, barY + 8, 0xFFFFFF);
    }
    
    private void drawStateInfo(DrawContext context) {
        int infoY = y + 95;
        
        // 浓度等级
        String tierKey = getConcentrationTierKey(state.getCurrentConcentration());
        Text tierText = Text.translatable("factorcraft.screen.monitor.tier", 
            Text.translatable(tierKey));
        int tierColor = getConcentrationTierColor(state.getCurrentConcentration());
        context.drawTextWithShadow(this.textRenderer, tierText, x + 10, infoY, tierColor);
        
        // 初始浓度
        Text initialText = Text.translatable("factorcraft.screen.monitor.initial", 
            String.format("%.1f", state.getInitialConcentration()));
        context.drawTextWithShadow(this.textRenderer, initialText, x + 10, infoY + 14, 0x888888);
        
        // 浓度下限
        Text floorText = Text.translatable("factorcraft.screen.monitor.floor", 
            String.format("%.1f", state.getConcentrationFloor()));
        context.drawTextWithShadow(this.textRenderer, floorText, x + 10, infoY + 28, 0x666666);
    }
    
    private void drawAnchorInfo(DrawContext context) {
        int anchorY = y + 145;
        
        if (state.isAnchored()) {
            // 锚定状态
            Text anchorText = Text.translatable("factorcraft.screen.monitor.anchored", 
                state.getAnchorRadius());
            context.drawTextWithShadow(this.textRenderer, anchorText, x + 10, anchorY, 0x55FF55);
            
            // 锚定图标效果
            context.fill(x + WIDTH - 30, anchorY - 2, x + WIDTH - 10, anchorY + 12, 0xFF00AA00);
        } else {
            // 未锚定
            Text unanchoredText = Text.translatable("factorcraft.screen.monitor.unanchored");
            context.drawTextWithShadow(this.textRenderer, unanchoredText, x + 10, anchorY, 0x888888);
        }
    }
    
    private void drawNoDataMessage(DrawContext context) {
        Text noData = Text.translatable("factorcraft.screen.monitor.no_data");
        context.drawCenteredTextWithShadow(this.textRenderer, noData, this.width / 2, y + 80, 0x888888);
        
        Text hint = Text.translatable("factorcraft.screen.monitor.hint");
        context.drawCenteredTextWithShadow(this.textRenderer, hint, this.width / 2, y + 100, 0x666666);
    }
    
    private void drawTooltips(DrawContext context, int mouseX, int mouseY) {
        if (state == null) return;
        
        // 浓度条提示
        int barX = x + 10;
        int barY = y + 55;
        int barWidth = WIDTH - 20;
        int barHeight = 24;
        
        if (isHovering(barX, barY, barWidth, barHeight, mouseX, mouseY)) {
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.translatable("factorcraft.tooltip.concentration"));
            tooltip.add(Text.translatable("factorcraft.tooltip.concentration.current", 
                String.format("%.2f", state.getCurrentConcentration())));
            tooltip.add(Text.translatable("factorcraft.tooltip.concentration.initial", 
                String.format("%.2f", state.getInitialConcentration())));
            tooltip.add(Text.translatable("factorcraft.tooltip.concentration.floor", 
                String.format("%.2f", state.getConcentrationFloor())));
            context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
        }
    }
    
    private boolean isHovering(int rectX, int rectY, int width, int height, int mouseX, int mouseY) {
        return mouseX >= rectX && mouseX < rectX + width && mouseY >= rectY && mouseY < rectY + height;
    }
    
    // ==================== 辅助方法 ====================
    
    private int getConcentrationBarColor(double concentration) {
        if (concentration < 20) return 0xFF5555;    // 红色 - 枯竭
        if (concentration < 50) return 0xFFAA55;    // 橙色 - 低能
        if (concentration < 80) return 0xFFFF55;    // 黄色 - 稳定
        if (concentration < 100) return 0x55FF55;   // 绿色 - 高能
        return 0x55FFFF;                             // 青色 - 过载
    }
    
    private int getConcentrationBorderColor(double concentration) {
        if (concentration < 20) return 0xFF3333;
        if (concentration < 50) return 0xFF8833;
        if (concentration < 80) return 0xFFFF33;
        if (concentration < 100) return 0x33FF33;
        return 0x33FFFF;
    }
    
    private int getConcentrationTierColor(double concentration) {
        if (concentration < 20) return 0xFF5555;
        if (concentration < 50) return 0xFFAA55;
        if (concentration < 80) return 0xFFFF55;
        if (concentration < 100) return 0x55FF55;
        return 0x55FFFF;
    }
    
    private String getConcentrationTierKey(double concentration) {
        if (concentration < 20) return "factorcraft.tier.depleted";
        if (concentration < 50) return "factorcraft.tier.low_energy";
        if (concentration < 80) return "factorcraft.tier.stable";
        if (concentration < 100) return "factorcraft.tier.high_energy";
        return "factorcraft.tier.overload";
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // ESC 键关闭
        if (keyCode == 256) { // GLFW.GLFW_KEY_ESCAPE
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击背景外部关闭
        if (!isHovering(x, y, WIDTH, HEIGHT, (int) mouseX, (int) mouseY)) {
            this.close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}