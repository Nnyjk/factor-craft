package com.factorcraft.module.logistics.request;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * 自动请求终端屏幕
 */
public class RequestTerminalScreen extends HandledScreen<RequestTerminalScreenHandler> {
    
    public RequestTerminalScreen(RequestTerminalScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 224;
    }
    
    @Override
    protected void init() {
        super.init();
        // TODO: 添加请求界面元素
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // 渲染请求界面
        renderRequestInterface(context, mouseX, mouseY);
    }
    
    private void renderRequestInterface(DrawContext context, int mouseX, int mouseY) {
        int startX = (width - backgroundWidth) / 2 + 10;
        int startY = (height - backgroundHeight) / 2 + 20;
        
        context.drawText(textRenderer, "自动请求终端", startX, startY, 0x404040, false);
        context.drawText(textRenderer, "功能开发中...", startX, startY + 20, 0x808080, false);
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.fill(
            (width - backgroundWidth) / 2,
            (height - backgroundHeight) / 2,
            (width + backgroundWidth) / 2,
            (height + backgroundHeight) / 2,
            0xFFC0C0C0
        );
    }
    
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0x404040, false);
    }
}
