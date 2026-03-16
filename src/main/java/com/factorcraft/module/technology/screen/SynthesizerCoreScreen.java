package com.factorcraft.module.technology.screen;

import com.factorcraft.module.technology.machine.SynthesisConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 合成核心 GUI 界面
 * 
 * 显示配方列表、合成进度、Factor 缓冲区
 */
public class SynthesizerCoreScreen extends HandledScreen<SynthesizerCoreScreenHandler> {
    
    private static final Identifier BACKGROUND = Identifier.of("factorcraft", "textures/gui/synthesizer_core.png");
    
    private static final int WIDTH = 280;
    private static final int HEIGHT = 200;
    
    // 配方列表滚动
    private int recipeScrollOffset = 0;
    private int selectedRecipeIndex = -1;
    
    // 按钮
    private ButtonWidget startButton;
    private ButtonWidget cancelButton;
    
    public SynthesizerCoreScreen(SynthesizerCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.titleX = 10;
        this.titleY = 6;
        
        // 关闭按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.close"),
            button -> this.close()
        ).dimensions(x + WIDTH - 60, y + HEIGHT - 20, 50, 16).build());
        
        // 开始合成按钮
        startButton = ButtonWidget.builder(
            Text.translatable("factorcraft.gui.synthesizer.start"),
            button -> {
                if (selectedRecipeIndex >= 0) {
                    var recipes = handler.getAvailableRecipes();
                    if (selectedRecipeIndex < recipes.size()) {
                        handler.startCrafting(recipes.get(selectedRecipeIndex).id());
                    }
                }
            }
        ).dimensions(x + 10, y + HEIGHT - 45, 80, 18).build();
        startButton.active = false;
        this.addDrawableChild(startButton);
        
        // 取消合成按钮
        cancelButton = ButtonWidget.builder(
            Text.translatable("factorcraft.gui.synthesizer.cancel"),
            button -> handler.cancelCrafting()
        ).dimensions(x + 95, y + HEIGHT - 45, 50, 18).build();
        cancelButton.active = false;
        this.addDrawableChild(cancelButton);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        
        drawPanel(context);
        drawStructureInfo(context);
        drawFactorBuffer(context);
        drawCraftingProgress(context);
        drawRecipeList(context, mouseX, mouseY);
        
        // 更新按钮状态
        updateButtonStates();
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void drawPanel(DrawContext context) {
        // 半透明背景
        context.fill(x, y, x + WIDTH, y + HEIGHT, 0xE0000000);
        
        // 边框
        int borderColor = getTierColor(handler.getTier());
        context.drawBorder(x, y, WIDTH, HEIGHT, borderColor);
        context.fill(x + 1, y + 1, x + WIDTH - 1, y + 3, borderColor);
    }
    
    private void drawStructureInfo(DrawContext context) {
        int statusY = y + 20;
        
        // 结构状态
        boolean valid = handler.isStructureValid();
        Text statusText = valid 
            ? Text.translatable("factorcraft.gui.structure.valid")
            : Text.translatable("factorcraft.gui.structure.invalid");
        int statusColor = valid ? 0x55FF55 : 0xFF5555;
        
        context.drawTextWithShadow(this.textRenderer, statusText, x + 10, statusY, statusColor);
        
        // Tier 徽章
        int badgeX = x + 100;
        int tier = handler.getTier();
        int tierColor = getTierColor(tier);
        
        context.fill(badgeX, statusY - 2, badgeX + 40, statusY + 12, tierColor);
        Text tierText = Text.translatable("factorcraft.gui.tier", tier);
        context.drawCenteredTextWithShadow(this.textRenderer, tierText, badgeX + 20, statusY, 0xFFFFFF);
        
        // 效率
        double eff = handler.getEfficiency();
        Text effText = Text.translatable("factorcraft.gui.efficiency", String.format("%.0f%%", eff * 100));
        context.drawTextWithShadow(this.textRenderer, effText, x + 10, statusY + 15, 0xAAAAAA);
    }
    
    private void drawFactorBuffer(DrawContext context) {
        int bufferY = y + 50;
        
        // 标签
        Text label = Text.translatable("factorcraft.gui.synthesizer.buffer");
        context.drawTextWithShadow(this.textRenderer, label, x + 10, bufferY, 0xAAAAAA);
        
        // 缓冲条
        int barX = x + 10;
        int barY = bufferY + 12;
        int barWidth = 120;
        int barHeight = 16;
        
        double percentage = handler.getBufferPercentage();
        int fillColor = getBufferColor(percentage);
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        int fillWidth = (int) (barWidth * percentage / 100.0);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, fillColor);
        context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
        
        Text valueText = Text.literal(String.format("%.0f / %.0f", 
            handler.getFactorBuffer(), handler.getMaxBuffer()));
        context.drawCenteredTextWithShadow(this.textRenderer, valueText, barX + barWidth / 2, barY + 4, 0xFFFFFF);
    }
    
    private void drawCraftingProgress(DrawContext context) {
        int craftY = y + 90;
        
        if (handler.isCrafting()) {
            // 当前配方
            Text recipeLabel = Text.translatable("factorcraft.gui.synthesizer.crafting");
            context.drawTextWithShadow(this.textRenderer, recipeLabel, x + 10, craftY, 0x55FFFF);
            
            // 进度条
            int barX = x + 10;
            int barY = craftY + 12;
            int barWidth = 120;
            int barHeight = 12;
            
            double progress = handler.getCraftProgressPercentage();
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            int fillWidth = (int) (barWidth * progress / 100.0);
            context.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFF00AA00);
            context.drawBorder(barX, barY, barWidth, barHeight, 0xFF666666);
            
            Text progressText = Text.literal(String.format("%.0f%%", progress));
            context.drawCenteredTextWithShadow(this.textRenderer, progressText, barX + barWidth / 2, barY + 2, 0xFFFFFF);
            
            // Factor 消耗
            Text factorText = Text.translatable("factorcraft.gui.synthesizer.factor_cost",
                String.format("%.1f / %.1f", handler.getFactorConsumed(), handler.getFactorNeeded()));
            context.drawTextWithShadow(this.textRenderer, factorText, x + 10, craftY + 28, 0xAAAAAA);
        } else {
            Text idleText = Text.translatable("factorcraft.gui.synthesizer.idle");
            context.drawTextWithShadow(this.textRenderer, idleText, x + 10, craftY, 0x888888);
            
            // 提示选择配方
            if (selectedRecipeIndex >= 0) {
                var recipes = handler.getAvailableRecipes();
                if (selectedRecipeIndex < recipes.size()) {
                    var recipe = recipes.get(selectedRecipeIndex);
                    Text selectedText = Text.translatable("factorcraft.gui.synthesizer.selected", 
                        recipe.id());
                    context.drawTextWithShadow(this.textRenderer, selectedText, x + 10, craftY + 15, 0xFFFF55);
                    
                    // 显示 Factor 需求
                    Text costText = Text.literal(String.format("Factor: %.0f", recipe.factorCost()));
                    context.drawTextWithShadow(this.textRenderer, costText, x + 10, craftY + 28, 0xAAAAAA);
                }
            }
        }
    }
    
    private void drawRecipeList(DrawContext context, int mouseX, int mouseY) {
        int listX = x + 150;
        int listY = y + 20;
        int listWidth = 120;
        int listHeight = 160;
        
        // 背景
        context.fill(listX, listY, listX + listWidth, listY + listHeight, 0x80000000);
        context.drawBorder(listX, listY, listWidth, listHeight, 0xFF555555);
        
        // 标题
        Text listTitle = Text.translatable("factorcraft.gui.synthesizer.recipes");
        context.drawCenteredTextWithShadow(this.textRenderer, listTitle, listX + listWidth / 2, listY + 4, 0xAAAAAA);
        
        // 配方列表
        var recipes = handler.getAvailableRecipes();
        int itemY = listY + 18;
        int itemHeight = 24;
        
        for (int i = recipeScrollOffset; i < Math.min(recipes.size(), recipeScrollOffset + 5); i++) {
            var recipe = recipes.get(i);
            int idx = i - recipeScrollOffset;
            int itemStartY = itemY + idx * itemHeight;
            
            // 选中高亮
            boolean hovered = mouseX >= listX && mouseX < listX + listWidth &&
                              mouseY >= itemStartY && mouseY < itemStartY + itemHeight;
            boolean selected = i == selectedRecipeIndex;
            
            if (selected || hovered) {
                int bgColor = selected ? 0x8055FF55 : 0x40555555;
                context.fill(listX + 1, itemStartY, listX + listWidth - 1, itemStartY + itemHeight, bgColor);
            }
            
            // 配方名称
            Text recipeName = Text.translatable("factorcraft.recipe." + recipe.id(), recipe.id());
            context.drawTextWithShadow(this.textRenderer, recipeName, listX + 4, itemStartY + 2, 0xFFFFFF);
            
            // Factor 成本
            Text costText = Text.literal(String.format("F: %.0f", recipe.factorCost()));
            context.drawTextWithShadow(this.textRenderer, costText, listX + 4, itemStartY + 12, 0xAAAAAA);
        }
        
        // 空列表提示
        if (recipes.isEmpty()) {
            Text emptyText = Text.translatable("factorcraft.gui.synthesizer.no_recipes");
            context.drawCenteredTextWithShadow(this.textRenderer, emptyText, listX + listWidth / 2, listY + 60, 0x888888);
        }
        
        // 滚动提示
        if (recipes.size() > 5) {
            Text scrollHint = Text.literal("↓ ↑");
            context.drawCenteredTextWithShadow(this.textRenderer, scrollHint, listX + listWidth / 2, listY + listHeight - 12, 0x888888);
        }
    }
    
    private void updateButtonStates() {
        // 开始按钮：有选中配方且当前未在合成
        startButton.active = selectedRecipeIndex >= 0 && !handler.isCrafting();
        
        // 取消按钮：当前正在合成
        cancelButton.active = handler.isCrafting();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 配方列表点击选择
        int listX = x + 150;
        int listY = y + 20;
        int listWidth = 120;
        int itemY = listY + 18;
        int itemHeight = 24;
        
        if (mouseX >= listX && mouseX < listX + listWidth) {
            var recipes = handler.getAvailableRecipes();
            for (int i = recipeScrollOffset; i < Math.min(recipes.size(), recipeScrollOffset + 5); i++) {
                int idx = i - recipeScrollOffset;
                int itemStartY = itemY + idx * itemHeight;
                
                if (mouseY >= itemStartY && mouseY < itemStartY + itemHeight) {
                    selectedRecipeIndex = i;
                    return true;
                }
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // 配方列表滚动
        int listX = x + 150;
        int listY = y + 20;
        int listWidth = 120;
        int listHeight = 160;
        
        if (mouseX >= listX && mouseX < listX + listWidth &&
            mouseY >= listY && mouseY < listY + listHeight) {
            var recipes = handler.getAvailableRecipes();
            int maxScroll = Math.max(0, recipes.size() - 5);
            
            recipeScrollOffset = Math.max(0, Math.min(maxScroll, 
                recipeScrollOffset - (int) verticalAmount));
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    // ==================== 辅助方法 ====================
    
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
    
    private int getBufferColor(double percentage) {
        if (percentage >= 90) return 0xFF00FF00;
        if (percentage >= 70) return 0xFF88FF00;
        if (percentage >= 50) return 0xFFFFFF00;
        if (percentage >= 30) return 0xFFFFAA00;
        return 0xFFFF5500;
    }
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 不使用纹理背景
    }
}