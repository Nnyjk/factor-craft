package com.factorcraft.module.machine.synthesizer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Factor 合成器 GUI 屏幕
 */
public class SynthesizerScreen extends HandledScreen<SynthesizerScreenHandler> {
    
    private static final Identifier TEXTURE = Identifier.of("factorcraft", "textures/gui/synthesizer.png");
    
    public SynthesizerScreen(SynthesizerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 166;
        this.backgroundWidth = 176;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 渲染背景 - Minecraft 1.21.4 API
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);
        
        // 渲染进度条
        if (handler.isProcessing()) {
            int progress = handler.getProcessingProgress();
            int maxProgress = handler.getMaxProcessingProgress();
            int progressWidth = (int) ((progress / (float) maxProgress) * 24);
            
            // 进度条背景位置：79, 35
            context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x + 79, y + 35, 176, 0, progressWidth, 16, backgroundWidth, backgroundHeight);
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void init() {
        super.init();
        // 标题居中
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}