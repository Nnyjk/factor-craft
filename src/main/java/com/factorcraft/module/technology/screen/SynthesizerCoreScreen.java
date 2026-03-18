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
 * 使用 GuiRenderHelper 实现视觉效果
 */
public class SynthesizerCoreScreen extends HandledScreen<SynthesizerCoreScreenHandler> {
    
    private static final Identifier BACKGROUND = Identifier.of("factorcraft", "textures/gui/synthesizer_core.png");
    
    private static final int WIDTH = 280;
    private static final int HEIGHT = 200;
    
    // 动画管理器
    private final GuiAnimationManager animManager = GuiAnimationManager.getInstance();
    private final String machineId;
    
    // 配方列表滚动
    private int recipeScrollOffset = 0;
    private int selectedRecipeIndex = -1;
    
    // 按钮
    private ButtonWidget startButton;
    private ButtonWidget cancelButton;
    
    // 缓存的动画值
    private double animatedProgress = 0;
    private double animatedBuffer = 0;
    private double animatedEfficiency = 0;
    
    public SynthesizerCoreScreen(SynthesizerCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = WIDTH;
        this.backgroundHeight = HEIGHT;
        this.machineId = "synthesizer_" + handler.hashCode();
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
        // 更新动画
        updateAnimations();
        
        renderBackground(context, mouseX, mouseY, delta);
        
        drawPanel(context);
        drawStructureInfo(context);
        drawFactorBuffer(context);
        drawCraftingProgress(context);
        drawRecipeList(context, mouseX, mouseY);
        drawStatusIndicator(context);
        
        // 更新按钮状态
        updateButtonStates();
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void updateAnimations() {
        animatedProgress = animManager.animateProgress(machineId, handler.getCraftProgressPercentage() / 100.0);
        animatedBuffer = animManager.animateFactorStorage(machineId, handler.getBufferPercentage() / 100.0);
        animatedEfficiency = animManager.animateEfficiency(machineId, handler.getEfficiency());
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
        
        // 使用 GuiRenderHelper 绘制状态
        boolean valid = handler.isStructureValid();
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
        int badgeX = x + 100;
        int tier = handler.getTier();
        int tierColor = getTierColor(tier);
        
        context.fill(badgeX, statusY - 2, badgeX + 40, statusY + 12, tierColor);
        Text tierText = Text.translatable("factorcraft.gui.tier", tier);
        context.drawCenteredTextWithShadow(this.textRenderer, tierText, badgeX + 20, statusY, 0xFFFFFF);
        
        // 效率条
        GuiRenderHelper.drawLabeledProgressBar(
            context, 
            x + 10, statusY + 15, 
            130, 6, 
            animatedEfficiency,
            Text.translatable("factorcraft.gui.efficiency", String.format("%.0f%%", animatedEfficiency * 100)),
            false
        );
    }
    
    private void drawFactorBuffer(DrawContext context) {
        int bufferY = y + 50;
        
        // 使用 GuiRenderHelper 渲染 Factor 存储
        GuiRenderHelper.drawFactorStorage(
            context, 
            x + 10, bufferY + 12, 
            120, 16,
            handler.getFactorBuffer(), 
            handler.getMaxBuffer(),
            "Factor 缓冲"
        );
    }
    
    private void drawCraftingProgress(DrawContext context) {
        int craftY = y + 90;
        
        if (handler.isCrafting()) {
            // 当前配方
            Text recipeLabel = Text.translatable("factorcraft.gui.synthesizer.crafting");
            context.drawTextWithShadow(this.textRenderer, recipeLabel, x + 10, craftY, 0x55FFFF);
            
            // 使用 GuiRenderHelper 渲染进度条
            GuiRenderHelper.drawLabeledProgressBar(
                context, 
                x + 10, craftY + 12, 
                120, 12, 
                animatedProgress,
                null,
                true
            );
            
            // Factor 消耗
            double consumed = handler.getFactorConsumed();
            double needed = handler.getFactorNeeded();
            Text factorText = Text.translatable("factorcraft.gui.synthesizer.factor_cost",
                String.format("%.1f / %.1f", consumed, needed));
            context.drawTextWithShadow(this.textRenderer, factorText, x + 10, craftY + 28, 0xAAAAAA);
            
            // 流量指示器
            double consumptionRate = handler.getFactorConsumptionRate();
            GuiRenderHelper.drawFlowIndicator(
                context, 
                x + 10, craftY + 42,
                0, consumptionRate
            );
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
            
            // 使用 GuiRenderHelper 渲染按钮状态
            GuiRenderHelper.ButtonState btnState = selected 
                ? GuiRenderHelper.ButtonState.PRESSED 
                : hovered 
                    ? GuiRenderHelper.ButtonState.HOVERED 
                    : GuiRenderHelper.ButtonState.NORMAL;
            
            if (selected || hovered) {
                int bgColor = selected ? 0x8055FF55 : 0x40555555;
                context.fill(listX + 1, itemStartY, listX + listWidth - 1, itemStartY + itemHeight, bgColor);
            }
            
            // 配方名称
            Text recipeName = Text.translatable("factorcraft.recipe." + recipe.id(), recipe.id());
            context.drawTextWithShadow(this.textRenderer, recipeName, listX + 4, itemStartY + 2, 0xFFFFFF);
            
            // Factor 成本（带颜色编码）
            double factorCost = recipe.factorCost();
            int costColor = factorCost < 100 ? 0x55FF55 : factorCost < 500 ? 0xFFFF55 : 0xFF5555;
            Text costText = Text.literal(String.format("F: %.0f", factorCost));
            context.drawTextWithShadow(this.textRenderer, costText, listX + 4, itemStartY + 12, costColor);
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
    
    private void drawStatusIndicator(DrawContext context) {
        // 确定机器状态
        GuiRenderHelper.MachineStatus status;
        String detail = "";
        
        if (!handler.isStructureValid()) {
            status = GuiRenderHelper.MachineStatus.ERROR;
            detail = "结构不完整";
        } else if (handler.isCrafting()) {
            double progress = handler.getCraftProgressPercentage();
            if (progress >= 100) {
                status = GuiRenderHelper.MachineStatus.COMPLETE;
                detail = "合成完成";
            } else {
                status = GuiRenderHelper.MachineStatus.WORKING;
                detail = String.format("合成中: %.0f%%", progress);
            }
        } else if (handler.getBufferPercentage() < 10) {
            status = GuiRenderHelper.MachineStatus.WARNING;
            detail = "Factor 不足";
        } else {
            status = GuiRenderHelper.MachineStatus.IDLE;
            detail = "等待配方";
        }
        
        // 渲染状态指示器
        GuiRenderHelper.drawStatusText(context, x + 10, y + HEIGHT - 25, status, detail);
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
    
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // 不使用纹理背景
    }
}