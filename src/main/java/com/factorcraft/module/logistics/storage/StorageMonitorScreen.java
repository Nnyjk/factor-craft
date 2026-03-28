package com.factorcraft.module.logistics.storage;

import com.factorcraft.factor.FactorType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

/**
 * 存储监控器屏幕
 */
public class StorageMonitorScreen extends HandledScreen<StorageMonitorScreenHandler> {
    
    private TextFieldWidget searchField;
    
    public StorageMonitorScreen(StorageMonitorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 256;
        this.backgroundHeight = 224;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 创建搜索框
        int searchX = (width - backgroundWidth) / 2 + 8;
        int searchY = (height - backgroundHeight) / 2 + 10;
        searchField = new TextFieldWidget(textRenderer, searchX, searchY, 150, 18, Text.literal("Search"));
        searchField.setMaxLength(32);
        searchField.setText(handler.getSearchFilter());
        addDrawableChild(searchField);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // 渲染 Factor 列表
        renderFactorList(context, mouseX, mouseY);
    }
    
    private void renderFactorList(DrawContext context, int mouseX, int mouseY) {
        Map<FactorType, Integer> factorData = handler.getFactorData();
        
        int startX = (width - backgroundWidth) / 2 + 10;
        int startY = (height - backgroundHeight) / 2 + 40;
        
        context.drawText(textRenderer, "存储的 Factor:", startX, startY - 10, 0x404040, false);
        
        int y = startY;
        for (Map.Entry<FactorType, Integer> entry : factorData.entrySet()) {
            FactorType type = entry.getKey();
            int amount = entry.getValue();
            
            String text = type.name() + ": " + formatNumber(amount);
            context.drawText(textRenderer, text, startX, y, 0x000000, false);
            
            y += 12;
            if (y > startY + 150) break; // 限制显示数量
        }
        
        if (factorData.isEmpty()) {
            context.drawText(textRenderer, "暂无存储的 Factor", startX, startY, Formatting.GRAY.getColorValue(), false);
        }
    }
    
    private String formatNumber(int num) {
        if (num >= 1_000_000) {
            return String.format("%.2fM", num / 1_000_000.0);
        } else if (num >= 1_000) {
            return String.format("%.2fK", num / 1_000.0);
        }
        return String.valueOf(num);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchField.keyPressed(keyCode, scanCode, modifiers)) {
            handler.setSearchFilter(searchField.getText());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        searchField.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchField.charTyped(chr, modifiers)) {
            handler.setSearchFilter(searchField.getText());
            return true;
        }
        return super.charTyped(chr, modifiers);
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 绘制背景
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
