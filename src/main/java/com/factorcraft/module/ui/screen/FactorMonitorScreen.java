package com.factorcraft.module.ui.screen;

import com.factorcraft.module.factor.management.ChunkFactorManager;
import com.factorcraft.module.factor.state.ChunkFactorState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

public class FactorMonitorScreen extends Screen {
    private final ChunkPos chunkPos;
    private static final int BG_COLOR = 0xCC000000;
    private static final int BORDER_COLOR = 0xFF555555;
    
    public FactorMonitorScreen(ChunkPos chunkPos) {
        super(Text.literal("Factor 监控"));
        this.chunkPos = chunkPos;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景
        int x = (this.width - 200) / 2;
        int y = (this.height - 150) / 2;
        context.fill(x, y, x + 200, y + 150, BG_COLOR);
        context.drawBorder(x, y, 200, 150, BORDER_COLOR);
        
        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, "Factor 监控", this.width / 2, y + 10, 0xFFFFFF);
        
        // 区块信息
        context.drawTextWithShadow(this.textRenderer, "区块: " + chunkPos.x + ", " + chunkPos.z, x + 10, y + 30, 0xAAAAAA);
        
        // 获取浓度
        ChunkFactorManager.getState(chunkPos).ifPresent(state -> {
            renderConcentrationBar(context, x + 10, y + 50, state);
            renderStateInfo(context, x + 10, y + 90, state);
        });
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderConcentrationBar(DrawContext context, int x, int y, ChunkFactorState state) {
        double concentration = state.getCurrentConcentration();
        int barWidth = 180;
        int barHeight = 20;
        
        // 背景条
        context.fill(x, y, x + barWidth, y + barHeight, 0x333333);
        
        // 浓度条
        int fillWidth = (int) (barWidth * Math.min(concentration / 150.0, 1.0));
        int color = getConcentrationColor(concentration);
        context.fill(x, y, x + fillWidth, y + barHeight, color);
        
        // 文字
        String text = String.format("%.1f", concentration);
        context.drawCenteredTextWithShadow(this.textRenderer, text, x + barWidth / 2, y + 6, 0xFFFFFF);
    }
    
    private void renderStateInfo(DrawContext context, int x, int y, ChunkFactorState state) {
        String tier = getConcentrationTier(state.getCurrentConcentration());
        context.drawTextWithShadow(this.textRenderer, "等级: " + tier, x, y, 0xFFFFFF);
        
        if (state.isAnchored()) {
            context.drawTextWithShadow(this.textRenderer, "锚定半径: " + state.getAnchorRadius(), x, y + 12, 0x55FF55);
        }
    }
    
    private int getConcentrationColor(double concentration) {
        if (concentration < 20) return 0xFF5555;
        if (concentration < 50) return 0xFFAA55;
        if (concentration < 80) return 0xFFFF55;
        if (concentration < 100) return 0x55FF55;
        return 0x55FFFF;
    }
    
    private String getConcentrationTier(double concentration) {
        if (concentration < 20) return "DEPLETED";
        if (concentration < 50) return "LOW_ENERGY";
        if (concentration < 80) return "STABLE";
        if (concentration < 100) return "HIGH_ENERGY";
        return "OVERLOAD";
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}