package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.FactorExtractorCoreBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * Factor 提取器 UI 屏幕
 */
public class FactorExtractorScreen extends HandledScreen<FactorExtractorScreenHandler> {
    
    public FactorExtractorScreen(FactorExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        playerInventoryTitleY = backgroundHeight - 94;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景纹理
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        
        // 绘制提取进度条
        FactorExtractorCoreBlockEntity entity = handler.getEntity();
        if (entity != null) {
            int progress = entity.getExtractProgress();
            int progressHeight = (int) (progress * 0.5);
            context.fill(x + 80, y + 30 + (50 - progressHeight), x + 96, y + 80, 0xFF00FF00);
            
            // 绘制 Factor 存储条
            double storagePercent = entity.getFactorStorage() / entity.getMaxStorage();
            int storageHeight = (int) (storagePercent * 50);
            context.fill(x + 120, y + 30 + (50 - storageHeight), x + 136, y + 80, 0xFF0000FF);
        }
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
        
        FactorExtractorCoreBlockEntity entity = handler.getEntity();
        if (entity != null) {
            String storageText = String.format("%.0f / %.0f F", entity.getFactorStorage(), entity.getMaxStorage());
            context.drawText(textRenderer, storageText, 10, 20, 0x404040, false);
            
            String rateText = String.format("Rate: %.1f F/tick", entity.getExtractRate());
            context.drawText(textRenderer, rateText, 10, 32, 0x404040, false);
        }
    }
}
