package com.factorcraft.module.ui.screen;

import com.factorcraft.module.material.trait.TraitInstance;
import com.factorcraft.module.material.trait.TraitService;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class TraitDisplayScreen extends Screen {
    private final ItemStack stack;
    private static final int BG_COLOR = 0xCC000000;
    private static final int BORDER_COLOR = 0xFF555555;
    
    public TraitDisplayScreen(ItemStack stack) {
        super(Text.literal("特性信息"));
        this.stack = stack;
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景
        int x = (this.width - 256) / 2;
        int y = (this.height - 200) / 2;
        context.fill(x, y, x + 256, y + 200, BG_COLOR);
        
        // 边框
        context.drawBorder(x, y, 256, 200, BORDER_COLOR);
        
        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, "特性信息", this.width / 2, y + 10, 0xFFFFFF);
        
        // 获取特性列表
        List<TraitInstance> traits = TraitService.getTraits(stack);
        
        if (traits.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, "无特性", this.width / 2, y + 50, 0xAAAAAA);
        } else {
            int offsetY = 30;
            for (TraitInstance trait : traits) {
                final int currentY = y + offsetY;
                trait.getDefinition().ifPresent(def -> {
                    String text = def.name() + " Lv." + trait.level();
                    int color = def.isPositive() ? 0x55FF55 : 0xFF5555;
                    context.drawTextWithShadow(this.textRenderer, text, x + 10, currentY, color);
                    context.drawTextWithShadow(this.textRenderer, def.description(), x + 20, currentY + 12, 0xAAAAAA);
                });
                offsetY += 30;
            }
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}